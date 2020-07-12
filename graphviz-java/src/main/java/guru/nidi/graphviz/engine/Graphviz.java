/*
 * Copyright © 2015 Stefan Niederhauser (nidin@gmx.ch)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package guru.nidi.graphviz.engine;

import guru.nidi.graphviz.attribute.validate.ValidatorMessage;
import guru.nidi.graphviz.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static guru.nidi.graphviz.attribute.validate.ValidatorFormat.UNKNOWN_FORMAT;
import static guru.nidi.graphviz.engine.GraphvizLoader.readAsString;
import static guru.nidi.graphviz.engine.Rasterizer.NONE;
import static java.lang.Double.parseDouble;
import static java.util.Arrays.asList;
import static java.util.regex.Pattern.CASE_INSENSITIVE;

public final class Graphviz {
    static {
        if (System.getProperty("java.awt.headless") == null) {
            System.setProperty("java.awt.headless", "true");
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(Graphviz.class);

    private static final List<GraphvizProcessor> DEFAULT_PROCESSORS =
            asList(new InvalidCharsRemover(), new SvgSizeAdjuster(), new SvgImagePathsRestorer());
    private static final Pattern DPI_PATTERN = Pattern.compile("\"?dpi\"?\\s*=\\s*\"?([0-9.]+)\"?", CASE_INSENSITIVE);

    @Nullable
    private static volatile List<GraphvizEngine> availableEngines;

    @Nullable
    private static volatile BlockingQueue<GraphvizEngine> engineQueue;
    @Nullable
    private static volatile GraphvizEngine engine;

    @Nullable
    private final MutableGraph graph;
    @Nullable
    private final String src;

    final Rasterizer rasterizer;
    final ProcessOptions processOptions;
    final Options options;
    private final List<GraphvizProcessor> processors;
    @Nullable
    private final Consumer<ValidatorMessage> messageConsumer;

    private Graphviz(@Nullable MutableGraph graph, @Nullable String src, ProcessOptions processOptions) {
        this(graph, src, Rasterizer.DEFAULT, processOptions, Options.create(), DEFAULT_PROCESSORS, null);
    }

    private Graphviz(@Nullable MutableGraph graph, @Nullable String src, Rasterizer rasterizer,
                     ProcessOptions processOptions, Options options,
                     List<GraphvizProcessor> processors, @Nullable Consumer<ValidatorMessage> messageConsumer) {
        this.graph = graph;
        this.src = src;
        this.rasterizer = rasterizer;
        this.processOptions = processOptions;
        this.options = options;
        this.processors = processors;
        this.messageConsumer = messageConsumer;
    }

    private static List<GraphvizEngine> availableEngines() {
        synchronized (Graphviz.class) {
            if (availableEngines == null) {
                availableEngines = new ArrayList<>();
                if (GraphvizCmdLineEngine.AVAILABLE) {
                    availableEngines.add(new GraphvizCmdLineEngine());
                }
                if (GraphvizV8Engine.AVAILABLE) {
                    availableEngines.add(new GraphvizV8Engine());
                }
                if (GraphvizJdkEngine.AVAILABLE) {
                    availableEngines.add(new GraphvizJdkEngine());
                }
                if (availableEngines.isEmpty()) {
                    LOG.warn("No GraphvizEngine is available."
                            + " Either add the needed dependencies on the classpath"
                            + " or explicitly use 'Graphviz.useEngine(new GraphvizServerEngine())'.");
                }
            }
            return availableEngines;
        }
    }

    public static void useDefaultEngines() {
        useEngine(availableEngines());
    }

    public static void useEngine(GraphvizEngine first, GraphvizEngine... rest) {
        final List<GraphvizEngine> engines = new ArrayList<>();
        engines.add(first);
        engines.addAll(asList(rest));
        useEngine(engines);
    }

    public static void useEngine(List<GraphvizEngine> engines) {
        if (engines.isEmpty()) {
            useDefaultEngines();
        } else {
            synchronized (Graphviz.class) {
                if (engineQueue == null) {
                    engineQueue = new ArrayBlockingQueue<>(1);
                } else {
                    try {
                        getEngine().close();
                    } catch (Exception e) {
                        //ignore
                    }
                }
            }
            engine = null;
            doUseEngine(engines);
        }
    }

    private static void doUseEngine(List<GraphvizEngine> engines) {
        if (engines.isEmpty()) {
            engineQueue.add(new ErrorGraphvizEngine());
        } else {
            engines.get(0).init(e -> engineQueue.add(e), e -> doUseEngine(engines.subList(1, engines.size())));
        }
    }

    private static GraphvizEngine getEngine() {
        if (engineQueue == null) {
            useDefaultEngines();
        }
        synchronized (Graphviz.class) {
            if (engine == null) {
                try {
                    engine = engineQueue.poll(120, TimeUnit.SECONDS);
                    if (engine == null) {
                        throw new GraphvizException("Initializing graphviz engine took too long.");
                    }
                    if (engine instanceof ErrorGraphvizEngine) {
                        throw new GraphvizException("None of the provided engines could be initialized.");
                    }
                } catch (InterruptedException e) {
                    //ignore
                }
            }
        }
        return engine;
    }

    public static void releaseEngine() {
        synchronized (Graphviz.class) {
            if (engine != null) {
                doReleaseEngine(engine);
            }
            if (engineQueue != null) {
                for (final GraphvizEngine engine : engineQueue) {
                    doReleaseEngine(engine);
                }
            }
        }
        engine = null;
        engineQueue = null;
    }

    private static void doReleaseEngine(GraphvizEngine engine) {
        try {
            engine.close();
        } catch (Exception e) {
            throw new GraphvizException("Problem closing engine", e);
        }
    }

    public static void noHeadless() {
        System.setProperty("java.awt.headless", "false");
    }

    public static Graphviz fromFile(File src) throws IOException {
        try (final InputStream in = new FileInputStream(src)) {
            return fromString(readAsString(in)).basedir(src.getAbsoluteFile().getParentFile());
        }
    }

    public static Graphviz fromGraph(Graph graph) {
        return fromGraph((MutableGraph) graph);
    }

    public static Graphviz fromGraph(MutableGraph graph) {
        return new Graphviz(graph, null, new ProcessOptions());
    }

    public static Graphviz fromString(String src) {
        return new Graphviz(null, src, new ProcessOptions().dpi(dpi(src)));
    }

    public Graphviz engine(Engine engine) {
        return new Graphviz(
                graph, src, rasterizer, processOptions, options.engine(engine), processors, messageConsumer);
    }

    public Graphviz totalMemory(@Nullable Integer totalMemory) {
        final Options opts = options.totalMemory(totalMemory);
        return new Graphviz(graph, src, rasterizer, processOptions, opts, processors, messageConsumer);
    }

    public Graphviz yInvert(@Nullable Boolean yInvert) {
        final Options opts = options.yInvert(yInvert);
        return new Graphviz(graph, src, rasterizer, processOptions, opts, processors, messageConsumer);
    }

    public Graphviz basedir(File basedir) {
        final Options opts = options.basedir(basedir);
        return new Graphviz(graph, src, rasterizer, processOptions, opts, processors, messageConsumer);
    }

    public Graphviz width(int width) {
        return new Graphviz(graph, src, rasterizer, processOptions.width(width), options, processors, messageConsumer);
    }

    public Graphviz height(int height) {
        return new Graphviz(
                graph, src, rasterizer, processOptions.height(height), options, processors, messageConsumer);
    }

    public Graphviz scale(double scale) {
        return new Graphviz(graph, src, rasterizer, processOptions.scale(scale), options, processors, messageConsumer);
    }

    public Graphviz preProcessor(GraphvizPreProcessor preProcessor) {
        return processor(preProcessor);
    }

    public Graphviz postProcessor(GraphvizPostProcessor postProcessor) {
        return processor(postProcessor);
    }

    public Graphviz processor(GraphvizProcessor processor) {
        final ArrayList<GraphvizProcessor> ps = new ArrayList<>(processors);
        ps.add(processor);
        return new Graphviz(graph, src, rasterizer, processOptions, options, ps, messageConsumer);
    }

    public Graphviz notValidating() {
        return new Graphviz(graph, src, rasterizer, processOptions, options, processors, null);
    }

    public Graphviz validating(Consumer<ValidatorMessage> messageConsumer) {
        return new Graphviz(graph, src, rasterizer, processOptions, options, processors, messageConsumer);
    }

    public Renderer rasterize(Rasterizer rasterizer) {
        if (rasterizer == NONE) {
            throw new IllegalArgumentException("The provided rasterizer implementation was not found."
                    + " Make sure that either 'guru.nidi.com.kitfox:svgSalamander' or"
                    + " 'org.apache.xmlgraphics:batik-rasterizer' is available on the classpath.");
        }
        final Options opts = options.format(rasterizer.format());
        final Graphviz g = new Graphviz(graph, src, rasterizer, processOptions, opts, processors, messageConsumer);
        return new Renderer(g, Format.PNG);
    }

    public Renderer render(Format format) {
        final Options opts = options.format(format);
        final Graphviz g = new Graphviz(graph, src, rasterizer, processOptions, opts, processors, messageConsumer);
        return new Renderer(g, format);
    }

    EngineResult execute() {
        final String source = src == null ? serializer().serialize(graph) : src;
        final ProcessOptions processOpts = processOptions.dpi(dpi(source));
        return new Graphviz(graph, source, rasterizer, processOpts, options, processors, messageConsumer).doExecute();
    }

    private Serializer serializer() {
        final Serializer serializer = new Serializer()
                .forEngine(options.engine.forValidator())
                //TODO can we parse the builtInRasterizer for the correct format?
                //TODO refactor all instanceof BuiltInRasterizer
                .forFormat(rasterizer instanceof BuiltInRasterizer ? UNKNOWN_FORMAT : options.format.forValidator());
        return messageConsumer == null ? serializer : serializer.validating(messageConsumer);
    }

    private static double dpi(String src) {
        final Matcher matcher = DPI_PATTERN.matcher(src);
        return matcher.find() ? parseDouble(matcher.group(1)) : 72;
    }

    private EngineResult doExecute() {
        return applyPostProcessors(options.format == Format.DOT
                ? EngineResult.fromString(src)
                : getEngine().execute(applyPreProcessors(src), options, rasterizer));
    }

    private String applyPreProcessors(String source) {
        String src = source;
        for (final GraphvizProcessor proc : processors) {
            src = proc.preProcess(src, options, processOptions);
        }
        return src;
    }

    private EngineResult applyPostProcessors(EngineResult result) {
        EngineResult res = result;
        for (final GraphvizProcessor proc : processors) {
            res = proc.postProcess(res, options, processOptions);
        }
        return res;
    }

    private static class ErrorGraphvizEngine implements GraphvizEngine {
        @Override
        public void init(Consumer<GraphvizEngine> onOk, Consumer<GraphvizEngine> onError) {
        }

        @Override
        public EngineResult execute(String src, Options options, Rasterizer rasterizer) {
            return EngineResult.fromString("");
        }

        @Override
        public void close() {
        }
    }
}
