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
import guru.nidi.graphviz.attribute.Named;
import guru.nidi.graphviz.attribute.validate.ValidatorMessage;
import guru.nidi.graphviz.attribute.validate.ValidatorMessage.Location;
import guru.nidi.graphviz.attribute.validate.ValidatorMessage.Severity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static guru.nidi.graphviz.attribute.validate.ValidatorMessage.Location.Type.*;
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
        final Graph g = graph().graphAttr().with("bla", "blu");
        assertSerialize(g, "graph {\ngraph ['bla'='blu']\n}",
                msg(ERROR, "bla", "is unknown.", GRAPH_ATTRS, g));
    }

    @Test
    void cluster() {
        final Graph y = graph("y").cluster().graphAttr().with("center", true).with(node("x"));
        final Graph g = graph().with(y);
        assertSerialize(g, "graph {\nsubgraph 'cluster_y' {\ngraph ['center'='true']\n'x'\n}\n}",
                msg(ERROR, "center", "is not allowed for clusters.", GRAPH_ATTRS, y));
    }

    @Test
    void nodeAttr() {
        final Graph g = graph().nodeAttr().with("distortion", "-200");
        assertSerialize(g, "graph {\nnode ['distortion'='-200']\n}",
                msg(WARN, "distortion", "has the value '-200' smaller than the minimum of '-100.0'.", NODE_ATTRS, g));
    }

    @Test
    void linkAttr() {
        final Graph g = graph("x").linkAttr().with("color", "#blu");
        assertSerialize(g, "graph 'x' {\nedge ['color'='#blu']\n}",
                msg(ERROR, "color", "has the value '#blu' which is not valid for any of the possible types:\n" +
                        "As color it has the invalid color value '#blu'.\n" +
                        "As list of colors it has the invalid color value '#blu'.", LINK_ATTRS, g));
    }

    @Test
    void nodes() {
        final Node y = node("y").with("center", "blu");
        final Graph g = graph("x").with(y);
        assertSerialize(g, "graph 'x' {\n'y' ['center'='blu']\n}",
                msg(ERROR, "center", "is not allowed for nodes.", NODE, y),
                msg(ERROR, "center", "has the invalid boolean value 'blu'.", NODE, y));
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
        final MutableNode z = ((MutableGraph) g).rootNodes().iterator().next();
        assertSerialize(g, "graph 'x' {\ngraph ['g'='x']\n'z' ['n'='y','bla'='blu']\n'y' ['n'='y']\n'z' -- 'y' ['l'='z']\n}",
                msg(ERROR, "g", "is unknown.", GRAPH_ATTRS, g),
                msg(ERROR, "n", "is unknown.", NODE, z),
                msg(ERROR, "bla", "is unknown.", NODE, z),
                msg(ERROR, "n", "is unknown.", NODE, ((PortNode) z.links().get(0).to()).node()),
                msg(ERROR, "l", "is unknown.", LINK, z.links().get(0)));
    }

    @Test
    void subgraph() {
        final Graph y = graph("y").graphAttr().with("center", "true").with(node("z"));
        final Graph g = graph("x").with(y);
        assertSerialize(g, "graph 'x' {\nsubgraph 'y' {\ngraph [\"center\"=\"true\"]\n'z'\n}\n}",
                msg(ERROR, "center", "is not allowed for subgraphs.", GRAPH_ATTRS, y));
    }

    @Test
    void namelessSubgraph() {
        final Node y = node("y").with("distortion", 0);
        final Graph g = graph("x").with(graph().with(y));
        assertSerialize(g, "graph 'x' {\n{\n'y' ['distortion'='0']\n}\n}",
                msg(INFO, "distortion", "has its default value '0.0'.", NODE, y));
    }

    @Test
    void simpleEdge() {
        assertSerialize(graph("x").with(node("x").link(node("y"))),
                "graph 'x' {\n'x' -- 'y'\n}");
    }

    @Test
    void attrEdge() {
        final Node y = node("y").link(to(node("z")).with("bla", "blu"));
        final Graph g = graph("x").with(y);
        assertSerialize(g, "graph 'x' {\n'y' -- 'z' ['bla'='blu']\n}",
                msg(ERROR, "bla", "is unknown.", LINK, y.links().get(0)));
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

    @Test
    void privateAttribute() {
        assertSerialize(graph().graphAttr().with("$a", "b").with(node("a").with("$b", "c")),
                "graph {\n'a'\n}");
    }

    private void assertSerialize(Graph graph, String expectedString, ValidatorMessage... expectedMessages) {
        assertEquals(expectedString.replace("'", "\""), ser.serializer.serialize(graph));
        assertEquals(asList(expectedMessages), ser.messages);
    }

    private ValidatorMessage msg(Severity severity, String attribute, String message, Location.Type type, Named name) {
        return new ValidatorMessage(severity, attribute, message, null, new Location(type, name));
    }

    static class Ser {
        final List<ValidatorMessage> messages = new ArrayList<>();
        final Serializer serializer = new Serializer().validating(messages::add);
    }
}
