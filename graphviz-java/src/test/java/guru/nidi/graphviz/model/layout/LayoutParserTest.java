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

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.*;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.Rasterizer;
import guru.nidi.graphviz.model.*;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

import static guru.nidi.graphviz.attribute.GraphAttr.*;
import static guru.nidi.graphviz.engine.Format.SVG;
import static guru.nidi.graphviz.model.Factory.mutGraph;
import static guru.nidi.graphviz.model.Factory.mutNode;

public class LayoutParserTest {
    @Test
    void simple() throws IOException {
        final MutableGraph mg = mutGraph("ex7").use((g, ctx) -> {
            g.setCluster(true).graphAttrs().add(Color.RED.background())
                    .add(
                            mutGraph().setCluster(true)
                                    .nodeAttrs().add(Style.FILLED, Color.WHITE)
                                    .graphAttrs().add(Style.FILLED, Color.LIGHTGREY, Label.of("process #1"))
                                    .add(mutNode("a0").addLink(mutNode("a1").addLink(mutNode("a2").addLink(mutNode("a3"))))),
                            mutGraph("x").setCluster(true)
                                    .nodeAttrs().add(Style.FILLED)
                                    .graphAttrs().add(Color.BLUE, Label.of("process #2"))
                                    .add(mutNode("b0").addLink(mutNode("b1").addLink(mutNode("b2").addLink(mutNode("b3"))))),
                            mutNode("start").add(Shape.mDiamond("", "")).addLink("a0", "b0"),
                            mutNode("a1").addLink("b3"),
                            mutNode("b2").addLink("a3"),
                            mutNode("a3").addLink("a0"),
                            mutNode("a3").addLink("end"),
                            mutNode("b3").addLink("end"),
                            mutNode("end").add(Shape.mSquare("", ""))
                    );
        });
        final Collection<MutableNode> nodes = mg.nodes();
        for (int p = 0; p < 2; p++) {
            set(mg.graphAttrs(), p, pad(10 / 72.0));
            for (int m = 0; m < 2; m++) {
                set(mg.graphAttrs(), m, margin(20 / 72.0));
                for (int d = 0; d < 2; d++) {
                    set(mg.graphAttrs(), d, dpi(144));
                    final String f = "target/render-pad" + p + "-margin" + m + "-dpi" + d;
                    final Graphviz viz = Graphviz.fromGraph(mg);//.width(500);//scale -y option //TODO
                    viz.rasterize(Rasterizer.SALAMANDER).toFile(new File(f + "-sal"));
                    viz.rasterize(Rasterizer.BATIK).toFile(new File(f + "-bat"));
                    viz.rasterize(Rasterizer.builtIn("png")).toFile(new File(f + "-nat"));
                    System.out.println(mg);
                    Graphviz.fromGraph(mg).parseLayout(true).render(SVG).toFile(new File(f + "-svg"));
                    System.out.println(mg);
//                    ImageIO.write(renderOutline(mg), "png", new File(f + "-out.png"));
                }
            }
        }

    }

    <F extends For> void set(MutableAttributed<?, F> attrs, int val, Attributes<F> attr) {
        if (val == 0) {
            final String key = attr.iterator().next().getKey();
            attrs.add(key, null);
        } else {
            attrs.add(attr);
        }
    }

    BufferedImage renderOutline(MutableGraph g) {
        final int width = LayoutAttributes.widthOf(g);
        final int height = LayoutAttributes.heightOf(g);
        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D gr = image.createGraphics();
        gr.setColor(java.awt.Color.BLACK);
        gr.draw(LayoutAttributes.outlineOf(g).toShape());
        for (final MutableNode node : g.nodes()) {
            gr.draw(LayoutAttributes.outlineOf(node).toShape());
        }
        for (final MutableGraph graph : g.graphs()) {
            gr.draw(LayoutAttributes.outlineOf(graph).toShape());
        }
        for (final Link edge : g.edges()) {
            gr.draw(LayoutAttributes.outlineOf(edge).toShape());
        }
        return image;
    }
}
