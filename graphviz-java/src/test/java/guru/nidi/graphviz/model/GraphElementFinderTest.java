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

import static guru.nidi.graphviz.engine.Format.SVG;
import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GraphElementFinderTest {
    @Test
    void nodeOfNormal() {
        final Node a = node("a\nb");
        final Graph g = graph().with(a);
        final String svg = Graphviz.fromGraph(g).render(SVG).toString();
        final GraphElementFinder finder = new SvgElementFinder(svg).fromGraph(g);
        assertEquals(a, finder.nodeOf(finder.findNodes().get(0)));
    }
    @Test
    void nodeOfHtml() {
        final Node a = node(Label.html("c"));
        final Graph g = graph().with(a);
        final String svg = Graphviz.fromGraph(g).render(SVG).toString();
        final GraphElementFinder finder = new SvgElementFinder(svg).fromGraph(g);
        assertEquals(a, finder.nodeOf(finder.findNodes().get(0)));
    }

    @Test
    void linkOf() {
        final Node a = node("a").link("b");
        final Graph g = graph().directed().with(a);
        final String svg = Graphviz.fromGraph(g).render(SVG).toString();
        final GraphElementFinder finder = new SvgElementFinder(svg).fromGraph(g);
        assertEquals(a.links().get(0), finder.linkOf(finder.findLinks().get(0)));
    }

    @Test
    void clusterOf() {
        final Graph sub = graph("sub").cluster().graphAttr().with("class", "c").with(node("a"));
        final Graph g = graph().with(sub);
        final String svg = Graphviz.fromGraph(g).render(SVG).toString();
        final GraphElementFinder finder = new SvgElementFinder(svg).fromGraph(g);
        assertEquals(sub, finder.clusterOf(finder.findClusters().get(0)));
    }
}
