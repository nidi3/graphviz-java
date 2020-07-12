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

import guru.nidi.graphviz.attribute.validate.ValidatorMessage;
import guru.nidi.graphviz.attribute.validate.ValidatorMessage.Location;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Node;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static guru.nidi.graphviz.attribute.validate.ValidatorMessage.Location.Type.NODE;
import static guru.nidi.graphviz.attribute.validate.ValidatorMessage.Severity.ERROR;
import static guru.nidi.graphviz.engine.Format.SVG;
import static guru.nidi.graphviz.engine.Rasterizer.NONE;
import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;
import static guru.nidi.graphviz.service.SystemUtils.uriPathOf;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GraphvizTest {
    @BeforeAll
    static void init() {
        Graphviz.useEngine(new AbstractGraphvizEngineTest.GraphvizEngineDummy());
    }

    @AfterAll
    static void end() {
        Graphviz.releaseEngine();
    }

    @Test
    void scaleMethodChain() {
        final Graph graph = graph().with(node("a").link("b"));
        final Graphviz graphviz = Graphviz.fromGraph(graph).height(20).width(30).scale(3);
        assertThatGraphvizHasFields(graphviz, 20, 30, 3d);
    }

    @Test
    void heightMethodChain() {
        final Graph graph = graph().with(node("a").link("b"));
        final Graphviz graphviz = Graphviz.fromGraph(graph).scale(3).width(30).height(20);
        assertThatGraphvizHasFields(graphviz, 20, 30, 3d);
    }

    @Test
    void widthMethodChain() {
        final Graph graph = graph().with(node("a").link("b"));
        final Graphviz graphviz = Graphviz.fromGraph(graph).scale(3).height(20).width(30);
        assertThatGraphvizHasFields(graphviz, 20, 30, 3d);
    }

    @Test
    void fromFile() throws IOException {
        final String result = Graphviz.fromFile(new File("src/test/resources/color.dot")).render(SVG).toString();
        assertThat(result, containsString("rank=same; cyan; yellow; pink"));
    }

    @Test
    void withTotalMemory() {
        final Graph graph = graph().with(node("a").link("b"));
        final String result = Graphviz.fromGraph(graph).totalMemory(32000).render(SVG).toString();
        assertThat(result, is("totalMemory=32000;render('graph {\\n\"a\" -- \"b\"\\n}',"
                + "{format:'svg',engine:'dot',totalMemory:'32000',basedir:'" + uriPathOf(new File(".")) + "',images:[]});"));
    }

    @Test
    void withoutTotalMemory() {
        final Graph graph = graph().with(node("a").link("b"));
        final String result = Graphviz.fromGraph(graph).render(SVG).toString();
        assertThat(result, is("render('graph {\\n\"a\" -- \"b\"\\n}',"
                + "{format:'svg',engine:'dot',basedir:'" + uriPathOf(new File(".")) + "',images:[]});"));
    }

    @Test
    void withYInvert() {
        final Graph graph = graph().with(node("a").link("b"));
        final String result = Graphviz.fromGraph(graph).yInvert(true).render(SVG).toString();
        assertThat(result, is("render('graph {\\n\"a\" -- \"b\"\\n}',"
                + "{format:'svg',engine:'dot',yInvert:true,basedir:'" + uriPathOf(new File(".")) + "',images:[]});"));
    }

    @Test
    void validating() {
        final List<ValidatorMessage> messages = new ArrayList<>();
        final Node a = node("a").with("c", "d").link("b");
        Graphviz.fromGraph(graph().with(a)).validating(messages::add).render(SVG).toString();
        assertEquals(asList(new ValidatorMessage(ERROR, "c", "is unknown.", null, new Location(NODE, a))), messages);
    }

    @Test
    void noRasterizer() {
        final Graph graph = graph().with(node("a").link("b"));
        assertThrows(IllegalArgumentException.class, () -> Graphviz.fromGraph(graph).rasterize(NONE));
    }

    @Test
    void processor() {
        final Graph graph = graph().with(node("a").link("b"));
        final String result = Graphviz.fromGraph(graph)
                .preProcessor((String source, Options options, ProcessOptions processOptions) ->
                        source.replace("\"a\"", "aaa"))
                .postProcessor((EngineResult res, Options options, ProcessOptions processOptions) ->
                        res.mapString((s) -> s + "2"))
                .render(SVG).toString();
        assertThat(result, startsWith("render('graph {\\naaa -- \"b\"\\n}'"));
    }

    private void assertThatGraphvizHasFields(Graphviz graphviz, int expectedHeight, int expectedWidth, double expectedScale) {
        assertThat(graphviz.processOptions.width, is(expectedWidth));
        assertThat(graphviz.processOptions.height, is(expectedHeight));
        assertThat(graphviz.processOptions.scale, is(expectedScale));
    }
}
