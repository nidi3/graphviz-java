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
package guru.nidi.graphviz;

import guru.nidi.graphviz.attribute.*;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Node;

import java.io.File;
import java.io.IOException;

import static guru.nidi.graphviz.attribute.Color.GREY80;
import static guru.nidi.graphviz.attribute.Color.WHITE;
import static guru.nidi.graphviz.attribute.GraphAttr.pad;
import static guru.nidi.graphviz.attribute.Rank.RankDir.LEFT_TO_RIGHT;
import static guru.nidi.graphviz.attribute.Size.Mode.FIXED;
import static guru.nidi.graphviz.attribute.Style.FILLED;
import static guru.nidi.graphviz.engine.Format.PNG;
import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;

final class LogoCreator {
    private LogoCreator() {
    }

    public static void main(String[] args) throws IOException {
        final Image duke = Image.of("graphviz-java/src/test/resources/duke.png");
        final Size size = Size.mode(FIXED).size(30, 20);
        final Node d = node("b").with(Label.of(""), duke, size);
        final Node c = node("\n\n\nJava").with(duke, size).link(d);
        final Node b = node("\n\n\nGraphviz").with(duke, size).link(d);
        final Node a = node("a").with(Label.of(""), duke, size).link(b, c);
        final Style<ForAll> lineWidth = Style.lineWidth(10);
        final Graph g = graph()
                .graphAttr().with(GREY80.gradient(WHITE).background().angle(90), Rank.dir(LEFT_TO_RIGHT), pad(5, 5))
                .linkAttr().with(lineWidth)
                .nodeAttr().with(lineWidth, FILLED, WHITE.fill(), Font.size(140))
                .with(a);
        Graphviz.fromGraph(g).width(1280).height(640).render(PNG).toFile(new File("logo"));
    }
}
