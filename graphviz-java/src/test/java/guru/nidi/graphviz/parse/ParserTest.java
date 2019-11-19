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

import guru.nidi.graphviz.attribute.*;
import guru.nidi.graphviz.attribute.validate.ValidatorMessage;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static guru.nidi.graphviz.attribute.Attributes.attr;
import static guru.nidi.graphviz.attribute.validate.ValidatorMessage.Severity.ERROR;
import static guru.nidi.graphviz.model.Compass.NORTH_EAST;
import static guru.nidi.graphviz.model.Compass.SOUTH_WEST;
import static guru.nidi.graphviz.model.Factory.*;
import static guru.nidi.graphviz.model.Link.between;
import static guru.nidi.graphviz.model.Link.to;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ParserTest {
    @Test
    void emptyGraph() throws IOException {
        final Pars pars = new Pars();
        assertEquals(mutGraph("bla"), pars.parser.read("graph bla{}"));
        assertEquals(asList(), pars.messages);
    }

    @Test
    void deduplicatedNodes() throws IOException {
        final Pars pars = new Pars();
        final MutableGraph g = pars.parser.read("graph { a--b; a[color=red] }");
        assertEquals(1, g.rootNodes().size());
        assertEquals(asList(), pars.messages);
    }

    @Test
    void emptyStrictDigraph() throws IOException {
        final Pars pars = new Pars();
        assertEquals(mutGraph("bla").setStrict(true).setDirected(true),
                pars.parser.read("strict digraph <bla>{}"));
        assertEquals(asList(), pars.messages);
    }

    @Test
    void attributesGraph() throws IOException {
        final Pars pars = new Pars();
        assertEquals(mutGraph()
                        .graphAttrs().add(attr("x", "y"), attr("a", "b"))
                        .add(mutNode("a").add(attr("c", "d")).addLink(to(mutNode("b").add(attr("c", "d")))
                                .with(attr("e", "f"), attr("g", "h"), attr("i", "j")))),
                pars.parser.read("graph { x=y; graph [a=b]; node[c=d] edge[e=f,g=h][i=j] a -- b }"));
        assertEquals(asList(
                new ValidatorMessage(ERROR, "x", "Attribute is unknown.", 1, 9),
                new ValidatorMessage(ERROR, "a", "Attribute is unknown.", 1, 21),
                new ValidatorMessage(ERROR, "c", "Attribute is unknown.", 1, 32),
                new ValidatorMessage(ERROR, "e", "Attribute is unknown.", 1, 42),
                new ValidatorMessage(ERROR, "g", "Attribute is unknown.", 1, 46),
                new ValidatorMessage(ERROR, "i", "Attribute is unknown.", 1, 51)),
                pars.messages);
    }

    @Test
    void nodes() throws IOException {
        final Pars pars = new Pars();
        assertEquals(mutGraph().add(mutNode("simple"), mutNode("with").add("dpi", "b")),
                pars.parser.read("graph { simple with[\"dpi\"=b]}")); //TODO with port? "d:1 full:1:ne
        assertEquals(asList(
                new ValidatorMessage(ERROR, "dpi", "Attribute is not allowed for scope 'NODE'.", 1, 23),
                new ValidatorMessage(ERROR, "dpi", "'b' is not a valid float.", 1, 23)),
                pars.messages);
    }

    @Test
    void links() throws IOException {
        final MutableNode
                simple = mutNode("simple"),
                c = mutNode("c"),
                d = mutNode("d"),
                full = mutNode("full");
        final Pars pars = new Pars();
        assertEquals(mutGraph().add(
                simple.addLink(to(c.port("2")).with("a", "b")),
                c.addLink(between(port("2"), d.port(SOUTH_WEST)).with("a", "b")),
                d.addLink(between(port(SOUTH_WEST), full.port("2", NORTH_EAST)).with("a", "b"))),
                pars.parser.read("graph { simple -- c:2 -- d:sw -- full:2:ne [a=b]}"));
        assertEquals(asList(
                new ValidatorMessage(ERROR, "a", "Attribute is unknown.", 1, 45)),
                pars.messages);
    }

    @Test
    void subgraph() throws IOException {
        final Pars pars = new Pars();
        assertEquals(mutGraph().add(
                mutGraph("s").graphAttrs().add("dpi", "b"),
                mutGraph().graphAttrs().add("c", "d"),
                mutGraph().graphAttrs().add("e", "f")),
                pars.parser.read("graph { subgraph s { dpi=b }; subgraph { c=d }; { e=f } }"));
        assertEquals(asList(
                new ValidatorMessage(ERROR, "dpi", "Attribute is not allowed for scope 'SUB_GRAPH'.", 1, 22),
                new ValidatorMessage(ERROR, "dpi", "'b' is not a valid float.", 1, 22),
                new ValidatorMessage(ERROR, "c", "Attribute is unknown.", 1, 42),
                new ValidatorMessage(ERROR, "e", "Attribute is unknown.", 1, 51)),
                pars.messages);
    }

    @Test
    void leftSubgraphEdge() throws IOException {
        final Pars pars = new Pars();
        assertEquals(mutGraph().add(
                mutGraph().addLink(to(mutNode("x")).with("a", "b")),
                mutGraph().addLink(mutNode("y")),
                mutGraph("a").addLink(mutNode("z"))),
                pars.parser.read("graph{ {} -- x [a=b]  subgraph{} -- y  subgraph a{} -- z }"));
        assertEquals(asList(
                new ValidatorMessage(ERROR, "a", "Attribute is unknown.", 1, 17)),
                pars.messages);
    }

    @Test
    void rightSubgraphEdge() throws IOException {
        final Pars pars = new Pars();
        assertEquals(mutGraph().add(
                mutNode("x").addLink(to(mutGraph()).with("a", "b")),
                mutNode("y").addLink(mutGraph()),
                mutNode("z").addLink(mutGraph("a"))),
                pars.parser.read("graph{ x -- {} [a=b]  y -- subgraph{}  z -- subgraph a{} }"));
        assertEquals(asList(
                new ValidatorMessage(ERROR, "a", "Attribute is unknown.", 1, 17)),
                pars.messages);
    }

    @Test
    void subgraphSubgraphEdge() throws IOException {
        final Pars pars = new Pars();
        assertEquals(mutGraph().add(
                mutGraph().addLink(to(mutGraph()).with("a", "b")),
                mutGraph().addLink(mutGraph()),
                mutGraph().addLink(mutGraph("a"))),
                pars.parser.read("graph{ {} -- {} [a=b]  {} -- subgraph{}  {} -- subgraph a{} }"));
        assertEquals(asList(
                new ValidatorMessage(ERROR, "a", "Attribute is unknown.", 1, 18)),
                pars.messages);
    }

    @Test
    void inheritDirected() throws IOException {
        final Pars pars = new Pars();
        assertEquals(mutGraph().setDirected(true).add(
                mutGraph().setDirected(true).add(mutNode("a").addLink("b"))),
                pars.parser.read("digraph { subgraph { a -> b } }"));
        assertEquals(asList(), pars.messages);
    }

    @Test
    void emptyString() throws IOException {
        final Pars pars = new Pars();
        assertEquals(mutGraph().add(mutNode(""), mutNode("a").add("label", Label.of(""))),
                pars.parser.read("graph { \"\" a [label=\"\"] }"));
        assertEquals(asList(), pars.messages);
    }

    @Test
    void multiNodeAttr() throws IOException {
        final Pars pars = new Pars();
        final MutableNode b = mutNode("b").add(Color.BLUE, attr("dpi", "1"), Shape.EGG);
        final MutableNode a = mutNode("a").add(Color.RED, attr("dpi", "1")).addLink(b);
        assertEquals(mutGraph().add(a),
                pars.parser.read("graph { node[color=red, dpi=1] a node[color=blue, shape=egg] a -- b }"));
        assertEquals(asList(
                new ValidatorMessage(ERROR, "dpi", "Attribute is not allowed for scope 'NODE'.", 1, 25)),
                pars.messages);
    }

    @Test
    void multiLinkAttr() throws IOException {
        final MutableNode b = mutNode("b");
        final MutableNode a = mutNode("a").addLink(to(b).with(Color.RED, attr("width", "x")))
                .addLink(to(b).with(Color.BLUE, attr("width", "x"), attr("a", "b")));
        final Pars pars = new Pars();
        assertEquals(mutGraph().add(a),
                pars.parser.read("graph { edge[color=red, width=x] a -- b edge[color=blue, a=b] a -- b }"));
        assertEquals(asList(
                new ValidatorMessage(ERROR, "width", "Attribute is not allowed for scope 'EDGE'.", 1, 25),
                new ValidatorMessage(ERROR, "width", "'x' is not a valid float.", 1, 25),
                new ValidatorMessage(ERROR, "a", "Attribute is unknown.", 1, 58)),
                pars.messages);
    }

    @Test
    void cluster() throws IOException {
        final Pars pars = new Pars();
        assertEquals(mutGraph().add(mutGraph("sub").setCluster(true)),
                pars.parser.read("graph { subgraph cluster_sub {} }"));
        assertEquals(asList(), pars.messages);
    }

    static class Pars {
        final List<ValidatorMessage> messages = new ArrayList<>();
        final Parser parser = new Parser().messageConsumer(messages::add);
    }
}
