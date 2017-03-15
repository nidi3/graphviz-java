package guru.nidi.graphviz.engine;

import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGException;
import com.kitfox.svg.SVGUniverse;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import static guru.nidi.graphviz.engine.Format.SVG;
import static guru.nidi.graphviz.engine.Format.SVG_STANDALONE;

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
        final String result = graphviz.execute(format == null || format == SVG_STANDALONE ? SVG : format);
        if (format == null || format == SVG) {
            return result.substring(result.indexOf("<svg "));
        }
        return result;
    }

    public void toFile(File file) throws IOException {
        if (format == null) {
            toFile(file, null);
        } else {
            try (final Writer out = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
                out.write(toString());
            }
        }
    }

    public void toFile(File file, String format) {
        BufferedImage image = toImage();
        final String f = format == null
                ? file.getName().substring(file.getName().lastIndexOf('.') + 1)
                : format;
        writeToFile(file, f, image);
    }

    public BufferedImage toImage() {
        if (format != null && format != SVG && format != SVG_STANDALONE) {
            throw new IllegalStateException("Images can only be rendered from SVG format.");
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
        final URI uri = universe.loadSVG(new StringReader(svg), "graph");
        final SVGDiagram diagram = universe.getDiagram(uri);
        diagram.setIgnoringClipHeuristic(true);
        return diagram;
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
