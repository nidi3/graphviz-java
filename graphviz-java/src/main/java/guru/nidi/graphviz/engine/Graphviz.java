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

import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.MutableGraph;
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

import static guru.nidi.graphviz.engine.IoUtils.readStream;
import static java.lang.Double.parseDouble;
import static java.util.Arrays.asList;

public final class Graphviz {
    private static final Logger LOG = LoggerFactory.getLogger(Graphviz.class);

    private static final Pattern DPI_PATTERN = Pattern.compile("\"?dpi\"?\\s*=\\s*\"?([0-9.]+)\"?",
            Pattern.CASE_INSENSITIVE);
    private static final List<GraphvizEngine> AVAILABLE_ENGINES = availableEngines();

    @Nullable
    private static volatile BlockingQueue<GraphvizEngine> engineQueue;
    @Nullable
    private static volatile GraphvizEngine engine;

    @Nullable
    private final MutableGraph graph;
    private final String src;

    @Nullable
    final Rasterizer rasterizer;
    final ProcessOptions processOptions;
    private final Options options;
    private final List<GraphvizFilter> filters;

    private Graphviz(@Nullable MutableGraph graph, String src) {
        this(graph, src, Rasterizer.DEFAULT, new ProcessOptions().dpi(dpi(src)), Options.create(), new ArrayList<>());
    }

    private Graphviz(@Nullable MutableGraph graph, String src, @Nullable Rasterizer rasterizer,
                     ProcessOptions processOptions, Options options, List<GraphvizFilter> filters) {
        this.graph = graph;
        this.src = src;
        this.rasterizer = rasterizer;
        this.processOptions = processOptions;
        this.options = options;
        this.filters = filters;
    }

    private static List<GraphvizEngine> availableEngines() {
        final List<GraphvizEngine> engines = new ArrayList<>();
        if (GraphvizCmdLineEngine.AVAILABLE) {
            engines.add(new GraphvizCmdLineEngine());
        }
        if (GraphvizV8Engine.AVAILABLE) {
            engines.add(new GraphvizV8Engine());
        }
        engines.add(new GraphvizServerEngine());
        if (GraphvizJdkEngine.AVAILABLE) {
            engines.add(new GraphvizJdkEngine());
        }
        if (engines.size() == 1) {
            LOG.warn("Only GraphvizServerEngine is available." +
                    " If you want to use other engines, please put the needed dependencies on the classpath.");
        }
        return engines;
    }

    public static void useDefaultEngines() {
        useEngine(AVAILABLE_ENGINES);
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

    public static Graphviz fromFile(File src) throws IOException {
        try (final InputStream in = new FileInputStream(src)) {
            return fromString(readStream(in)).basedir(src.getAbsoluteFile().getParentFile());
        }
    }

    public static Graphviz fromGraph(Graph graph) {
        return fromGraph((MutableGraph) graph);
    }

    public static Graphviz fromGraph(MutableGraph graph) {
        return new Graphviz(graph, graph.toString());
    }

    public static Graphviz fromString(String src) {
        return new Graphviz(null, src);
    }

    public Graphviz engine(Engine engine) {
        return new Graphviz(graph, src, rasterizer, processOptions, options.engine(engine), filters);
    }

    public Graphviz totalMemory(@Nullable Integer totalMemory) {
        return new Graphviz(graph, src, rasterizer, processOptions, options.totalMemory(totalMemory), filters);
    }

    public Graphviz yInvert(@Nullable Boolean yInvert) {
        return new Graphviz(graph, src, rasterizer, processOptions, options.yInvert(yInvert), filters);
    }

    public Graphviz basedir(File basedir) {
        return new Graphviz(graph, src, rasterizer, processOptions, options.basedir(basedir), filters);
    }

    public Graphviz width(int width) {
        return new Graphviz(graph, src, rasterizer, processOptions.width(width), options, filters);
    }

    public Graphviz height(int height) {
        return new Graphviz(graph, src, rasterizer, processOptions.height(height), options, filters);
    }

    public Graphviz scale(double scale) {
        return new Graphviz(graph, src, rasterizer, processOptions.scale(scale), options, filters);
    }

    public Graphviz filter(GraphvizFilter filter) {
        final ArrayList<GraphvizFilter> fs = new ArrayList<>(filters);
        fs.add(filter);
        return new Graphviz(graph, src, rasterizer, processOptions, options, fs);
    }

    public Renderer rasterize(@Nullable Rasterizer rasterizer) {
        if (rasterizer == null) {
            throw new IllegalArgumentException("The provided rasterizer implementation was not found. "
                    + "Make sure that the batik-rasterizer or svg-salamander jar is available on the classpath.");
        }
        final Options opts = options.format(rasterizer.format());
        final Graphviz graphviz = new Graphviz(graph, src, rasterizer, processOptions, opts, filters);
        return new Renderer(graphviz, null, Format.PNG);
    }

    public Renderer render(Format format) {
        final Graphviz g = new Graphviz(graph, src, rasterizer, processOptions, options.format(format), filters);
        return new Renderer(g, null, format);
    }

    EngineResult execute() {
        final EngineResult result = options.format == Format.DOT
                ? EngineResult.fromString(src)
                : getEngine().execute(options.format.preProcess(src), options, rasterizer);
        EngineResult engineResult = options.format.postProcess(this, result);
        for (final GraphvizFilter filter : filters) {
            engineResult = filter.filter(options.format, engineResult);
        }
        return engineResult;
    }

    Format format() {
        return options.format;
    }

    private static double dpi(String src) {
        final Matcher matcher = DPI_PATTERN.matcher(src);
        return matcher.find() ? parseDouble(matcher.group(1)) : 72;
    }

    private static class ErrorGraphvizEngine implements GraphvizEngine {
        @Override
        public void init(Consumer<GraphvizEngine> onOk, Consumer<GraphvizEngine> onError) {
        }

        @Override
        public EngineResult execute(String src, Options options, @Nullable Rasterizer rasterizer) {
            return EngineResult.fromString("");
        }

        @Override
        public void close() {
        }
    }
}
