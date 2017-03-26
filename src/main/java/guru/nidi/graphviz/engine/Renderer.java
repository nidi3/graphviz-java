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

import com.kitfox.svg.*;
import com.kitfox.svg.animation.AnimationElement;
import com.kitfox.svg.xml.StyleAttribute;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import static guru.nidi.graphviz.engine.Format.*;

public class Renderer {
    private final Graphviz graphviz;
    private final Format format;
    private final Consumer<Graphics2D> graphicsConfigurer;

    Renderer(Graphviz graphviz, Format format, Consumer<Graphics2D> graphicsConfigurer) {
        this.graphviz = graphviz;
        this.format = format;
        this.graphicsConfigurer = graphicsConfigurer;
    }

    public Renderer withGraphics(Consumer<Graphics2D> graphicsConfigurer) {
        return new Renderer(graphviz, format, graphicsConfigurer);
    }

    public String toString() {
        final String result = graphviz.execute(format == PNG || format == SVG_STANDALONE ? SVG : format);
        if (format == PNG || format == SVG) {
            return result.substring(result.indexOf("<svg "));
        }
        return result;
    }

    public void toFile(File file) throws IOException {
        if (format == PNG) {
            writeToFile(file, "png", toImage());
        } else {
            try (final Writer out = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
                out.write(toString());
            }
        }
    }

    public BufferedImage toImage() {
        if (format != PNG && format != SVG && format != SVG_STANDALONE) {
            throw new IllegalStateException("Images can only be rendered from PNG and SVG formats.");
        }
        final SVGDiagram diagram = createDiagram(graphviz.execute(SVG));
        double scaleX = graphviz.scale;
        double scaleY = graphviz.scale;
        if (graphviz.width != 0 || graphviz.height != 0) {
            scaleX = 1D * graphviz.width / diagram.getWidth();
            scaleY = 1D * graphviz.height / diagram.getHeight();
            if (scaleX == 0) {
                scaleX = scaleY;
            }
            if (scaleY == 0) {
                scaleY = scaleX;
            }
        }
        final BufferedImage image = new BufferedImage((int) Math.ceil(scaleX * diagram.getWidth()), (int) Math.ceil(scaleY * diagram.getHeight()), BufferedImage.TYPE_INT_ARGB);
        final Graphics2D graphics = image.createGraphics();
        configGraphics(graphics);
        if (graphicsConfigurer != null) {
            graphicsConfigurer.accept(graphics);
        }
        graphics.scale(scaleX, scaleY);
        renderDiagram(diagram, graphics);
        return image;
    }

    private SVGDiagram createDiagram(String svg) {
        final SVGUniverse universe = new SVGUniverse();
        final URI uri = universe.loadSVG(new StringReader(svg), "//graph/");
        final SVGDiagram diagram = universe.getDiagram(uri);
        replaceTransparent(diagram.getRoot());
        diagram.setIgnoringClipHeuristic(true);
        return diagram;
    }

    private void replaceTransparent(SVGElement element) {
        final StyleAttribute stroke = element.getPresAbsolute("stroke");
        if (stroke != null && "transparent".equals(stroke.getStringValue())) {
            try {
                element.setAttribute("stroke", AnimationElement.AT_XML, "#fff");
                if (!element.hasAttribute("stroke-opacity", AnimationElement.AT_XML)) {
                    element.addAttribute("stroke-opacity", AnimationElement.AT_XML, "0.0");
                }
            } catch (SVGElementException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < element.getNumChildren(); i++) {
            replaceTransparent(element.getChild(i));
        }
    }

    private void renderDiagram(SVGDiagram diagram, Graphics2D graphics) {
        try {
            diagram.render(graphics);
        } catch (SVGException e) {
            throw new GraphvizException("Problem rendering SVG", e);
        }
    }

    private void writeToFile(File output, String format, BufferedImage img) {
        try {
            ImageIO.write(img, format, output);
        } catch (IOException e) {
            throw new GraphvizException("Problem writing to file", e);
        }
    }

    private void configGraphics(Graphics2D graphics) {
        graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

}
