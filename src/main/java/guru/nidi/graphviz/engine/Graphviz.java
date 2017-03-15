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

import java.io.*;

public final class Graphviz {
    private static GraphvizEngine engine;
    final String src;
    final Engine engineImpl;
    final int width, height;
    final double scale;

    private Graphviz(String src, Engine engine, int width, int height, double scale) {
        this.src = src;
        this.engineImpl = engine;
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

    String execute(Format format) {
        return engine.execute(src, engineImpl, format);
    }

    public static Graphviz fromString(String src) {
        return new Graphviz(src, Engine.DOT, 0, 0, 2);
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
        return new Graphviz(src, engine, width, height, scale);
    }

    public Graphviz width(int width) {
        return new Graphviz(src, engineImpl, width, height, 0);
    }

    public Graphviz height(int height) {
        return new Graphviz(src, engineImpl, width, height, 0);
    }

    public Graphviz scale(double scale) {
        return new Graphviz(src, engineImpl, 0, 0, scale);
    }

    public Renderer render() {
        return render(null);
    }

    public Renderer render(Format format) {
        if (engine == null) {
            initEngine();
        }
        return new Renderer(this, format, null);
    }
}
