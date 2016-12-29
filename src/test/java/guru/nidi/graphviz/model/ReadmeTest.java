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
import guru.nidi.graphviz.engine.Graphviz;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static guru.nidi.graphviz.attribute.Records.rec;
import static guru.nidi.graphviz.attribute.Records.turn;
import static guru.nidi.graphviz.model.Compass.*;
import static guru.nidi.graphviz.model.Factory.*;
import static guru.nidi.graphviz.model.Link.to;

public class ReadmeTest {
    @Test
    public void ex1() {
        Graph g = graph("example1").directed().nodes(node("a").link(node("b")));
        Graphviz.fromGraph(g).renderToFile(new File("example/ex1.png"));
    }

    @Test
    public void ex2() {
        Node
                init = node("init"),
                execute = node("execute"),
                compare = node("compare").attr(Shape.RECTANGLE, Style.FILLED, Color.hsv(.7, .3, 1.0)),
                mkString = node("mkString").attr(Label.of("make a\nstring")),
                printf = node("printf");

        Graph g = graph("example2").directed().nodes(
                node("main").attr(Shape.RECTANGLE).link(
                        to(node("parse").link(execute)).attr("weight", 8),
                        to(init).attr(Style.DOTTED),
                        node("cleanup"),
                        to(printf).attr(Style.BOLD, Label.of("100 times"), Color.RED)),
                execute.link(
                        graph().nodes(mkString, printf),
                        to(compare).attr(Color.RED)),
                init.link(mkString));

        Graphviz.fromGraph(g).renderToFile(new File("example/ex2.png"));
    }

    @Test
    public void ex3() throws IOException {
        Node
                node0 = node("node0").attr(Records.of(rec("f0", ""), rec("f1", ""), rec("f2", ""), rec("f3", ""), rec("f4", ""))),
                node1 = node("node1").attr(Records.of(turn(rec("n4"), rec("v", "719"), rec("")))),
                node2 = node("node2").attr(Records.of(turn(rec("a1"), rec("805"), rec("p", "")))),
                node3 = node("node3").attr(Records.of(turn(rec("i9"), rec("718"), rec("")))),
                node4 = node("node4").attr(Records.of(turn(rec("e5"), rec("989"), rec("p", "")))),
                node5 = node("node5").attr(Records.of(turn(rec("t2"), rec("v", "959"), rec("")))),
                node6 = node("node6").attr(Records.of(turn(rec("o1"), rec("794"), rec("")))),
                node7 = node("node7").attr(Records.of(turn(rec("s7"), rec("659"), rec(""))));
        Graph g = graph("example3").directed()
                .general().attr(RankDir.LEFT_TO_RIGHT)
                .nodes(
                        node0.link(
                                between(loc("f0"), node1.loc("v", SOUTH)),
                                between(loc("f1"), node2.loc(WEST)),
                                between(loc("f2"), node3.loc(WEST)),
                                between(loc("f3"), node4.loc(WEST)),
                                between(loc("f4"), node5.loc("v", NORTH))),
                        node2.link(between(loc("p"), node6.loc(NORTH_WEST))),
                        node4.link(between(loc("p"), node7.loc(SOUTH_WEST))));
        Graphviz.fromGraph(g).renderToFile(new File("example/ex3.png"));
    }

}
