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

public final class Graphviz {
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

    public static void useEngine(GraphvizEngine engine) {
        Graphviz.engine = engine;
    }

    public static void initEngine() {
        engine = new GraphvizV8Engine(e ->
                engine = new GraphvizServerEngine(e1 ->
                        engine = new GraphvizJdkEngine()));
    }

    public static void releaseEngine() {
        if (engine != null) {
            engine.release();
        }
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

    public Graphviz totalMemory(int totalMemory) {
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
        if (engine == null) {
            initEngine();
        }
        final Graphviz graphviz = new Graphviz(src, rasterizer, width, height, scale, options.format(format));
        return new Renderer(graphviz, null);
    }

    String execute() {
        return options.format.postProcess(engine.execute(src, options));
    }

    Format format() {
        return options.format;
    }

}
