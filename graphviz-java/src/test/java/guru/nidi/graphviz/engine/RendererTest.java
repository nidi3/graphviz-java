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

import guru.nidi.graphviz.model.Graph;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RendererTest {

    @BeforeAll
    static void init() {
        Graphviz.useEngine(new GraphvizV8Engine(), new GraphvizJdkEngine());
    }

    @AfterAll
    static void end() {
        Graphviz.releaseEngine();
    }

    @Test
    void toFileParentFolderNotExists() throws Exception {
        final File expectedFile = new File("target/testFolder/ex1.png");
        testFileExists(expectedFile, expectedFile);
    }

    @Test
    void toFileWithoutExtension() throws Exception {
        final File expectedFile = new File("target/testFolder/ex1.png");
        final File givenFile = new File("target/testFolder/ex1");
        testFileExists(givenFile, expectedFile);
    }

    private void testFileExists(File giveFile, File expectedFile) throws IOException {
        Files.deleteIfExists(expectedFile.toPath());
        Files.deleteIfExists(expectedFile.getParentFile().toPath());

        final Graph graph = graph("example1").directed().with(node("a").link(node("b")));
        Graphviz.fromGraph(graph).width(200).render(Format.PNG).toFile(giveFile);

        assertTrue(expectedFile.exists() && expectedFile.isFile());
    }

    @Test
    void toFileNoParentFolder() throws Exception {
        final File file = new File("test.png");
        Files.deleteIfExists(file.toPath());
        try {
            final Graph graph = graph("example1").directed().with(node("a").link(node("b")));
            Graphviz.fromGraph(graph).width(200).render(Format.PNG).toFile(file);
            assertTrue(file.exists());
        } finally {
            Files.deleteIfExists(file.toPath());
        }
    }

}