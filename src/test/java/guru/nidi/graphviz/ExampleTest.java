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
import guru.nidi.graphviz.engine.Graphviz;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static guru.nidi.graphviz.Factory.*;
import static guru.nidi.graphviz.Link.to;
import static guru.nidi.graphviz.attribute.Records.rec;
import static guru.nidi.graphviz.attribute.Records.turn;

/**
 *
 */
public class ExampleTest {
    @After
    public void closeContext() {
        CreationContext.end();
    }

    @Test
    public void ex11() {
        new CreationContext(cc -> {
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
        final Graph g = graph("ex2").directed().general().attr("size", "4,4").with(
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
                struct1 = node("struct1").attrs(Records.label("<f0> left|<f1> mid\\ dle|<f2> right")),
                struct2 = node("struct2").attrs(Records.label("<f0> one|<f1> two")),
                struct3 = node("struct3").attrs(Records.label("hello\nworld |{ b |{c|<here> d|e}| f}| g | h"));
        final Graph g = graph("ex41").directed().with(
                struct1.links(
                        between(record("f1"), struct2.record("f0")),
                        between(record("f2"), struct3.record("here"))));
        Graphviz.fromGraph(g).renderToFile(new File("target/ex41.png"), "png", 300, 300);
    }

    @Test
    public void ex42() throws IOException {
        CreationContext.begin()
                .graphs().attrs(Color.YELLOWGREEN.background())
                .nodes().attrs(Color.LIGHTBLUE3.fill(), Style.FILLED, Color.VIOLET.font())
                .links().attrs(Style.DOTTED);
        final Node
                struct1 = node("struct1").attrs(Records.mOf(rec("f0", "left"), rec("f1", "mid dle"), rec("f2", "right"))),
                struct2 = node("struct2").attrs(Records.mOf(rec("f0", "one"), rec("f1", "two"))),
                struct3 = node("struct3").attrs(Records.mOf(
                        rec("hello\nworld"),
                        turn(rec("b"),
                                turn(rec("c"), rec("here", "d"), rec("e")),
                                rec("f")),
                        rec("g"), rec("h")));
        final Graph g = graph("ex42").directed().with(
                struct1.links(
                        between(record("f1"), struct2.record("f0")),
                        between(record("f2"), struct3.record("here"))));
        Graphviz.fromGraph(g).renderToFile(new File("target/ex42.png"), "png", 300, 300);
    }

    @Test
    public void ex5() throws IOException {
        final Node
                ksh = node("ksh"),
                nmake = node("nmake");

        final Graph g = graph("ex5").directed().general().attrs("ranksep", .75, "size", "7.5,7.5").with(
                graph().with(
                        node("past").link(to(
                                node("1978").link(to(
                                        node("1980").link(to(
                                                node("1982").link(to(
                                                        node("1983").link(to(
                                                                node("1985")
                                                        )))))))))))
                        .nodes(
                                "Bourne sh", "make", "SCCS", "yacc", "cron", "Reiser cpp", "Cshell", "emacs",
                                "build", "vi", "<curses>", "RCS", "C"
                        ),
                graph().general().attrs(Rank.SAME).nodes("Software IS", "Configuration Mgt", "Architecture & Libraries", "Process"),
                graph().general().attrs(Rank.SAME).nodes("past", "scss", "make", "Bourne sh", "yacc", "cron"),
                graph().general().attrs(Rank.SAME).nodes("1978", "Reiser cpp", "Cshell"),
                graph().general().attrs(Rank.SAME).nodes("1980", "build", "emacs", "vi"),
                graph().general().attrs(Rank.SAME).nodes("1982", "rcs", "curses", "IMX", "SYNED"),
                graph().general().attrs(Rank.SAME).nodes("1983", "ksh", "IFS", "TTU"),
                graph().general().attrs(Rank.SAME).nodes("1985", "nmake", "Peggy"))
                .with(
                        node("scss").links(to(node("rcs")), to(nmake)),
                        node("make").links(to(node("build")), to(nmake)),
                        node("bsh").links(to(node("csh").link(to(ksh))), to(ksh)),
                        ksh.link(to(nmake)),
                        nmake.link(to(ksh)),
                        node("vi").link(to(ksh)),
                        node("emacs").link(to(ksh)),
                        node("SYNED").link(to(node("Peggy"))),
                        node("IMX").link(to(node("TTU")))
                );
        Graphviz.fromGraph(g).renderToFile(new File("target/ex5.png"), "png", 1000, 1000);

    }
}
