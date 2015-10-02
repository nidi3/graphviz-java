package guru.nidi.graphviz;

import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGException;
import com.kitfox.svg.SVGUniverse;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;

/**
 *
 */
public class GraphViz {
    private final String dot;

    public GraphViz(String dot) {
        this.dot = dot;
    }

    public String createSvg() {
        return GraphvizEngine.execute(dot);
    }

    public void renderToGraphics(Graphics2D graphics) {
        renderToGraphics(createDiagram(), graphics);
    }

    public void renderToFile(File output, String format, int maxWidth, int maxHeight) {
        final SVGDiagram diagram = createDiagram();
        final double scale = Math.min(maxWidth / diagram.getWidth(), maxHeight / diagram.getHeight());
        final BufferedImage img = new BufferedImage((int) (scale * diagram.getWidth()), (int) (scale * diagram.getHeight()), BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = img.createGraphics();
        g.scale(scale, scale);
        renderToGraphics(diagram, g);
        try {
            ImageIO.write(img, format, output);
        } catch (IOException e) {
            throw new GraphvizException("Problem writing to file", e);
        }
    }

    private void renderToGraphics(SVGDiagram diagram, Graphics2D graphics) {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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
