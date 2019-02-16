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

import guru.nidi.graphviz.model.*;

import javax.annotation.Nullable;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

public final class Graphviz {
    @Nullable
    private static volatile BlockingQueue<GraphvizEngine> engineQueue;
    @Nullable
    private static GraphvizEngine engine;
    private final String src;
    private final Options options;
    @Nullable
    final Rasterizer rasterizer;
    final int width;
    final int height;
    final double scale;
    final double fontAdjust;

    private Graphviz(String src, @Nullable Rasterizer rasterizer,
                     int width, int height, double scale, double fontAdjust, Options options) {
        this.src = src;
        this.rasterizer = rasterizer;
        this.width = width;
        this.height = height;
        this.scale = scale;
        this.fontAdjust = fontAdjust;
        this.options = options;
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
        GraphvizV8Engine.releaseThread(); //TODO remove this hard coupling
        if (engine != null) {
            try {
                engine.close();
            } catch (Exception e) {
                throw new GraphvizException("Problem closing engine", e);
            }
        }
        engine = null;
        engineQueue = null;
    }

    public static Graphviz fromString(String src) {
        return new Graphviz(src, Rasterizer.DEFAULT, 0, 0, 1, 1, Options.create());
    }

    public static Graphviz fromFile(File src) throws IOException {
        try (final InputStream in = new FileInputStream(src)) {
            return fromString(IoUtils.readStream(in)).basedir(src.getParentFile());
        }
    }

    public static Graphviz fromGraph(Graph graph) {
        return fromGraph((MutableGraph) graph);
    }

    public static Graphviz fromGraph(MutableGraph graph) {
        return fromString(new Serializer(graph).serialize());
    }

    public Graphviz engine(Engine engine) {
        return new Graphviz(src, rasterizer, width, height, scale, fontAdjust, options.engine(engine));
    }

    public Graphviz totalMemory(@Nullable Integer totalMemory) {
        return new Graphviz(src, rasterizer, width, height, scale, fontAdjust, options.totalMemory(totalMemory));
    }

    public Graphviz yInvert(@Nullable Boolean yInvert) {
        return new Graphviz(src, rasterizer, width, height, scale, fontAdjust, options.yInvert(yInvert));
    }

    public Graphviz basedir(File basedir) {
        return new Graphviz(src, rasterizer, width, height, scale, fontAdjust, options.basedir(basedir));
    }

    public Graphviz width(int width) {
        return new Graphviz(src, rasterizer, width, height, scale, fontAdjust, options);
    }

    public Graphviz height(int height) {
        return new Graphviz(src, rasterizer, width, height, scale, fontAdjust, options);
    }

    public Graphviz scale(double scale) {
        return new Graphviz(src, rasterizer, width, height, scale, fontAdjust, options);
    }

    public Graphviz fontAdjust(double fontAdjust) {
        return new Graphviz(src, rasterizer, width, height, scale, fontAdjust, options);
    }

    public Renderer rasterize(@Nullable Rasterizer rasterizer) {
        if (rasterizer == null) {
            throw new IllegalArgumentException("The provided rasterizer implementation was not found. "
                    + "Make sure that the batik-rasterizer or svg-salamander jar is available on the classpath.");
        }
        final Options opts = options.format(rasterizer.format());
        final Graphviz graphviz = new Graphviz(src, rasterizer, width, height, scale, fontAdjust, opts);
        return new Renderer(graphviz, null, Format.PNG);
    }

    public Renderer render(Format format) {
        final Graphviz g = new Graphviz(src, rasterizer, width, height, scale, fontAdjust, options.format(format));
        return new Renderer(g, null, format);
    }

    String execute() {
        return options.format.postProcess(getEngine().execute(src, options), fontAdjust);
    }

    Format format() {
        return options.format;
    }

    private static class ErrorGraphvizEngine implements GraphvizEngine {
        @Override
        public void init(Consumer<GraphvizEngine> onOk, Consumer<GraphvizEngine> onError) {
        }

        @Override
        public String execute(String src, Options options) {
            return "";
        }

        @Override
        public void close() {
        }
    }
}