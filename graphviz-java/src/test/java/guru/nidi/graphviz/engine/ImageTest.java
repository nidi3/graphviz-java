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

import guru.nidi.graphviz.attribute.Image;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;

class ImageTest {
    @BeforeAll
    static void init() {
        Graphviz.useEngine(new GraphvizV8Engine());
    }

    @Test
    void relative() throws IOException {
        Graphviz g = Graphviz.fromGraph(graph()
                .with(node(" ").with(Image.of("example/graphviz.png"))));
        g.render(Format.PNG).toFile(new File("target/img-relative.png"));
    }

    @Test
    void relativeWithBase() throws IOException {
        Graphviz g = Graphviz.fromGraph(graph()
                .with(node(" ").with(Image.of("graphviz.png"))));
        g.basedir(new File("example")).render(Format.PNG).toFile(new File("target/img-base-relative.png"));
    }

    @Test
    void absolute() throws IOException {
        Graphviz g = Graphviz.fromGraph(graph()
                .with(node(" ").with(Image.of(new File("example/graphviz.png").getAbsolutePath()))));
        g.render(Format.PNG).toFile(new File("target/img-absolute.png"));
    }

    @Test
    void http() throws IOException {
        Graphviz g = Graphviz.fromGraph(graph()
                .with(node(" ").with(Image.of("https://raw.githubusercontent.com/nidi3/graphviz-java/master/graphviz-java/example/ex7.png"))));
        g.render(Format.PNG).toFile(new File("target/img-http.png"));
    }
}
