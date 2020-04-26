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
import guru.nidi.graphviz.attribute.validate.ValidatorMessage;
import guru.nidi.graphviz.attribute.validate.ValidatorMessage.Severity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static guru.nidi.graphviz.attribute.validate.ValidatorMessage.Severity.*;
import static guru.nidi.graphviz.model.Compass.*;
import static guru.nidi.graphviz.model.Factory.*;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SerializerTest {
    Ser ser = new Ser();

    @BeforeEach
    void init() {
        ser.messages.clear();
    }

    @Test
    void simple() {
        assertSerialize(graph(), "graph {\n}");
    }

    @Test
    void directed() {
        assertSerialize(graph("x").directed(), "digraph 'x' {\n}");
    }

    @Test
    void strict() {
        assertSerialize(graph().strict(), "strict graph {\n}");
    }

    @Test
    void escapeLabel() {
        assertSerialize(graph().graphAttr().with(Label.of("b\"l\\a\\")),
                "graph {\ngraph ['label'='b\\'l\\a\\\\']\n}");
    }

    @Test
    void htmlLabel() {
        assertSerialize(graph().graphAttr().with(Label.html("bla")),
                "graph {\ngraph ['label'=<bla>]\n}");
    }

    @Test
    void graphAttr() {
        assertSerialize(graph().graphAttr().with("bla", "blu"),
                "graph {\ngraph ['bla'='blu']\n}",
                msg(ERROR, "bla", "Attribute is unknown.", "Graph attrs of ''"));
    }

    @Test
    void cluster() {
        assertSerialize(graph().with(graph("y").cluster().graphAttr().with("center", true).with(node("x"))),
                "graph {\nsubgraph 'cluster_y' {\ngraph ['center'='true']\n'x'\n}\n}",
                msg(ERROR, "center", "Attribute is not allowed for clusters.", "Graph attrs of 'y'"));
    }

    @Test
    void nodeAttr() {
        assertSerialize(graph().nodeAttr().with("distortion", "-200"),
                "graph {\nnode ['distortion'='-200']\n}",
                msg(WARN, "distortion", "Attribute has a minimum of '-100.0' but is set to '-200'.", "Node attrs of ''"));
    }

    @Test
    void linkAttr() {
        assertSerialize(graph("x").linkAttr().with("color", "#blu"),
                "graph 'x' {\nedge ['color'='#blu']\n}",
                msg(ERROR, "color", "'#blu' is not valid for any of the possible types:\n"
                        + "As color: '#blu' is not a valid color.\n"
                        + "As list of colors: '#blu' is not a valid color.", "Edge attrs of 'x'"));
    }

    @Test
    void nodes() {
        assertSerialize(graph("x").with(node("y").with("center", "blu")),
                "graph 'x' {\n'y' ['center'='blu']\n}",
                msg(ERROR, "center", "Attribute is not allowed for nodes.", "Node 'y'"),
                msg(ERROR, "center", "'blu' is not a valid boolean.", "Node 'y'"));
    }

    @Test
    void context() {
        final Graph g = CreationContext.use(ctx -> {
            ctx
                    .graphAttrs().add("g", "x")
                    .nodeAttrs().add("n", "y")
                    .linkAttrs().add("l", "z");
            return graph("x").with(node("z").with("bla", "blu").link(node("y")));
        });
        assertSerialize(g,
                "graph 'x' {\ngraph ['g'='x']\n'z' ['n'='y','bla'='blu']\n'y' ['n'='y']\n'z' -- 'y' ['l'='z']\n}",
                msg(ERROR, "g", "Attribute is unknown.", "Graph attrs of 'x'"),
                msg(ERROR, "n", "Attribute is unknown.", "Node 'z'"),
                msg(ERROR, "bla", "Attribute is unknown.", "Node 'z'"),
                msg(ERROR, "n", "Attribute is unknown.", "Node 'y'"),
                msg(ERROR, "l", "Attribute is unknown.", "Edge 'z--y'"));
    }

    @Test
    void subgraph() {
        assertSerialize(graph("x").with(graph("y").graphAttr().with("center", "true").with(node("z"))),
                "graph 'x' {\nsubgraph 'y' {\ngraph [\"center\"=\"true\"]\n'z'\n}\n}",
                msg(ERROR, "center", "Attribute is not allowed for subgraphs.", "Graph attrs of 'y'"));
    }

    @Test
    void namelessSubgraph() {
        assertSerialize(graph("x").with(graph().with(node("y").with("distortion", 0))),
                "graph 'x' {\n{\n'y' ['distortion'='0']\n}\n}",
                msg(INFO, "distortion", "Attribute is set to its default value '0.0'.", "Node 'y'"));
    }

    @Test
    void simpleEdge() {
        assertSerialize(graph("x").with(node("x").link(node("y"))),
                "graph 'x' {\n'x' -- 'y'\n}");
    }

    @Test
    void attrEdge() {
        assertSerialize(graph("x").with(node("y").link(to(node("z")).with("bla", "blu"))),
                "graph 'x' {\n'y' -- 'z' ['bla'='blu']\n}",
                msg(ERROR, "bla", "Attribute is unknown.", "Edge 'y--z'"));
    }

    @Test
    void graphEdgeStart() {
        assertSerialize(graph("x").with(
                graph("y").with(node("z").link(
                        node("a"))).link(node("x").port(NORTH))),
                "graph 'x' {\nsubgraph 'y' {\n'z' -- 'a'\n} -- 'x':n\n}");
    }

    @Test
    void graphEdgeEnd() {
        assertSerialize(graph("x").with(
                node("x").link(between(port(NORTH),
                        graph("y").with(node("z").link(node("a")))))),
                "graph 'x' {\n'x':n -- subgraph 'y' {\n'z' -- 'a'\n}\n}");
    }

    @Test
    void graphEdge() {
        assertSerialize(graph("x").with(
                graph("y").with(node("z").link(node("a"))).link(
                        graph("y2").with(node("z2").link(node("a2"))))),
                "graph 'x' {\nsubgraph 'y' {\n'z' -- 'a'\n} -- subgraph 'y2' {\n'z2' -- 'a2'\n}\n}");
    }

    @Test
    void compassEdge() {
        assertSerialize(graph("x").with(node("x").link(between(port(SOUTH_WEST), node("y").port(NORTH_EAST)))),
                "graph 'x' {\n'x':sw -- 'y':ne\n}");
    }

    @Test
    void recordEdge() {
        assertSerialize(graph("x").with(node("x").link(between(port("r1"), node("y").port("r2")))),
                "graph 'x' {\n'x':'r1' -- 'y':'r2'\n}");
    }

    @Test
    void compassRecordEdge() {
        assertSerialize(graph("x").with(node("x").link(
                between(port("r1", SOUTH_WEST), node("y").port("r2", NORTH_EAST)))),
                "graph 'x' {\n'x':'r1':sw -- 'y':'r2':ne\n}");
    }

    @Test
    void complexEdge() {
        assertSerialize(graph("x").directed()
                        .with(node("x").link(node("y").link(node("z"))))
                        .with(node("a").link(node("x"))),
                "digraph 'x' {\n'x' -> 'y'\n'y' -> 'z'\n'a' -> 'x'\n}");
    }

    @Test
    void mixedDirected() {
        assertSerialize(graph().with(node("a").link(
                graph().directed().with(node("b").link(node("c"))))),
                "digraph {\nedge ['dir'='none']\n'a' -> {\nedge ['dir'='forward']\n'b' -> 'c'\n}\n}");
    }

    private void assertSerialize(Graph graph, String expectedString, ValidatorMessage... expectedMessages) {
        assertEquals(expectedString.replace("'", "\""), ser.serializer.serialize(graph));
        assertEquals(asList(expectedMessages), ser.messages);
    }

    private ValidatorMessage msg(Severity severity, String attribute, String message, String position) {
        return new ValidatorMessage(severity, attribute, message, 0, 0, position);
    }

    static class Ser {
        final List<ValidatorMessage> messages = new ArrayList<>();
        final Serializer serializer = new Serializer().messageConsumer(messages::add);
    }
}
