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

import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGException;
import com.kitfox.svg.SVGUniverse;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.Serializer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;

public final class Graphviz {
    private static GraphvizEngine engine;
    private final String dot;
    private final int width, height;
    private final double scale;

    private Graphviz(String dot, int width, int height, double scale) {
        this.dot = dot;
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

    public static Graphviz fromString(String dot) {
        return new Graphviz(dot, 0, 0, 4);
    }

    public static Graphviz fromFile(File dot) throws IOException {
        try (final InputStream in = new FileInputStream(dot)) {
            return fromString(IoUtils.readStream(in));
        }
    }

    public static Graphviz fromGraph(Graph graph) {
        return fromGraph((MutableGraph) graph);
    }

    public static Graphviz fromGraph(MutableGraph graph) {
        return fromString(new Serializer(graph).serialize());
    }

    public Graphviz width(int width) {
        return new Graphviz(dot, width, height, 0);
    }

    public Graphviz height(int height) {
        return new Graphviz(dot, width, height, 0);
    }

    public Graphviz scale(double scale) {
        return new Graphviz(dot, 0, 0, scale);
    }

    public String createSvg() {
        if (engine == null) {
            initEngine();
        }
        return engine.execute(dot);
    }

    public void renderToGraphics(Graphics2D graphics) {
        renderToGraphics(createDiagram(), graphics);
    }

    public void renderToFile(File output) {
        renderToFile(output, null);
    }

    public void renderToFile(File output, String format) {
        final SVGDiagram diagram = createDiagram();
        double scaleX = scale;
        double scaleY = scale;
        if (width != 0 || height != 0) {
            scaleX = 1D * width / diagram.getWidth();
            scaleY = 1D * height / diagram.getHeight();
            if (scaleX == 0) {
                scaleX = scaleY;
            }
            if (scaleY == 0) {
                scaleY = scaleX;
            }
        }
        final BufferedImage img = new BufferedImage((int) Math.ceil(scaleX * diagram.getWidth()), (int) Math.ceil(scaleY * diagram.getHeight()), BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = img.createGraphics();
        g.scale(scaleX, scaleY);
        renderToGraphics(diagram, g);
        final String f = format == null
                ? output.getName().substring(output.getName().lastIndexOf('.') + 1)
                : format;
        writeToFile(output, f, img);
    }

    private void writeToFile(File output, String format, BufferedImage img) {
        try {
            ImageIO.write(img, format, output);
        } catch (IOException e) {
            throw new GraphvizException("Problem writing to file", e);
        }
    }

    private void renderToGraphics(SVGDiagram diagram, Graphics2D graphics) {
        graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        try {
            diagram.render(graphics);
        } catch (SVGException e) {
            throw new GraphvizException("Problem rendering SVG", e);
        }
    }

    private SVGDiagram createDiagram() {
        final SVGUniverse universe = new SVGUniverse();
        final URI uri = universe.loadSVG(new StringReader(createSvg()), "graph");
        final SVGDiagram diagram = universe.getDiagram(uri);
        diagram.setIgnoringClipHeuristic(true);
        return diagram;
    }
}
