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
package guru.nidi.graphviz.parse;

import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Label;
import guru.nidi.graphviz.model.Node;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static guru.nidi.graphviz.model.Compass.NORTH_EAST;
import static guru.nidi.graphviz.model.Compass.SOUTH_WEST;
import static guru.nidi.graphviz.model.Factory.*;
import static org.junit.Assert.assertEquals;

/**
 *
 */
public class ParserTest {
    @Test
    public void emptyGraph() throws IOException {
        assertEquals(Graph.named("bla"),
                parse("graph bla{}"));
    }

    @Test
    public void emptyStrictDigraph() throws IOException {
        assertEquals(Graph.named(Label.html("bla")).strict().directed(),
                parse("strict digraph <bla>{}"));
    }

    @Test
    public void attributesGraph() throws IOException {
        assertEquals(graph()
                        .general().attr("x", "y")
                        .graphs().attr("a", "b")
                        .nodes().attr("c", "d")
                        .links().attr("e", "f", "g", "h", "i", "j"),
                parse("graph { x=y; graph [a=b]; node[c=d] edge[e=f,g=h][i=j] }"));
    }

    @Test
    public void nodes() throws IOException {
        assertEquals(graph().withNodes("simple").with(node("attr").attr("a", "b")),
                parse("graph { simple attr[\"a\"=b]}")); //TODO with port? "d:1 full:1:ne
    }

    @Test
    public void links() throws IOException {
        final Node
                simple = node("simple"),
                d = node("d"),
                full = node("full");
        assertEquals(graph().withNodes(
                simple.link(to(d.loc(SOUTH_WEST)).attr("a", "b")),
                d.link(between(loc(SOUTH_WEST), full.loc("2", NORTH_EAST)).attr("a", "b"))),
                parse("graph { simple -- d:sw -- full:2:ne [a=b]}"));
    }

    @Test
    public void subgraph() throws IOException {
        assertEquals(graph().withGraphs(
                graph("s").general().attr("a", "b"),
                graph().general().attr("c", "d"),
                graph().general().attr("e", "f")),
                parse("graph { subgraph s { a=b }; subgraph { c=d }; { e=f } }"));
    }

    @Test
    public void leftSubgraphEdge() throws IOException {
        assertEquals(graph().withGraphs(
                graph().link(to(node("x")).attr("a", "b")),
                graph().link(node("y")),
                graph("a").link(node("z"))),
                parse("graph{ {} -- x [a=b]  subgraph{} -- y  subgraph a{} -- z }"));
    }

    @Test
    public void rightSubgraphEdge() throws IOException {
        assertEquals(graph().withNodes(
                node("x").link(to(graph()).attr("a", "b")),
                node("y").link(graph()),
                node("z").link(graph("a"))),
                parse("graph{ x -- {} [a=b]  y -- subgraph{}  z -- subgraph a{} }"));
    }

    @Test
    public void subgraphSubgraphEdge() throws IOException {
        assertEquals(graph().withGraphs(
                graph().link(to(graph()).attr("a", "b")),
                graph().link(graph()),
                graph().link(graph("a"))),
                parse("graph{ {} -- {} [a=b]  {} -- subgraph{}  {} -- subgraph a{} }"));
    }

    private Graph parse(String s) throws IOException {
        final Parser parser = new Parser(new Lexer(new StringReader(s)));
        return parser.parse();
    }
}
