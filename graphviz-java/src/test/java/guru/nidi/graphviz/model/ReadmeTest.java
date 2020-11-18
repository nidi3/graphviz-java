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

import guru.nidi.graphviz.attribute.*;
import guru.nidi.graphviz.engine.*;
import guru.nidi.graphviz.parse.Parser;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import java.awt.image.BufferedImage;
import java.io.*;

import static guru.nidi.graphviz.attribute.Attributes.attr;
import static guru.nidi.graphviz.attribute.Label.Justification.LEFT;
import static guru.nidi.graphviz.attribute.Rank.RankDir.LEFT_TO_RIGHT;
import static guru.nidi.graphviz.attribute.Records.rec;
import static guru.nidi.graphviz.attribute.Records.turn;
import static guru.nidi.graphviz.model.Compass.*;
import static guru.nidi.graphviz.model.Factory.*;

class ReadmeTest {

    @BeforeAll
    static void init() {
        Graphviz.useEngine(new GraphvizV8Engine(), new GraphvizJdkEngine());
    }

    @AfterAll
    static void end() {
        Graphviz.releaseEngine();
    }

    @Test
    void ex1() throws IOException {
        //## basic
        Graph g = graph("example1").directed()
                .graphAttr().with(Rank.dir(LEFT_TO_RIGHT))
                .nodeAttr().with(Font.name("arial"))
                .linkAttr().with("class", "link-class")
                .with(
                        node("a").with(Color.RED).link(node("b")),
                        node("b").link(
                                to(node("c")).with(attr("weight", 5), Style.DASHED)
                        )
                );
        Graphviz.fromGraph(g).height(100).render(Format.PNG).toFile(new File("example/ex1.png"));
        //##
    }

    @Test
    void ex1m() throws IOException {
        //## mutable
        MutableGraph g = mutGraph("example1").setDirected(true).add(
                mutNode("a").add(Color.RED).addLink(mutNode("b")));
        Graphviz.fromGraph(g).width(200).render(Format.PNG).toFile(new File("example/ex1m.png"));
        //##
    }

    @Test
    void ex1i() throws IOException {
        //## imperative
        MutableGraph g = mutGraph("example1").setDirected(true).use((gr, ctx) -> {
            mutNode("b");
            nodeAttrs().add(Color.RED);
            mutNode("a").addLink(mutNode("b"));
        });
        Graphviz.fromGraph(g).width(200).render(Format.PNG).toFile(new File("example/ex1i.png"));
        //##
    }

    @Test
    void ex2() throws IOException {
        //## complex
        Node
                main = node("main").with(Label.html("<b>main</b><br/>start"), Color.rgb("1020d0").font()),
                init = node(Label.markdown("**_init_**")),
                execute = node("execute"),
                compare = node("compare").with(Shape.RECTANGLE, Style.FILLED, Color.hsv(.7, .3, 1.0)),
                mkString = node("mkString").with(Label.lines(LEFT, "make", "a", "multi-line")),
                printf = node("printf");

        Graph g = graph("example2").directed().with(
                main.link(
                        to(node("parse").link(execute)).with(LinkAttr.weight(8)),
                        to(init).with(Style.DOTTED),
                        node("cleanup"),
                        to(printf).with(Style.BOLD, Label.of("100 times"), Color.RED)),
                execute.link(
                        graph().with(mkString, printf),
                        to(compare).with(Color.RED)),
                init.link(mkString));

        Graphviz.fromGraph(g).width(900).render(Format.PNG).toFile(new File("example/ex2.png"));
        //##
    }

