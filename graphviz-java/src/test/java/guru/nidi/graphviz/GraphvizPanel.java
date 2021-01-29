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
package guru.nidi.graphviz;

import guru.nidi.graphviz.engine.*;
import guru.nidi.graphviz.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static guru.nidi.graphviz.engine.Format.PNG;
import static java.util.stream.Collectors.toList;

class GraphvizPanel extends JPanel {
    private final Dimension size;
    private final BufferedImage img;
    private double scaleX = 1, scaleY = 1;
    private List<Map.Entry<Rectangle, String>> boxes;

    GraphvizPanel(Graph g, Dimension size) {
        this.size = size;
        img = Graphviz.fromGraph(g).postProcessor((EngineResult source, Options options, ProcessOptions processOptions) -> {
            try {
                final String svg = source.asString();
                final GraphElementFinder finder = new SvgElementFinder(svg).fromGraph(g);
                final SvgShapeAnalyzer analyzer = new SvgShapeAnalyzer(finder, size.width, size.height);
                boxes = finder.findNodes().stream()
                        .map(node -> {
                            final String tooltip = (String) finder.nodeOf(node).attrs().get("tooltip");
                            return tooltip == null ? null : new AbstractMap.SimpleEntry<>(analyzer.getBoundingBox(node), tooltip);
                        })
                        .filter(Objects::nonNull)
                        .collect(toList());
                return EngineResult.fromString(svg);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).width(size.width).height(size.height).render(PNG).toImage();
        ToolTipManager.sharedInstance().registerComponent(this);
    }

    @Override
    public Dimension getPreferredSize() {
        return size;
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        for (final Map.Entry<Rectangle, String> box : boxes) {
            if (box.getKey().contains(e.getX() / scaleX, e.getY() / scaleY)) {
                return box.getValue();
            }
        }
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        final Rectangle c = g.getClipBounds();
        scaleX = c.getWidth() / size.width;
        scaleY = c.getHeight() / size.height;
        g.drawImage(img, 0, 0, c.width, c.height, null);
//        for (Map.Entry<Rectangle, String> box : boxes) {
//            g.drawRect(box.getKey().x, box.getKey().y, box.getKey().width, box.getKey().height);
//        }
    }
}
