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

import guru.nidi.graphviz.model.Graph;
import org.junit.Test;

import java.io.File;

import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;
import static org.junit.Assert.*;


public class RendererTest {

    @Test
    public void toFile_parentFolderNotExists() throws Exception {
        Graph graph = graph("example1").directed().with(node("a").link(node("b")));

        File expectedFile = new File("target/testFolder/ex1.png");
        Graphviz.fromGraph(graph).width(200).render(Format.PNG).toFile(expectedFile);

        assertTrue(expectedFile.exists());

        expectedFile.delete();
        expectedFile.getParentFile().delete();
    }

}