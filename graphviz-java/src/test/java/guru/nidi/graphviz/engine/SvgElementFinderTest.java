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

import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Node;
import org.junit.jupiter.api.Test;

import static guru.nidi.graphviz.engine.Format.SVG;
import static guru.nidi.graphviz.model.Factory.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SvgElementFinderTest {
    @Test
    void findGraph() {
        final Graph g = graph().graphAttr().with("class", "g").with(node("a"));
        final String svg = Graphviz.fromGraph(g).render(SVG).toString();
        final SvgElementFinder finder = new SvgElementFinder(svg);
        assertEquals("graph g", classAttr(finder.findGraph()));
    }

    @Test
    void findNode() {
        final Node a = node("a'").with("class", "aclass").with(Label.of("hula")).link("b");
        final String svg = Graphviz.fromGraph(graph().with(a)).render(SVG).toString();
        final SvgElementFinder finder = new SvgElementFinder(svg);
        assertEquals("node aclass", classAttr(finder.findNode("a'")));
        assertEquals("node aclass", classAttr(finder.findNode(a)));
    }

    @Test
    void findEdge() {
        final Node a = node("a'").with(Label.of("hula")).link(to(node("b")).with("class", "link"));
        final String svg = Graphviz.fromGraph(graph().with(a)).render(SVG).toString();
        final SvgElementFinder finder = new SvgElementFinder(svg);
        assertEquals("edge link", classAttr(finder.findLink("a'", "b")));
        assertEquals("edge link", classAttr(finder.findLink(a.links().get(0))));
        final String svg2 = Graphviz.fromGraph(graph().directed().with(a)).render(SVG).toString();
        assertEquals("edge link", classAttr(new SvgElementFinder(svg2).findLink("a'", "b")));
    }

    @Test
    void findCluster() {
        final Graph sub = graph("sub").cluster().graphAttr().with("class", "c").with(node("a"));
        final String svg = Graphviz.fromGraph(graph().with(sub)).render(SVG).toString();
        final SvgElementFinder finder = new SvgElementFinder(svg);
        assertEquals("cluster c", classAttr(finder.findCluster("sub")));
        assertEquals("cluster c", classAttr(finder.findCluster(sub)));
    }

    private String classAttr(org.w3c.dom.Node node) {
        return node.getAttributes().getNamedItem("class").getNodeValue();
    }
}
