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
package guru.nidi.graphviz.rough;

import guru.nidi.graphviz.attribute.*;
import guru.nidi.graphviz.engine.*;
import guru.nidi.graphviz.model.Graph;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReadmeTest {
    @BeforeAll
    static void init() {
        Graphviz.useEngine(new GraphvizV8Engine());
    }

    @Test
    void simple() throws IOException {
        //## rough
        final Graph g = graph("ex1").directed().with(
                graph().cluster()
                        .nodeAttr().with(Style.FILLED, Color.WHITE)
                        .graphAttr().with(Style.FILLED, Color.LIGHTGREY, Label.of("process #1"))
                        .with(node("a0").link(node("a1").link(node("a2")))),
                graph("x").cluster()
                        .nodeAttr().with(Style.FILLED)
                        .graphAttr().with(Color.BLUE, Label.of("process #2"))
                        .with(node("b0").link(node("b1").link(node("b2")))),
                node("start").with(Shape.M_DIAMOND).link("a0", "b0"),
                node("a0").with(Style.FILLED, Color.RED.gradient(Color.BLUE)).link("b1"),
                node("b1").link("a2"),
                node("a2").link("end"),
                node("b2").link("end"),
                node("end").with(Shape.M_SQUARE)
        );

        Graphviz.fromGraph(g)
                .processor(new Roughifyer()
                        .bowing(2)
                        .curveStepCount(6)
                        .roughness(1)
                        .fillStyle(FillStyle.hachure().width(2).gap(5).angle(0))
                        .font("*serif", "Comic Sans MS"))
                .render(Format.PNG)
                .toFile(new File("example/ex1-rough.png"));
        //## end

        Graphviz.fromGraph(g)
                .render(Format.PNG)
                .toFile(new File("example/ex1.png"));

        assertTrue(new File("example/ex1.png").exists() && new File("example/ex1-rough.png").exists());
    }
}
