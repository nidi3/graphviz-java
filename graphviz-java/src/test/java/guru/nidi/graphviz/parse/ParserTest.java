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
package guru.nidi.graphviz.parse;

import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.model.MutableNode;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static guru.nidi.graphviz.attribute.Attributes.attr;
import static guru.nidi.graphviz.model.Compass.NORTH_EAST;
import static guru.nidi.graphviz.model.Compass.SOUTH_WEST;
import static guru.nidi.graphviz.model.Factory.*;
import static guru.nidi.graphviz.model.Link.between;
import static guru.nidi.graphviz.model.Link.to;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ParserTest {
    @Test
    void emptyGraph() throws IOException {
        assertEquals(mutGraph("bla"), Parser.read("graph bla{}"));
    }

    @Test
    void emptyStrictDigraph() throws IOException {
        assertEquals(mutGraph("bla").setStrict(true).setDirected(true),
                Parser.read("strict digraph <bla>{}"));
    }

    @Test
    void attributesGraph() throws IOException {
        assertEquals(mutGraph()
                        .graphAttrs().add("x", "y")
                        .graphAttrs().add("a", "b")
                        .nodeAttrs().add("c", "d")
                        .linkAttrs().add(attr("e", "f"), attr("g", "h"), attr("i", "j")),
                Parser.read("graph { x=y; graph [a=b]; node[c=d] edge[e=f,g=h][i=j] }"));
    }

    @Test
    void nodes() throws IOException {
        assertEquals(mutGraph().add(mutNode("simple"), mutNode("with").add("a", "b")),
                Parser.read("graph { simple with[\"a\"=b]}")); //TODO with port? "d:1 full:1:ne
    }

    @Test
    void links() throws IOException {
        final MutableNode
                simple = mutNode("simple"),
                c = mutNode("c"),
                d = mutNode("d"),
                full = mutNode("full");
        assertEquals(mutGraph().add(
                simple.addLink(to(c.withRecord("2")).with("a", "b")),
                c.withRecord("2").addLink(to(d.withCompass(SOUTH_WEST)).with("a", "b")),
                d.addLink(between(port(SOUTH_WEST), full.withRecord("2").setCompass(NORTH_EAST)).with("a", "b"))),
                Parser.read("graph { simple -- c:2 -- d:sw -- full:2:ne [a=b]}"));
    }

    @Test
    void subgraph() throws IOException {
        assertEquals(mutGraph().add(
                mutGraph("s").graphAttrs().add("a", "b"),
                mutGraph().graphAttrs().add("c", "d"),
                mutGraph().graphAttrs().add("e", "f")),
                Parser.read("graph { subgraph s { a=b }; subgraph { c=d }; { e=f } }"));
    }

    @Test
    void leftSubgraphEdge() throws IOException {
        assertEquals(mutGraph().add(
                mutGraph().addLink(to(mutNode("x")).with("a", "b")),
                mutGraph().addLink(mutNode("y")),
                mutGraph("a").addLink(mutNode("z"))),
                Parser.read("graph{ {} -- x [a=b]  subgraph{} -- y  subgraph a{} -- z }"));
    }

    @Test
    void rightSubgraphEdge() throws IOException {
        assertEquals(mutGraph().add(
                mutNode("x").addLink(to(mutGraph()).with("a", "b")),
                mutNode("y").addLink(mutGraph()),
                mutNode("z").addLink(mutGraph("a"))),
                Parser.read("graph{ x -- {} [a=b]  y -- subgraph{}  z -- subgraph a{} }"));
    }

    @Test
    void subgraphSubgraphEdge() throws IOException {
        assertEquals(mutGraph().add(
                mutGraph().addLink(to(mutGraph()).with("a", "b")),
                mutGraph().addLink(mutGraph()),
                mutGraph().addLink(mutGraph("a"))),
                Parser.read("graph{ {} -- {} [a=b]  {} -- subgraph{}  {} -- subgraph a{} }"));
    }

    @Test
    void inheritDirected() throws IOException {
        assertEquals(mutGraph().setDirected(true).add(
                mutGraph().setDirected(true).add(mutNode("a").addLink("b"))),
                Parser.read("digraph { subgraph { a -> b } }"));
    }

    @Test
    void emptyString() throws IOException {
        assertEquals(mutGraph().add(mutNode(""), mutNode("a").add("label", Label.of(""))),
                Parser.read("graph { \"\" a [label=\"\"] }"));
    }
}
