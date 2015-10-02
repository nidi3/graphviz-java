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

import guru.nidi.graphviz.attribute.*;
import org.junit.Test;

import java.io.File;

import static guru.nidi.graphviz.Factory.*;
import static guru.nidi.graphviz.Link.to;
import static guru.nidi.graphviz.attribute.Record.item;
import static guru.nidi.graphviz.attribute.Record.turn;

/**
 *
 */
public class ExampleTest {
    @Test
    public void ex11() {
        new NodeContext(() -> {
            final Graph g = graph("ex1").directed().with(
                    node("main").links(
                            to(node("parse")), to(node("init")), to(node("cleanup")), to(node("printf"))),
                    node("parse").link(
                            to(node("execute"))),
                    node("execute").links(
                            to(node("make_string")), to(node("printf")), to(node("compare"))),
                    node("init").link(
                            to(node("make_string"))));
            Graphviz.fromGraph(g).renderToFile(new File("target/ex11.png"), "png", 300, 300);
        });
    }

    @Test
    public void ex12() {
        final Node
                printf = node("printf"),
                make_string = node("make_string");
        final Graph g = graph("ex1").directed().with(
                node("main").attrs(Color.rgb("ffcc00"), Style.FILLED).links(
                        to(node("parse")
                                .link(to(node("execute")
                                        .links(to(make_string), to(printf), to(node("compare")))))),
                        to(node("init")
                                .link(to(make_string))),
                        to(node("cleanup")),
                        to(printf)));
        Graphviz.fromGraph(g).renderToFile(new File("target/ex12.png"), "png", 300, 300);
    }

    @Test
    public void ex2() {
        final Node
                main = node("main").attrs(Shape.RECTANGLE),
                parse = node("parse"),
                init = node("init"),
                execute = node("execute"),
                compare = node("compare").attrs(Shape.RECTANGLE, Style.FILLED, Color.hsv(.7, .3, 1.0)),
                make_string = node("make_string"),
                printf = node("printf");
        final Attribute red = Color.RED;
        final Graph g = graph("ex2").directed().attr("size", "4,4").with(
                main.links(
                        to(parse).attr("weight", 8),
                        to(init).attrs(Style.DOTTED),
                        to(node("cleanup")),
                        to(printf).attrs(Style.BOLD, Label.of("100 times"), red)),
                parse.link(to(execute)),
                execute.link(to(graph().with(make_string, printf))),
                init.link(to(make_string.attrs(Label.of("make a\nstring")))),
                execute.link(to(compare).attrs(red)));
        Graphviz.fromGraph(g).renderToFile(new File("target/ex2.png"), "png", 300, 300);
    }

    @Test
    public void ex3() {
        final Node
                a = node("a").attrs(Shape.polygon(5, 0, 0), "peripheries", 3, Color.LIGHTBLUE, Style.FILLED),
                c = node("c").attrs(Shape.polygon(4, .4, 0), Label.of("hello world")),
                d = node("d").attrs(Shape.INV_TRIANGLE),
                e = node("e").attrs(Shape.polygon(4, 0, .7));
        final Graph g = graph("ex3").directed().with(
                a.link(to(node("b").links(to(c), to(d)))),
                e);
        Graphviz.fromGraph(g).renderToFile(new File("target/ex3.png"), "png", 300, 300);
    }

    @Test
    public void ex41() {
        final Node
                struct1 = node("struct1").attrs(Record.label("<f0> left|<f1> mid\\ dle|<f2> right")),
                struct2 = node("struct2").attrs(Record.label("<f0> one|<f1> two")),
                struct3 = node("struct3").attrs(Record.label("hello\nworld |{ b |{c|<here> d|e}| f}| g | h"));
        final Graph g = graph("ex41").directed().with(
                struct1.links(
                        between(record("f1"), struct2.record("f0")),
                        between(record("f2"), struct3.record("here"))));
        Graphviz.fromGraph(g).renderToFile(new File("target/ex41.png"), "png", 300, 300);
    }

    @Test
    public void ex42() {
        final Node
                struct1 = node("struct1").attrs(Record.mItems(item("f0", "left"), item("f1", "mid dle"), item("f2", "right"))),
                struct2 = node("struct2").attrs(Record.mItems(item("f0", "one"), item("f1", "two"))),
                struct3 = node("struct3").attrs(Record.mItems(
                        item("hello\nworld"),
                        turn(item("b"),
                                turn(item("c"), item("here", "d"), item("e")),
                                item("f")),
                        item("g"), item("h")));
        final Graph g = graph("ex42").directed().with(
                struct1.links(
                        between(record("f1"), struct2.record("f0")),
                        between(record("f2"), struct3.record("here"))));
        Graphviz.fromGraph(g).renderToFile(new File("target/ex42.png"), "png", 300, 300);
    }

}
