/*
 * Copyright (C) 2015 Stefan Niederhauser (nidin@gmx.ch)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
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
    private final Engine engineImpl;
    final Rasterizer rasterizer;
    final int width, height;
    final double scale;

    private Graphviz(String src, Engine engine, Rasterizer rasterizer, int width, int height, double scale) {
        this.src = src;
        this.engineImpl = engine;
        this.rasterizer = rasterizer;
        this.width = width;
        this.height = height;
        this.scale = scale;
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
        for (String name : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
            System.out.println(name);
        }
    }

    String execute(Format format) {
        return engine.execute(src, engineImpl, format);
    }

    public static Graphviz fromString(String src) {
        return new Graphviz(src, Engine.DOT, Rasterizer.BATIK, 0, 0, 1);
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
        return new Graphviz(src, engine, rasterizer, width, height, scale);
    }

    public Graphviz rasterizer(Rasterizer rasterizer) {
        return new Graphviz(src, engineImpl, rasterizer, width, height, scale);
    }

    public Graphviz width(int width) {
        return new Graphviz(src, engineImpl, rasterizer, width, height, 0);
    }

    public Graphviz height(int height) {
        return new Graphviz(src, engineImpl, rasterizer, width, height, 0);
    }

    public Graphviz scale(double scale) {
        return new Graphviz(src, engineImpl, rasterizer, 0, 0, scale);
    }

    public Renderer render(Format format) {
        if (engine == null) {
            initEngine();
        }
        return new Renderer(this, format, null);
    }

}
