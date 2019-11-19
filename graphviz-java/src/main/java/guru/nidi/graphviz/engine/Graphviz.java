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

import javax.annotation.Nullable;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static guru.nidi.graphviz.engine.IoUtils.readStream;

public final class Graphviz {
    private static final Pattern DPI_PATTERN = Pattern.compile("\"?dpi\"?\\s*=\\s*\"?([0-9.]+)\"?",
            Pattern.CASE_INSENSITIVE);

    @Nullable
    private static volatile BlockingQueue<GraphvizEngine> engineQueue;
    @Nullable
    private static volatile GraphvizEngine engine;

    private final String src;
    @Nullable
    final Rasterizer rasterizer;
    final int width;
    final int height;
    final double scale;
    final double fontAdjust;
    private final Options options;
    private final List<GraphvizFilter> filters;

    private Graphviz(String src, @Nullable Rasterizer rasterizer,
                     int width, int height, double scale, double fontAdjust,
                     Options options, List<GraphvizFilter> filters) {
        this.src = src;
        this.rasterizer = rasterizer;
        this.width = width;
        this.height = height;
        this.scale = scale;
        this.fontAdjust = fontAdjust;
        this.options = options;
        this.filters = filters;
    }

    public static void useDefaultEngines() {
        useEngine(new GraphvizCmdLineEngine(), new GraphvizV8Engine(),
                new GraphvizServerEngine(), new GraphvizJdkEngine());
    }

    public static void useEngine(GraphvizEngine first, GraphvizEngine... rest) {
        final List<GraphvizEngine> engines = new ArrayList<>();
        engines.add(first);
        engines.addAll(Arrays.asList(rest));
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
        return fromString(graph.toString());
    }

    public static Graphviz fromString(String src) {
        return new Graphviz(src, Rasterizer.DEFAULT, 0, 0, 1, 1, Options.create(), new ArrayList<>());
    }

    public Graphviz engine(Engine engine) {
        return new Graphviz(src, rasterizer, width, height, scale, fontAdjust, options.engine(engine), filters);
    }

    public Graphviz totalMemory(@Nullable Integer totalMemory) {
        return new Graphviz(
                src, rasterizer, width, height, scale, fontAdjust, options.totalMemory(totalMemory), filters);
    }

    public Graphviz yInvert(@Nullable Boolean yInvert) {
        return new Graphviz(src, rasterizer, width, height, scale, fontAdjust, options.yInvert(yInvert), filters);
    }

    public Graphviz basedir(File basedir) {
        return new Graphviz(src, rasterizer, width, height, scale, fontAdjust, options.basedir(basedir), filters);
    }

    public Graphviz width(int width) {
        return new Graphviz(src, rasterizer, width, height, scale, fontAdjust, options, filters);
    }

    public Graphviz height(int height) {
        return new Graphviz(src, rasterizer, width, height, scale, fontAdjust, options, filters);
    }

    public Graphviz scale(double scale) {
        return new Graphviz(src, rasterizer, width, height, scale, fontAdjust, options, filters);
    }

    public Graphviz fontAdjust(double fontAdjust) {
        return new Graphviz(src, rasterizer, width, height, scale, fontAdjust, options, filters);
    }

    public Graphviz filter(GraphvizFilter filter) {
        final ArrayList<GraphvizFilter> fs = new ArrayList<>(filters);
        fs.add(filter);
        return new Graphviz(src, rasterizer, width, height, scale, fontAdjust, options, fs);
    }

    public Renderer rasterize(@Nullable Rasterizer rasterizer) {
        if (rasterizer == null) {
            throw new IllegalArgumentException("The provided rasterizer implementation was not found. "
                    + "Make sure that the batik-rasterizer or svg-salamander jar is available on the classpath.");
        }
        final Options opts = options.format(rasterizer.format());
        final Graphviz graphviz = new Graphviz(src, rasterizer, width, height, scale, fontAdjust, opts, filters);
        return new Renderer(graphviz, null, Format.PNG);
    }

    public Renderer render(Format format) {
        final Graphviz g =
                new Graphviz(src, rasterizer, width, height, scale, fontAdjust, options.format(format), filters);
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

    double dpi() {
        final Matcher matcher = DPI_PATTERN.matcher(src);
        return matcher.find() ? Double.parseDouble(matcher.group(1)) : 72;
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
