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

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.engine.Graphviz;
import org.junit.Test;

import java.io.File;

import static guru.nidi.graphviz.Factory.graph;
import static guru.nidi.graphviz.Factory.node;
import static guru.nidi.graphviz.Link.to;

/**
 *
 */
public class ReadmeTest {
    @Test
    public void ex1() {
        Graph g = graph("example1").directed().node(node("a").link(node("b")));
        Graphviz.fromGraph(g).renderToFile(new File("example/ex1.png"), "png", 300, 300);
    }

    @Test
    public void ex2() {
        Node
                init = node("init"),
                execute = node("execute"),
                compare = node("compare").attr(Shape.RECTANGLE, Style.FILLED, Color.hsv(.7, .3, 1.0)),
                mkString = node("mkString").attr(Label.of("make a\nstring")),
                printf = node("printf");

        Graph g = graph("example2").directed().node(
                node("main").attr(Shape.RECTANGLE).link(
                        to(node("parse").link(execute)).attr("weight", 8),
                        to(init).attr(Style.DOTTED),
                        node("cleanup"),
                        to(printf).attr(Style.BOLD, Label.of("100 times"), Color.RED)),
                execute.link(
                        graph().node(mkString, printf),
                        to(compare).attr(Color.RED)),
                init.link(mkString));

        Graphviz.fromGraph(g).renderToFile(new File("example/ex2.png"), "png", 300, 300);
    }

}
