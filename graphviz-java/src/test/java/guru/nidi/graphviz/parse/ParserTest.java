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
import guru.nidi.graphviz.attribute.validate.ValidatorMessage.Severity;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;
import org.junit.jupiter.api.BeforeEach;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParserTest {
    Pars pars = new Pars();

    @BeforeEach
    void init() {
        pars.messages.clear();
    }

    @Test
    void emptyGraph() throws IOException {
        assertParse("graph bla{}", mutGraph("bla"));
    }

    @Test
    void deduplicatedNodes() throws IOException {
        assertEquals(1, pars.parser.read("graph { a--b; a[color=red] }").rootNodes().size());
        assertEquals(asList(), pars.messages);
    }

    @Test
    void emptyStrictDigraph() throws IOException {
        assertParse("strict digraph <bla>{}",
                mutGraph("bla").setStrict(true).setDirected(true));
    }

    @Test
    void attributesGraph() throws IOException {
        assertParse("graph { x=y; graph [a=b]; node[c=d] edge[e=f,g=h][i=j] a -- b }",
                mutGraph()
                        .graphAttrs().add(attr("x", "y"), attr("a", "b"))
                        .add(mutNode("a").add(attr("c", "d")).addLink(to(mutNode("b").add(attr("c", "d")))
                                .with(attr("e", "f"), attr("g", "h"), attr("i", "j")))),
                msg(ERROR, "x", "is unknown.", 1, 9),
                msg(ERROR, "a", "is unknown.", 1, 21),
                msg(ERROR, "c", "is unknown.", 1, 32),
                msg(ERROR, "e", "is unknown.", 1, 42),
                msg(ERROR, "g", "is unknown.", 1, 46),
                msg(ERROR, "i", "is unknown.", 1, 51));
    }

    @Test
    void nodes() throws IOException {
        assertParse("graph { simple with[\"dpi\"=b]}", //TODO with port? "d:1 full:1:ne
                mutGraph().add(mutNode("simple"), mutNode("with").add("dpi", "b")),
                msg(ERROR, "dpi", "is not allowed for nodes.", 1, 23),
                msg(ERROR, "dpi", "has the invalid float value 'b'.", 1, 23));
    }

    @Test
    void links() throws IOException {
        final MutableNode
                simple = mutNode("simple"),
                c = mutNode("c"),
                d = mutNode("d"),
                full = mutNode("full");
        assertParse("graph { simple -- c:2 -- d:sw -- full:2:ne [a=b]}",
                mutGraph().add(
                        simple.addLink(to(c.port("2")).with("a", "b")),
                        c.addLink(between(port("2"), d.port(SOUTH_WEST)).with("a", "b")),
                        d.addLink(between(port(SOUTH_WEST), full.port("2", NORTH_EAST)).with("a", "b"))),
                msg(ERROR, "a", "is unknown.", 1, 45));
    }

    @Test
    void subgraph() throws IOException {
        assertParse("graph { subgraph s { dpi=b }; subgraph { c=d }; { e=f } }",
                mutGraph().add(
                        mutGraph("s").graphAttrs().add("dpi", "b"),
                        mutGraph().graphAttrs().add("c", "d"),
                        mutGraph().graphAttrs().add("e", "f")),
                msg(ERROR, "dpi", "is not allowed for subgraphs.", 1, 22),
                msg(ERROR, "dpi", "has the invalid float value 'b'.", 1, 22),
                msg(ERROR, "c", "is unknown.", 1, 42),
                msg(ERROR, "e", "is unknown.", 1, 51));
    }

    @Test
    void leftSubgraphEdge() throws IOException {
        assertParse("graph{ {} -- x [a=b]  subgraph{} -- y  subgraph a{} -- z }",
                mutGraph().add(
                        mutGraph().addLink(to(mutNode("x")).with("a", "b")),
                        mutGraph().addLink(mutNode("y")),
                        mutGraph("a").addLink(mutNode("z"))),
                msg(ERROR, "a", "is unknown.", 1, 17));
    }

    @Test
    void rightSubgraphEdge() throws IOException {
        assertParse("graph{ x -- {} [a=b]  y -- subgraph{}  z -- subgraph a{} }",
                mutGraph().add(
                        mutNode("x").addLink(to(mutGraph()).with("a", "b")),
                        mutNode("y").addLink(mutGraph()),
                        mutNode("z").addLink(mutGraph("a"))),
                msg(ERROR, "a", "is unknown.", 1, 17));
    }

    @Test
    void subgraphSubgraphEdge() throws IOException {
        assertParse("graph{ {} -- {} [a=b]  {} -- subgraph{}  {} -- subgraph a{} }",
                mutGraph().add(
                        mutGraph().addLink(to(mutGraph()).with("a", "b")),
                        mutGraph().addLink(mutGraph()),
                        mutGraph().addLink(mutGraph("a"))),
                msg(ERROR, "a", "is unknown.", 1, 18));
    }

    @Test
    void inheritDirected() throws IOException {
        assertParse("digraph { subgraph { a -> b } }",
                mutGraph().setDirected(true).add(
                        mutGraph().setDirected(true).add(mutNode("a").addLink("b"))));
    }

    @Test
    void emptyString() throws IOException {
        assertParse("graph { \"\" a [label=\"\"] }",
                mutGraph().add(mutNode(""), mutNode("a").add("label", Label.of(""))));
    }

    @Test
    void multiNodeAttr() throws IOException {
        final MutableNode b = mutNode("b").add(Color.BLUE, attr("dpi", "1"), Shape.EGG);
        final MutableNode a = mutNode("a").add(Color.RED, attr("dpi", "1")).addLink(b);
        assertParse("graph { node[color=red, dpi=1] a node[color=blue, shape=egg] a -- b }",
                mutGraph().add(a),
                msg(ERROR, "dpi", "is not allowed for nodes.", 1, 25));
    }

    @Test
    void multiLinkAttr() throws IOException {
        final MutableNode b = mutNode("b");
        final MutableNode a = mutNode("a").addLink(to(b).with(Color.RED, attr("width", "x")))
                .addLink(to(b).with(Color.BLUE, attr("width", "x"), attr("a", "b")));
        assertParse("graph { edge[color=red, width=x] a -- b edge[color=blue, a=b] a -- b }",
                mutGraph().add(a),
                msg(ERROR, "width", "is not allowed for edges.", 1, 25),
                msg(ERROR, "width", "has the invalid float value 'x'.", 1, 25),
                msg(ERROR, "a", "is unknown.", 1, 58));
    }

    @Test
    void cluster() throws IOException {
        assertParse("graph { subgraph cluster_sub {} }",
                mutGraph().add(mutGraph("sub").setCluster(true)));
    }

    @Test
    void comment() throws IOException {
        assertParse("graph //bla \n#blu \n {/*hula*/}", mutGraph());
    }

    @Test
    void missingBrace() {
        final ParserException e = assertThrows(ParserException.class, () -> pars.parser.read("graph\n { a-b"));
        assertException("'}' expected.", 2, 7, e);
    }

    @Test
    void missingNodeOrSubgraph() {
        final ParserException e = assertThrows(ParserException.class, () -> pars.parser.read("graph\n { a-- }"));
        assertException("node or 'graph' or '{' expected.", 2, 9, e);
    }

    private void assertException(String message, int line, int col, ParserException e) {
        assertEquals(message, e.getMessage());
        assertEquals(line, e.getPosition().getLine());
        assertEquals(col, e.getPosition().getCol());
    }

    private void assertParse(String input, MutableGraph expectedGraph, ValidatorMessage... expectedMessages) throws IOException {
        assertEquals(expectedGraph, pars.parser.read(input));
        assertEquals(asList(expectedMessages), pars.messages);
    }

    private ValidatorMessage msg(Severity severity, String attribute, String message, int line, int column) {
        return new ValidatorMessage(severity, attribute, message, new ValidatorMessage.Position("<string>", line, column), null);
    }

    static class Pars {
        final List<ValidatorMessage> messages = new ArrayList<>();
        final Parser parser = new Parser().validating(messages::add);
    }
}
