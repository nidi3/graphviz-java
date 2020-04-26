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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.function.Consumer;

import static guru.nidi.graphviz.engine.Rasterizer.NONE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Locale.ENGLISH;

public class Renderer {
    private static final Consumer<Graphics2D> NOP_GRAPHICS_CONFIGURER = Graphics2D -> {
    };
    private final Graphviz graphviz;
    private final Consumer<Graphics2D> graphicsConfigurer;
    private final Format output;

    Renderer(Graphviz graphviz, Format output) {
        this(graphviz, NOP_GRAPHICS_CONFIGURER, output);
    }

    Renderer(Graphviz graphviz, Consumer<Graphics2D> graphicsConfigurer, Format output) {
        this.graphviz = graphviz;
        this.graphicsConfigurer = graphicsConfigurer;
        this.output = output;
    }

    public Renderer withGraphics(Consumer<Graphics2D> graphicsConfigurer) {
        return new Renderer(graphviz, graphicsConfigurer, output);
    }

    public String toString() {
        return execute().map(file -> {
            throw new IllegalArgumentException("Expected a String result, but found a File."
                    + " Use toFile / toImage instead of toString or use a different Rasterizer (not the built-in).");
        }, string -> string);
    }

    public File toFile(File file) throws IOException {
        return execute().mapIO(
                fileRes -> toFile(fileRes, file),
                string -> toFile(string, file));
    }

    private File toFile(File source, File target) throws IOException {
        final File out = withExt(target, getExt(source));
        Files.createDirectories(target.getAbsoluteFile().getParentFile().toPath());
        Files.copy(source.toPath(), out.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return out;
    }

    private File toFile(String content, File file) throws IOException {
        final File target = withExt(file, output.fileExtension);
        Files.createDirectories(file.getAbsoluteFile().getParentFile().toPath());
        if (output.image) {
            writeToFile(target, output.name().toLowerCase(ENGLISH), toImage(content));
        } else {
            try (final Writer out = new OutputStreamWriter(new FileOutputStream(target), UTF_8)) {
                out.write(content);
            }
        }
        return target;
    }

    private File withExt(File file, String ext) {
        return file.getName().contains(".") ? file : new File(file.getParentFile(), file.getName() + "." + ext);
    }

    private String getExt(File file) {
        return file.getName().substring(file.getName().lastIndexOf('.') + 1);
    }

    public void toOutputStream(OutputStream outputStream) throws IOException {
        execute().mapIO(
                file -> Files.copy(file.toPath(), outputStream),
                string -> toOutputStream(string, outputStream));
    }

    private long toOutputStream(String content, OutputStream outputStream) throws IOException {
        if (output.image) {
            writeToOutputStream(outputStream, output.name().toLowerCase(ENGLISH), toImage(content));
        } else {
            try (final Writer out = new OutputStreamWriter(outputStream, UTF_8)) {
                out.write(content);
            }
        }
        return 0;
    }

    public BufferedImage toImage() {
        return toImage(execute());
    }

    private BufferedImage toImage(EngineResult result) {
        return result.map(this::toImage, this::toImage);
    }

    private BufferedImage toImage(String content) {
        if (graphviz.rasterizer == NONE) {
            throw new IllegalStateException("- Rasterizer explicitly set no null or\n"
                    + "- neither 'guru.nidi.com.kitfox:svgSalamander' nor 'org.apache.xmlgraphics:batik-rasterizer'"
                    + " found on classpath.");
        }
        return graphviz.rasterizer.rasterize(graphviz, graphicsConfigurer, content);
    }

    private BufferedImage toImage(File file) {
        try {
            final BufferedImage image = ImageIO.read(file);
            if (image == null) {
                throw new IllegalArgumentException("Could not convert the resulting file into an Image");
            }
            return image;
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not convert the resulting file into an Image", e);
        }
    }

    private EngineResult execute() {
        return graphviz.execute();
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
