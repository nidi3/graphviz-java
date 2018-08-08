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

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.engine.*;
import org.junit.jupiter.api.*;

import java.util.List;

import static guru.nidi.graphviz.model.Factory.*;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ContextTest {
    @BeforeAll
    static void init() {
        Graphviz.useEngine(new GraphvizV8Engine(), new GraphvizJdkEngine());
    }

    @AfterAll
    static void end() {
        Graphviz.releaseEngine();
    }

    @Test
    void overwriteNode() {
        final MutableGraph g = CreationContext.use(ctx -> {
            ctx.nodeAttrs().add(Color.RED);
            return mutGraph().add(
                    node("a").with(Color.BLUE),
                    node("b"));
        });
        assertEquals(mutGraph().add(
                node("a").with(Color.BLUE),
                node("b").with(Color.RED)),
                g);
    }

    @Test
    void overwriteLink() {
        final MutableGraph g = CreationContext.use(ctx -> {
            ctx.linkAttrs().add(Color.RED);
            return mutGraph().add(
                    node("a").link(to(node("b")).with(Color.BLUE)),
                    node("b").link(node("c")));
        });
        assertEquals(mutGraph().add(
                node("a").link(to(node("b")).with(Color.BLUE)),
                node("b").link(to(node("c")).with(Color.RED))),
                g);
    }

    @Test
    void overwriteGraph() {
        final List<MutableGraph> gs = CreationContext.use(ctx -> {
            ctx.graphAttrs().add(Color.RED);
            return asList(
                    mutGraph().graphAttrs().add(Color.BLUE),
                    mutGraph());
        });
        assertEquals(asList(
                mutGraph().graphAttrs().add(Color.BLUE),
                mutGraph().graphAttrs().add(Color.RED)),
                gs);
    }
}
