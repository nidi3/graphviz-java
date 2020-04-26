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

import com.kitfox.svg.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.StringReader;
import java.net.URI;
import java.util.function.Consumer;

import static java.awt.RenderingHints.*;

class SalamanderRasterizer extends SvgRasterizer {
    @Override
    public BufferedImage doRasterize(Graphviz graphviz, Consumer<Graphics2D> graphicsConfigurer, String svg) {
        final SVGDiagram diagram = createDiagram(svg);
        final BufferedImage image = new BufferedImage(
                (int) diagram.getWidth(), (int) diagram.getHeight(), BufferedImage.TYPE_INT_ARGB);
        final Graphics2D graphics = image.createGraphics();
        configGraphics(graphics);
        graphicsConfigurer.accept(graphics);
        renderDiagram(diagram, graphics);
        return image;
    }

    private SVGDiagram createDiagram(String svg) {
        final SVGUniverse universe = new SVGUniverse();
        final URI uri = universe.loadSVG(new StringReader(svg), "//graph/");
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

    private void configGraphics(Graphics2D graphics) {
        graphics.setRenderingHint(KEY_ALPHA_INTERPOLATION, VALUE_ALPHA_INTERPOLATION_QUALITY);
        graphics.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(KEY_COLOR_RENDERING, VALUE_COLOR_RENDER_QUALITY);
        graphics.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BICUBIC);
        graphics.setRenderingHint(KEY_RENDERING, VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON);
    }

}
