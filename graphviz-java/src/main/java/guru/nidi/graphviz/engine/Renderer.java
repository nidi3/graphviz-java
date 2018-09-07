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

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.function.Consumer;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Locale.ENGLISH;

public class Renderer {
    private final Graphviz graphviz;
    @Nullable
    private final Consumer<Graphics2D> graphicsConfigurer;
    private final Format output;

    Renderer(Graphviz graphviz, @Nullable Consumer<Graphics2D> graphicsConfigurer, Format output) {
        this.graphviz = graphviz;
        this.graphicsConfigurer = graphicsConfigurer;
        this.output = output;
    }

    public Renderer withGraphics(Consumer<Graphics2D> graphicsConfigurer) {
        return new Renderer(graphviz, graphicsConfigurer, output);
    }

    public String toString() {
        return graphviz.execute();
    }

    public void toFile(File file) throws IOException {
        Files.createDirectories(file.getAbsoluteFile().getParentFile().toPath());
        final File target = file.getName().contains(".")
                ? file
                : new File(file.getParentFile(), file.getName() + "." + output.fileExtension);
        if (output.image) {
            writeToFile(target, output.name().toLowerCase(ENGLISH), toImage());
        } else {
            try (final Writer out = new OutputStreamWriter(new FileOutputStream(target), UTF_8)) {
                out.write(toString());
            }
        }
    }

    public void toOutputStream(OutputStream outputStream) throws IOException {
        if (output.image) {
            writeToOutputStream(outputStream, output.name().toLowerCase(ENGLISH), toImage());
        } else {
            try (final Writer out = new OutputStreamWriter(outputStream, UTF_8)) {
                out.write(toString());
            }
        }
    }

    public BufferedImage toImage() {
        if (graphviz.rasterizer == null) {
            throw new IllegalStateException("- Rasterizer explicitly set no null or\n"
                    + "- neither Batik nor Salamander found on classpath.");
        }
        return graphviz.rasterizer.rasterize(graphviz, graphicsConfigurer, graphviz.execute());
    }

    private void writeToFile(File output, String format, BufferedImage img) {
        try {
            ImageIO.write(img, format, output);
        } catch (IOException e) {
            throw new GraphvizException("Problem writing to file", e);
        }
    }

    private void writeToOutputStream(OutputStream outputStream, String format, BufferedImage img) {
        try (final OutputStream closing = outputStream) {
            ImageIO.write(img, format, closing);
        } catch (IOException e) {
            throw new GraphvizException("Problem writing to output stream", e);
        }
    }
}
