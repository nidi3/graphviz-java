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
package guru.nidi.graphviz.model.layout;

import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import static guru.nidi.graphviz.attribute.GraphAttr.*;
import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;

public final class LocatorDemo {
    private LocatorDemo() {
    }

    public static void main(String[] args) {
        final JLabel info = new JLabel("Outside");
        final MutableGraph graph = createGraph();
        final BufferedImage image = Graphviz.fromGraph(graph).parseLayout(true).render(Format.PNG).toImage();
        final BufferedImage outline = createOutlineImage(graph);
        final ImagePanel picture = createPicture(image, new LayoutLocator(graph, 1), info);

        final JFrame frame = new JFrame("Locator test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(500, 500));

        final Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(createActions(e -> picture.setImage(image), e -> picture.setImage(outline)), BorderLayout.NORTH);
        contentPane.add(picture, BorderLayout.CENTER);
        contentPane.add(info, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
    }

    private static ImagePanel createPicture(BufferedImage image, LayoutLocator locator, JLabel output) {
        final ImagePanel picture = new ImagePanel(image);
        picture.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                SwingUtilities.invokeLater(() -> {
                    final Point point = picture.toImageContext(e.getPoint());
                    locator.useElementAt(point.x, point.y, g -> {
                        output.setText(g.name().toString());
                    }, n -> {
                        output.setText(n.name().toString());
                    }, l -> {
                        output.setText(l.from().name().toString() + "-" + l.to().name().toString());
                    }, () -> {
                        output.setText("Outside");
                    });
                });
            }
        });
        return picture;
    }

    private static JPanel createActions(ActionListener imageClick, ActionListener outlineClick) {
        final JPanel actions = new JPanel();
        final JButton acImage = new JButton("Image");
        acImage.addActionListener(imageClick);
        actions.add(acImage);
        final JButton acOutline = new JButton("Outline");
        acOutline.addActionListener(outlineClick);
        actions.add(acOutline);
        return actions;
    }

    private static BufferedImage createOutlineImage(MutableGraph graph) {
        final int width = LayoutAttributes.widthOf(graph);
        final int height = LayoutAttributes.heightOf(graph);
        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = image.createGraphics();
        g.fillRect(0, 0, width, height);
        g.setColor(java.awt.Color.BLACK);
        g.draw(LayoutAttributes.outlineOf(graph).toShape());
        for (final MutableGraph sub : graph.graphs()) {
            g.draw(LayoutAttributes.outlineOf(sub).toShape());
        }
        for (final MutableNode node : graph.nodes()) {
            g.draw(LayoutAttributes.outlineOf(node).toShape());
        }
        for (final Link edge : graph.edges()) {
            g.draw(LayoutAttributes.outlineOf(edge).toShape());
        }
        return image;
    }

    static MutableGraph createGraph() {
        return graph("ex7").cluster().graphAttr().with(margin(0), pad(10 / 72.0)).with(
                graph("proc 1").cluster()
                        .nodeAttr().with(Style.FILLED, guru.nidi.graphviz.attribute.Color.WHITE)
                        .graphAttr().with(Style.FILLED, guru.nidi.graphviz.attribute.Color.LIGHTGREY, guru.nidi.graphviz.attribute.Label.of("process #1"))
                        .with(node("a0").link(node("a1").link(node("a2").link(node("a3"))))),
                graph("proc 2").cluster()
                        .nodeAttr().with(Style.FILLED)
                        .graphAttr().with(guru.nidi.graphviz.attribute.Color.BLUE, guru.nidi.graphviz.attribute.Label.of("process #2"))
                        .with(node("b0").link(node("b1").link(node("b2").link(node("b3"))))),
                node("start").with(guru.nidi.graphviz.attribute.Shape.mDiamond("", "")).link("a0", "b0"),
                node("a1").link("b3"),
                node("b2").link("a3"),
                node("a3").link("a0"),
                node("a3").link("end"),
                node("b3").link("end"),
                node("end").with(Shape.mSquare("", ""))
        ).toMutable();
    }
}

class ImagePanel extends JPanel {
    private BufferedImage img;

    ImagePanel(BufferedImage img) {
        this.img = img;
    }

    void setImage(BufferedImage img) {
        this.img = img;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return img == null ? super.getPreferredSize() : new Dimension(img.getWidth(), img.getHeight());
    }

    protected Point getImageLocation() {
        final int x = (getWidth() - img.getWidth()) / 2;
        final int y = (getHeight() - img.getHeight()) / 2;
        return new Point(x, y);
    }

    public Point toImageContext(Point p) {
        final Point imgLocation = getImageLocation();
        final Point relative = new Point(p);
        relative.x -= imgLocation.x;
        relative.y -= imgLocation.y;
        return relative;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        final Point p = getImageLocation();
        g.drawImage(img, p.x, p.y, this);
    }
}
