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
package guru.nidi.graphviz.model;

import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.engine.Graphviz;
import org.junit.jupiter.api.Test;

import static guru.nidi.graphviz.attribute.Attributes.attr;
import static guru.nidi.graphviz.engine.Format.SVG;
import static guru.nidi.graphviz.model.Factory.*;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SvgElementFinderTest {
    @Test
    void findGraph() {
        final Graph g = graph().graphAttr().with("class", "g").with(node("a"));
        final String svg = Graphviz.fromGraph(g).render(SVG).toString();
        final SvgElementFinder finder = new SvgElementFinder(svg);
        assertEquals("graph g", finder.findGraph().getAttribute("class"));
    }

    @Test
    void findNode() {
        final Node a = node("a'").with("class", "aclass").with(Label.of("hula")).link("b");
        final String svg = Graphviz.fromGraph(graph().with(a)).render(SVG).toString();
        final SvgElementFinder finder = new SvgElementFinder(svg);
        assertEquals("node aclass", finder.findNode("a'").getAttribute("class"));
        assertEquals("node aclass", finder.findNode(a).getAttribute("class"));
    }

    @Test
    void findNodes() {
        final Node a = node("a").with(attr("class", "aclass")).link(node("b").with("class", "bclass"));
        final String svg = Graphviz.fromGraph(graph().with(a)).render(SVG).toString();
        final SvgElementFinder finder = new SvgElementFinder(svg);
        assertEquals("node aclass", finder.findNodes().get(0).getAttribute("class"));
        assertEquals("node bclass", finder.findNodes().get(1).getAttribute("class"));
    }

    @Test
    void nodeNameOf() {
        final String svg = Graphviz.fromGraph(graph().with(node("a"))).render(SVG).toString();
        final SvgElementFinder finder = new SvgElementFinder(svg);
        assertEquals("a", SvgElementFinder.nodeNameOf(finder.findNodes().get(0)));
    }

    @Test
    void findEdge() {
        final Node a = node("a'").with(Label.of("hula")).link(to(node("b")).with("class", "link"));
        final String svg = Graphviz.fromGraph(graph().with(a)).render(SVG).toString();
        final SvgElementFinder finder = new SvgElementFinder(svg);
        assertEquals("edge link", finder.findLink("a'", "b").getAttribute("class"));
        assertEquals("edge link", finder.findLink(a.links().get(0)).getAttribute("class"));
        final String svg2 = Graphviz.fromGraph(graph().directed().with(a)).render(SVG).toString();
        assertEquals("edge link", new SvgElementFinder(svg2).findLink("a'", "b").getAttribute("class"));
    }

    @Test
    void findEdges() {
        final Node b = node("b");
        final Node a = node("a").link(to(b).with("class", "a-b"));
        final String svg = Graphviz.fromGraph(graph().with(a, b.link(to(a).with("class", "b-a")))).render(SVG).toString();
        final SvgElementFinder finder = new SvgElementFinder(svg);
        assertEquals("edge a-b", finder.findLinks().get(0).getAttribute("class"));
        assertEquals("edge b-a", finder.findLinks().get(1).getAttribute("class"));
    }

    @Test
    void linkedNodeNamesOf() {
        final String svg = Graphviz.fromGraph(graph().directed().with(node("a").link("b"))).render(SVG).toString();
        final SvgElementFinder finder = new SvgElementFinder(svg);
        assertEquals(asList("a", "b"), SvgElementFinder.linkedNodeNamesOf(finder.findLinks().get(0)));
    }

    @Test
    void findCluster() {
        final Graph sub = graph("sub").cluster().graphAttr().with("class", "c").with(node("a"));
        final String svg = Graphviz.fromGraph(graph().with(sub)).render(SVG).toString();
        final SvgElementFinder finder = new SvgElementFinder(svg);
        assertEquals("cluster c", finder.findCluster("sub").getAttribute("class"));
        assertEquals("cluster c", finder.findCluster(sub).getAttribute("class"));
    }

    @Test
    void findClusters() {
        final String svg = Graphviz.fromGraph(graph().with(
                graph("sub1").cluster().graphAttr().with("class", "c").with(node("a")),
                graph("sub2").cluster().graphAttr().with("class", "d").with(node("b"))))
                .render(SVG).toString();
        final SvgElementFinder finder = new SvgElementFinder(svg);
        assertEquals("cluster c", finder.findClusters().get(0).getAttribute("class"));
        assertEquals("cluster d", finder.findClusters().get(1).getAttribute("class"));
    }

    @Test
    void clusterNameOf() {
        final String svg = Graphviz.fromGraph(graph().with(
                graph("sub1").cluster().graphAttr().with("class", "c").with(node("a"))))
                .render(SVG).toString();
        final SvgElementFinder finder = new SvgElementFinder(svg);
        assertEquals("sub1", SvgElementFinder.clusterNameOf(finder.findClusters().get(0)));
    }

    @Test
    void use() {
        final String svg = Graphviz.fromGraph(graph().with(node("node"))).render(SVG).toString();
        final String newSvg = SvgElementFinder.use(svg, finder -> {
            finder.findNode("node").setAttribute("class", "hula");
        });
        assertThat(newSvg, containsString("<g class=\"hula\""));
    }
}
