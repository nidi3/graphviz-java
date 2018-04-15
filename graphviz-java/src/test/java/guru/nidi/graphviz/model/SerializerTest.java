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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static guru.nidi.graphviz.model.Compass.*;
import static guru.nidi.graphviz.model.Factory.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SerializerTest {
    @AfterEach
    void closeContext() {
        CreationContext.end();
    }

    @Test
    void simple() {
        assertGraph("graph {\n}", graph());
    }

    @Test
    void directed() {
        assertGraph("digraph 'x' {\n}", graph("x").directed());
    }

    @Test
    void strict() {
        assertGraph("strict graph {\n}", graph().strict());
    }

    @Test
    void escapeLabel() {
        assertGraph("graph {\ngraph ['label'='b\\'la']\n}", graph().graphAttr().with(Label.of("b\"la")));
    }

    @Test
    void htmlLabel() {
        assertGraph("graph {\ngraph ['label'=<bla>]\n}", graph().graphAttr().with(Label.html("bla")));
    }

    @Test
    void graphAttr() {
        assertGraph("graph {\ngraph ['bla'='blu']\n}", graph().graphAttr().with("bla", "blu"));
    }

    @Test
    void cluster() {
        assertGraph("graph {\nsubgraph 'cluster_y' {\n'x' ['bla'='blu']\n}\n}", graph()
                .with(graph("y").cluster().with(node("x").with("bla", "blu"))));
    }

    @Test
    void nodeAttr() {
        assertGraph("graph {\nnode ['bla'='blu']\n}", graph().nodeAttr().with("bla", "blu"));
    }

    @Test
    void linkAttr() {
        assertGraph("graph {\nedge ['bla'='blu']\n}", graph().linkAttr().with("bla", "blu"));
    }

    @Test
    void nodes() {
        assertGraph("graph 'x' {\n'x' ['bla'='blu']\n}", graph("x")
                .with(node("x").with("bla", "blu")));
    }

    @Test
    void context() {
        CreationContext.begin()
                .graphs().add("g", "x")
                .nodes().add("n", "y")
                .links().add("l", "z");
        assertGraph("graph 'x' {\ngraph ['g'='x']\n'x' ['n'='y','bla'='blu']\n'y' ['n'='y']\n'x' -- 'y' ['l'='z']\n}", graph("x")
                .with(node("x").with("bla", "blu").link(node("y"))));
    }

    @Test
    void subgraph() {
        assertGraph("graph 'x' {\nsubgraph 'x' {\n'x' ['bla'='blu']\n}\n}", graph("x")
                .with(graph("x").with(node("x").with("bla", "blu"))));
    }

    @Test
    void namelessSubgraph() {
        assertGraph("graph 'x' {\n{\n'x' ['bla'='blu']\n}\n}", graph("x")
                .with(graph().with(node("x").with("bla", "blu"))));
    }

    @Test
    void simpleEdge() {
        assertGraph("graph 'x' {\n'x' -- 'y'\n}", graph("x")
                .with(node("x").link(node("y"))));
    }

    @Test
    void attrEdge() {
        assertGraph("graph 'x' {\n'x' -- 'y' ['bla'='blu']\n}", graph("x")
                .with(node("x").link(to(node("y")).with("bla", "blu"))));
    }

    @Test
    void graphEdgeStart() {
        assertGraph("graph 'x' {\nsubgraph 'y' {\n'z' -- 'a'\n} -- 'x':n\n}", graph("x").with(
                graph("y").with(node("z").link(
                        node("a"))).link(node("x").port(NORTH))));
    }

    @Test
    void graphEdgeEnd() {
        assertGraph("graph 'x' {\n'x':n -- subgraph 'y' {\n'z' -- 'a'\n}\n}", graph("x").with(
                node("x").link(between(port(NORTH),
                        graph("y").with(node("z").link(node("a")))))));
    }

    @Test
    void graphEdge() {
        assertGraph("graph 'x' {\nsubgraph 'y' {\n'z' -- 'a'\n} -- subgraph 'y2' {\n'z2' -- 'a2'\n}\n}", graph("x").with(
                graph("y").with(node("z").link(node("a"))).link(
                        graph("y2").with(node("z2").link(node("a2"))))));
    }

    @Test
    void compassEdge() {
        assertGraph("graph 'x' {\n'x':sw -- 'y':ne\n}", graph("x")
                .with(node("x").link(between(port(SOUTH_WEST), node("y").port(NORTH_EAST)))));
    }

    @Test
    void recordEdge() {
        assertGraph("graph 'x' {\n'x':'r1' -- 'y':'r2'\n}", graph("x")
                .with(node("x").link(between(port("r1"), node("y").port("r2")))));
    }

    @Test
    void compassRecordEdge() {
        assertGraph("graph 'x' {\n'x':'r1':sw -- 'y':'r2':ne\n}", graph("x")
                .with(node("x").link(
                        between(port("r1", SOUTH_WEST), node("y").port("r2", NORTH_EAST)))));
    }

    @Test
    void complexEdge() {
        assertGraph("digraph 'x' {\n'x' -> 'y'\n'y' -> 'z'\n'a' -> 'x'\n}", graph("x").directed()
                .with(node("x").link(node("y").link(node("z"))))
                .with(node("a").link(node("x"))));
    }

    private void assertGraph(String expected, Graph graph) {
        assertEquals(expected.replace("'", "\""), new Serializer((MutableGraph) graph).serialize());
    }
}