    @Test
    void ex3() throws IOException {
        //## records
        Node
                node0 = node("node0").with(Records.of(rec("f0", ""), rec("f1", ""), rec("f2", ""), rec("f3", ""), rec("f4", ""))),
                node1 = node("node1").with(Records.of(turn(rec("n4"), rec("v", "719"), rec("")))),
                node2 = node("node2").with(Records.of(turn(rec("a1"), rec("805"), rec("p", "")))),
                node3 = node("node3").with(Records.of(turn(rec("i9"), rec("718"), rec("")))),
                node4 = node("node4").with(Records.of(turn(rec("e5"), rec("989"), rec("p", "")))),
                node5 = node("node5").with(Records.of(turn(rec("t2"), rec("v", "959"), rec("")))),
                node6 = node("node6").with(Records.of(turn(rec("o1"), rec("794"), rec("")))),
                node7 = node("node7").with(Records.of(turn(rec("s7"), rec("659"), rec(""))));
        Graph g = graph("example3").directed()
                .graphAttr().with(Rank.dir(LEFT_TO_RIGHT))
                .with(
                        node0.link(
                                between(port("f0"), node1.port("v", SOUTH)),
                                between(port("f1"), node2.port(WEST)),
                                between(port("f2"), node3.port(WEST)),
                                between(port("f3"), node4.port(WEST)),
                                between(port("f4"), node5.port("v", NORTH))),
                        node2.link(between(port("p"), node6.port(NORTH_WEST))),
                        node4.link(between(port("p"), node7.port(SOUTH_WEST))));
        Graphviz.fromGraph(g).width(900).render(Format.PNG).toFile(new File("example/ex3.png"));
        //##
    }

    @Test
    void ex4() throws IOException {
        //## manipulate
        try (InputStream dot = getClass().getResourceAsStream("/color.dot")) {
            MutableGraph g = new Parser().read(dot);
            Graphviz.fromGraph(g).width(700).render(Format.PNG).toFile(new File("example/ex4-1.png"));

            g.graphAttrs()
                    .add(Color.WHITE.gradient(Color.rgb("888888")).background().angle(90))
                    .nodeAttrs().add(Color.WHITE.fill())
                    .nodes().forEach(node ->
                    node.add(
                            Color.named(node.name().toString()),
                            Style.lineWidth(4), Style.FILLED));
            Graphviz.fromGraph(g).width(700).render(Format.PNG).toFile(new File("example/ex4-2.png"));
        }
        //##
    }

    @Test
    @DisabledIfEnvironmentVariable(named = "CI", matches = ".*")
    void ex5() throws IOException {
        //## config
        Graphviz.useEngine(new GraphvizCmdLineEngine()); // Rasterizer.builtIn() works only with CmdLineEngine
        Graph g = graph("example5").directed().with(node("abc").link(node("xyz")));
        Graphviz viz = Graphviz.fromGraph(g);
        viz.width(200).render(Format.SVG).toFile(new File("example/ex5.svg"));
        viz.width(200).rasterize(Rasterizer.BATIK).toFile(new File("example/ex5b.png"));
        viz.width(200).rasterize(Rasterizer.SALAMANDER).toFile(new File("example/ex5s.png"));
        viz.width(200).rasterize(Rasterizer.builtIn("pdf")).toFile(new File("example/ex5p"));
        String dot = viz.render(Format.DOT).toString();
        String json = viz.engine(Engine.NEATO).render(Format.JSON).toString();
        BufferedImage image = viz.render(Format.PNG).toImage();
        //##
        end();
        init();
    }

    @Test
    @DisabledIfEnvironmentVariable(named = "CI", matches = ".*")
    void ex7() throws IOException {
        //## img
        Graphviz.useEngine(new GraphvizCmdLineEngine());
        Graphviz g = Graphviz.fromGraph(graph()
                .with(node(Label.html("<table border='0'><tr><td><img src='graphviz.png' /></td></tr></table>"))));
        g.basedir(new File("example")).render(Format.PNG).toFile(new File("example/ex7.png"));
        //##
    }

    @Test
    void ex8() throws IOException {
        //## image
        Graphviz g = Graphviz.fromGraph(graph()
                .with(node(" ").with(Size.std().margin(.8, .7), Image.of("graphviz.png"))));
        g.basedir(new File("example")).render(Format.PNG).toFile(new File("example/ex8.png"));
        //##
    }

    @Test
    void ex9() throws IOException {
        //## processor
        Graph graph = graph().with(node("bad word").link("good word"));
        Graphviz g = Graphviz.fromGraph(graph)
                .preProcessor((source, options, processOptions) -> source.replace("bad word", "unicorn"))
                .postProcessor((result, options, processOptions) ->
                        result.mapString(svg ->
                                SvgElementFinder.use(svg, finder -> {
                                    finder.findNode("unicorn").setAttribute("class", "pink");
                                })));
        g.render(Format.PNG).toFile(new File("example/ex9.png"));
        //##
    }
}
