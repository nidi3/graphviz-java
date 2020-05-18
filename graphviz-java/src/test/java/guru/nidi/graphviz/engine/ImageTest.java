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
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;

import static guru.nidi.graphviz.engine.Format.PNG;
import static guru.nidi.graphviz.engine.Format.SVG;
import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;
import static guru.nidi.graphviz.service.SystemUtils.uriPathOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImageTest {
    @BeforeEach
    void init(RepetitionInfo info) {
        //on CI we don't have dot installed
        if (info.getCurrentRepetition() == 1 || System.getenv("CI") != null) {
            Graphviz.useEngine(new GraphvizV8Engine());
        } else {
            Graphviz.useEngine(new GraphvizCmdLineEngine());
        }
    }

    @RepeatedTest(2)
    void relative() throws IOException {
        final Graphviz g = Graphviz.fromGraph(graph().with(node(" ").with(Image.of("example/graphviz.png"))));
        final File out = new File("target/img-relative.png");
        g.render(PNG).toFile(out);
        assertTrue(out.length() > 15000);
        assertThat(g.render(SVG).toString(), Matchers.containsString("\"example/graphviz.png\""));
    }

    @RepeatedTest(2)
    void relativeWithRelativeBase() throws IOException {
        final Graphviz g = Graphviz.fromGraph(graph().with(node(" ").with(Image.of("graphviz.png")))).basedir(new File("example"));
        final File out = new File("target/img-base-relative.png");
        g.render(PNG).toFile(out);
        assertTrue(out.length() > 15000);
        assertThat(g.render(SVG).toString(), Matchers.containsString("\"example/graphviz.png\""));
    }

    @RepeatedTest(2)
    void relativeWithAbsoluteBase() throws IOException {
        final File absBase = new File("example").getAbsoluteFile();
        final Graphviz g = Graphviz.fromGraph(graph().with(node(" ").with(Image.of("graphviz.png")))).basedir(absBase);
        final File out = new File("target/img-base-relative.png");
        g.render(PNG).toFile(out);
        assertTrue(out.length() > 15000);
        assertThat(g.render(SVG).toString(), Matchers.containsString("\"" + uriPathOf(absBase) + "/graphviz.png\""));
    }

    @RepeatedTest(2)
    void absolute() throws IOException {
        final String abs = new File("example/graphviz.png").getAbsolutePath();
        final Graphviz g = Graphviz.fromGraph(graph().with(node(" ").with(Image.of(abs))));
        final File out = new File("target/img-absolute.png");
        g.render(PNG).toFile(out);
        assertTrue(out.length() > 15000);
        assertThat(g.render(SVG).toString(), Matchers.containsString("\"" + abs + "\""));
    }

    @RepeatedTest(2)
    void http() throws IOException {
        final String url = "https://raw.githubusercontent.com/nidi3/graphviz-java/master/graphviz-java/example/ex7.png";
        final Graphviz g = Graphviz.fromGraph(graph().with(node(" ").with(Image.of(url))));
        final File out = new File("target/img-http.png");
        g.render(PNG).toFile(out);
        assertTrue(out.length() > 15000);
        assertThat(g.render(SVG).toString(), Matchers.containsString("\"" + url + "\""));
    }
}
