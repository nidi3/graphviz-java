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

import java.util.*;

import static guru.nidi.graphviz.model.Factory.*;
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
            ctx.nodes().add(Color.RED);
            return mutGraph().add(node("a").with(Color.BLUE)).add(node("b"));
        });
        final Iterator<MutableNode> iter = g.nodes().iterator();
        assertEquals("blue", iter.next().attrs().get("color"));
        assertEquals("red", iter.next().attrs().get("color"));
    }

    @Test
    void overwriteLink() {
        final MutableGraph g = CreationContext.use(ctx -> {
            ctx.links().add(Color.RED);
            return mutGraph().add(node("a").link(to(node("b")).with(Color.BLUE)),
                    node("b").link(node("c")));
        });
        final Iterator<MutableNode> nodes = g.rootNodes().iterator();
        assertEquals("blue", nodes.next().links().iterator().next().get("color"));
        assertEquals("red", nodes.next().links().iterator().next().get("color"));
    }

    @Test
    void overwriteGraph() {
        final List<MutableGraph> gs = CreationContext.use(ctx -> {
            ctx.graphs().add(Color.RED);
            return Arrays.asList(mutGraph().graphAttrs().add(Color.BLUE), mutGraph());
        });
        assertEquals("blue", gs.get(0).graphAttrs().get("color"));
        assertEquals("red", gs.get(1).graphAttrs().get("color"));
    }
}
