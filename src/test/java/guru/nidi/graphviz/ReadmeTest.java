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
package guru.nidi.graphviz;

import guru.nidi.graphviz.engine.Graphviz;
import org.junit.Test;

import java.io.File;

import static guru.nidi.graphviz.Factory.graph;
import static guru.nidi.graphviz.Factory.node;

/**
 *
 */
public class ReadmeTest {
    @Test
    public void ex1() {
        final Graph g = graph("example").directed().node(node("a").link(node("b")));
        Graphviz.fromGraph(g).renderToFile(new File("example/ex1.png"), "png", 300, 300);
    }

}
