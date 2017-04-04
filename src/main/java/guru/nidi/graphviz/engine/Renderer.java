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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
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
        file.mkdirs();
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
        final String svg = graphviz.execute(SVG).replace("stroke=\"transparent\"", "stroke=\"#fff\" stroke-opacity=\"0.0\"");
        return graphviz.rasterizer.render(graphviz, graphicsConfigurer, svg);
    }

    private void writeToFile(File output, String format, BufferedImage img) {
        try {
            ImageIO.write(img, format, output);
        } catch (IOException e) {
            throw new GraphvizException("Problem writing to file", e);
        }
    }
}
