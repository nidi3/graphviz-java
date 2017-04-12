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
package guru.nidi.graphviz.engine;

import static guru.nidi.graphviz.model.Factory.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import guru.nidi.graphviz.model.Graph;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;

public class GraphvizTest {

    @Test
    public void scaleMethodChainCheck() {
        final Graph graph = graph().with(node("a").link("b"));
        final Graphviz graphviz = Graphviz.fromGraph(graph).height(20).width(30).scale(3);

        assertThatGraphvizHasFields(graphviz, 20, 30, 3d);
    }

    @Test
    public void heightMethodChainCheck() {
        final Graph graph = graph().with(node("a").link("b"));
        final Graphviz graphviz = Graphviz.fromGraph(graph).scale(3).width(30).height(20);

        assertThatGraphvizHasFields(graphviz, 20, 30, 3d);
    }

    @Test
    public void widthMethodChainCheck() {
        final Graph graph = graph().with(node("a").link("b"));
        final Graphviz graphviz = Graphviz.fromGraph(graph).scale(3).height(20).width(30);

        assertThatGraphvizHasFields(graphviz, 20, 30, 3d);
    }

    @Test
    public void totalMemoryMethodChainCheck() {
        final Graph graph = graph().with(node("a").link("b"));

        final Graphviz graphviz = Graphviz.fromGraph(graph).height(20).width(30).scale(3).totalMemory(32000);

       assertThat(graphviz.totalMemory, CoreMatchers.is(32000));
    }

    @Test
    public void executeWithTotalMemory() {
        final Graph graph = graph().with(node("a").link("b"));

        Graphviz.useEngine(new AbstractGraphvizEngineTest.GraphvizEngineDummmy());
        final String result = Graphviz.fromGraph(graph).totalMemory(32000).execute(Format.SVG);

        assertThat(result, is("Viz('graph { \"a\" -- \"b\" }',{format:'svg',engine:'dot',totalMemory:'32000'});"));

        Graphviz.useEngine(null);
    }

    @Test
    public void executeWithoutTotalMemory() {
        final Graph graph = graph().with(node("a").link("b"));

        Graphviz.useEngine(new AbstractGraphvizEngineTest.GraphvizEngineDummmy());
        final String result = Graphviz.fromGraph(graph).execute(Format.SVG);

        assertThat(result, is("Viz('graph { \"a\" -- \"b\" }',{format:'svg',engine:'dot'});"));

        Graphviz.useEngine(null);
    }

    private void assertThatGraphvizHasFields(Graphviz graphviz, int expectedHeight, int expectedWidth, double expectedScale) {
        Assert.assertThat(graphviz.width, Is.is(expectedWidth));
        Assert.assertThat(graphviz.height, Is.is(expectedHeight));
        Assert.assertThat(graphviz.scale, Is.is(expectedScale));
    }




}