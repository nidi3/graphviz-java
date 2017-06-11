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
import guru.nidi.graphviz.model.Serializer;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class Graphviz {
    private static volatile BlockingQueue<GraphvizEngine> engineQueue;
    private static GraphvizEngine engine;
    private final String src;
    private final Options options;
    final Rasterizer rasterizer;
    final int width;
    final int height;
    final double scale;

    private Graphviz(String src, Rasterizer rasterizer, int width, int height, double scale, Options options) {
        this.src = src;
        this.rasterizer = rasterizer;
        this.width = width;
        this.height = height;
        this.scale = scale;
        this.options = options;
    }

    public static void useEngine(GraphvizEngine first, GraphvizEngine... engines) {
        synchronized (Graphviz.class) {
            if (engineQueue == null) {
                engineQueue = new ArrayBlockingQueue<>(1);
            } else {
                try {
                    getEngine().release();
                } catch (GraphvizException e) {
                    //ignore
                }
            }
        }
        engine = null;
        first.init(e -> engineQueue.add(e), e -> useEngine(Arrays.asList(engines)));
    }

    private static void useEngine(List<GraphvizEngine> engines) {
        if (engines.isEmpty()) {
            engineQueue.add(new ErrorGraphvizEngine());
        } else {
            engines.get(0).init(e -> engineQueue.add(e), e -> useEngine(engines.subList(1, engines.size())));
        }
    }

    public static void initEngine() {
        useEngine(new GraphvizCmdLineEngine(), new GraphvizV8Engine(),
                new GraphvizServerEngine(), new GraphvizJdkEngine());
    }

    private static GraphvizEngine getEngine() {
        if (engineQueue == null) {
            initEngine();
        }
        synchronized (Graphviz.class) {
            if (engine == null) {
                try {
                    engine = engineQueue.poll(60, TimeUnit.SECONDS);
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
        if (engine != null) {
            engine.release();
        }
        engine = null;
        engineQueue = null;
    }

    public static void printFontNames() {
        for (final String name : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
            System.out.println(name);
        }
    }

    public static Graphviz fromString(String src) {
        return new Graphviz(src, Rasterizer.BATIK, 0, 0, 1, Options.create());
    }

    public static Graphviz fromFile(File src) throws IOException {
        try (final InputStream in = new FileInputStream(src)) {
            return fromString(IoUtils.readStream(in));
        }
    }

    public static Graphviz fromGraph(Graph graph) {
        return fromGraph((MutableGraph) graph);
    }

    public static Graphviz fromGraph(MutableGraph graph) {
        return fromString(new Serializer(graph).serialize());
    }

    public Graphviz engine(Engine engine) {
        return new Graphviz(src, rasterizer, width, height, scale, options.engine(engine));
    }

    public Graphviz totalMemory(Integer totalMemory) {
        return new Graphviz(src, rasterizer, width, height, scale, options.totalMemory(totalMemory));
    }

    public Graphviz rasterizer(Rasterizer rasterizer) {
        return new Graphviz(src, rasterizer, width, height, scale, options);
    }

    public Graphviz width(int width) {
        return new Graphviz(src, rasterizer, width, height, scale, options);
    }

    public Graphviz height(int height) {
        return new Graphviz(src, rasterizer, width, height, scale, options);
    }

    public Graphviz scale(double scale) {
        return new Graphviz(src, rasterizer, width, height, scale, options);
    }

    public Renderer render(Format format) {
        final Graphviz graphviz = new Graphviz(src, rasterizer, width, height, scale, options.format(format));
        return new Renderer(graphviz, null);
    }

    String execute() {
        return options.format.postProcess(getEngine().execute(src, options));
    }

    Format format() {
        return options.format;
    }


    private static class ErrorGraphvizEngine implements GraphvizEngine {
        public void init(Consumer<GraphvizEngine> onOk, Consumer<GraphvizEngine> onError) {
        }

        public String execute(String src, Options options) {
            return null;
        }

        public void release() {
        }
    }
}