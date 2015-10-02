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
