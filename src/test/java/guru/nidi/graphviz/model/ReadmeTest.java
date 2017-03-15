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
package guru.nidi.graphviz.model;

import guru.nidi.graphviz.attribute.*;
import guru.nidi.graphviz.engine.Engine;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.parse.Parser;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static guru.nidi.graphviz.attribute.Records.rec;
import static guru.nidi.graphviz.attribute.Records.turn;
import static guru.nidi.graphviz.engine.Engine.NEATO;
import static guru.nidi.graphviz.engine.Format.JSON;
import static guru.nidi.graphviz.engine.Format.SVG;
import static guru.nidi.graphviz.model.Compass.*;
import static guru.nidi.graphviz.model.Factory.*;
import static guru.nidi.graphviz.model.Link.to;

public class ReadmeTest {
    @Test
    public void ex1() throws IOException {
        //## basic
        Graph g = graph("example1").directed().with(node("a").link(node("b")));
        Graphviz.fromGraph(g).render().toFile(new File("example/ex1.png"));
        //## end
    }

    @Test
    public void ex2() throws IOException {
        //## complex
        Node
                init = node("init"),
                execute = node("execute"),
                compare = node("compare").with(Shape.RECTANGLE, Style.FILLED, Color.hsv(.7, .3, 1.0)),
                mkString = node("mkString").with(Label.of("make a\nstring")),
                printf = node("printf");

        Graph g = graph("example2").directed().with(
                node("main").with(Shape.RECTANGLE).link(
                        to(node("parse").link(execute)).with("weight", 8),
                        to(init).with(Style.DOTTED),
                        node("cleanup"),
                        to(printf).with(Style.BOLD, Label.of("100 times"), Color.RED)),
                execute.link(
                        graph().with(mkString, printf),
                        to(compare).with(Color.RED)),
                init.link(mkString));

        Graphviz.fromGraph(g).render().toFile(new File("example/ex2.png"));
        //## end
    }

    @Test
    public void ex3() throws IOException {
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
                .generalAttr().with(RankDir.LEFT_TO_RIGHT)
                .with(
                        node0.link(
                                between(loc("f0"), node1.loc("v", SOUTH)),
                                between(loc("f1"), node2.loc(WEST)),
                                between(loc("f2"), node3.loc(WEST)),
                                between(loc("f3"), node4.loc(WEST)),
                                between(loc("f4"), node5.loc("v", NORTH))),
                        node2.link(between(loc("p"), node6.loc(NORTH_WEST))),
                        node4.link(between(loc("p"), node7.loc(SOUTH_WEST))));
        Graphviz.fromGraph(g).render().toFile(new File("example/ex3.png"));
        //## end
    }

    @Test
    public void ex4() throws IOException {
        //## manipulate
        MutableGraph g = Parser.read(getClass().getResourceAsStream("/color.dot"));
        Graphviz.fromGraph(g).render().toFile(new File("example/ex4-1.png"));

        g.generalAttrs()
                .add(Color.WHITE.gradient(Color.rgb("888888")).background().angle(90))
                .nodeAttrs().add(Color.WHITE.fill())
                .allNodes().forEach(node ->
                node.add(
                        Color.named(node.label().toString()),
                        Style.lineWidth(4).and(Style.FILLED)));
        Graphviz.fromGraph(g).render().toFile(new File("example/ex4-2.png"));
        //## end
    }

    @Test
    public void ex5() throws IOException {
        //## config
        Graph g = graph("example5").directed().with(node("a").link(node("b")));
        Graphviz viz = Graphviz.fromGraph(g);
        viz.width(200).engine(NEATO).render().toFile(new File("example/ex5.png"));
        viz.render(SVG).toFile(new File("example/ex5.svg"));
        String json = viz.render(JSON).toString();
        BufferedImage image = viz.render().toImage();
        //## end
    }

}
