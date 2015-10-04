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

import static guru.nidi.graphviz.Compass.W;
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
            final Graph g = graph("ex1").directed().node(
                    node("main").link(
                            node("parse"), node("init"), node("cleanup"), node("printf")),
                    node("parse").link(
                            node("execute")),
                    node("execute").link(
                            node("make_string"), node("printf"), node("compare")),
                    node("init").link(
                            node("make_string")));
            Graphviz.fromGraph(g).renderToFile(new File("target/ex11.png"), "png", 300, 300);
        });
    }

    @Test
    public void ex12() {
        final Node
                printf = node("printf"),
                make_string = node("make_string");
        final Graph g = graph("ex1").directed().node(
                node("main").attr(Color.rgb("ffcc00"), Style.FILLED).link(
                        node("parse").link(node("execute")
                                .link(make_string, printf, node("compare"))),
                        node("init").link(make_string),
                        node("cleanup"),
                        printf));
        Graphviz.fromGraph(g).renderToFile(new File("target/ex12.png"), "png", 300, 300);
    }

    @Test
    public void ex2() {
        final Node
                main = node("main").attr(Shape.RECTANGLE),
                parse = node("parse"),
                init = node("init"),
                execute = node("execute"),
                compare = node("compare").attr(Shape.RECTANGLE, Style.FILLED, Color.hsv(.7, .3, 1.0)),
                make_string = node("make_string"),
                printf = node("printf");
        final Attribute red = Color.RED;
        final Graph g = graph("ex2").directed().general().attr("size", "4,4").node(
                main.link(
                        to(parse).attr("weight", 8),
                        to(init).attr(Style.DOTTED),
                        node("cleanup"),
                        to(printf).attr(Style.BOLD, Label.of("100 times"), red)),
                parse.link(to(execute)),
                execute.link(to(graph().node(make_string, printf))),
                init.link(to(make_string.attr(Label.of("make a\nstring")))),
                execute.link(to(compare).attr(red)));
        Graphviz.fromGraph(g).renderToFile(new File("target/ex2.png"), "png", 300, 300);
    }

    @Test
    public void ex3() {
        final Node
                a = node("a").attr(Shape.polygon(5, 0, 0), "peripheries", 3, Color.LIGHTBLUE, Style.FILLED),
                c = node("c").attr(Shape.polygon(4, .4, 0), Label.of("hello world")),
                d = node("d").attr(Shape.INV_TRIANGLE),
                e = node("e").attr(Shape.polygon(4, 0, .7));
        final Graph g = graph("ex3").directed().node(
                a.link(node("b").link(c, d)),
                e);
        Graphviz.fromGraph(g).renderToFile(new File("target/ex3.png"), "png", 300, 300);
    }

    @Test
    public void ex41() {
        final Node
                struct1 = node("struct1").attr(Records.label("<f0> left|<f1> mid\\ dle|<f2> right")),
                struct2 = node("struct2").attr(Records.label("<f0> one|<f1> two")),
                struct3 = node("struct3").attr(Records.label("hello\nworld |{ b |{c|<here> d|e}| f}| g | h"));
        final Graph g = graph("ex41").directed().node(
                struct1.link(
                        between(record("f1"), struct2.record("f0")),
                        between(record("f2"), struct3.record("here"))));
        Graphviz.fromGraph(g).renderToFile(new File("target/ex41.png"), "png", 300, 300);
    }

    @Test
    public void ex42() throws IOException {
        CreationContext.begin()
                .graphs().attr(Color.YELLOWGREEN.background())
                .nodes().attr(Color.LIGHTBLUE3.fill(), Style.FILLED, Color.VIOLET.font())
                .links().attr(Style.DOTTED);
        final Node
                struct1 = node("struct1").attr(Records.mOf(rec("f0", "left"), rec("f1", "mid dle"), rec("f2", "right"))),
                struct2 = node("struct2").attr(Records.mOf(rec("f0", "one"), rec("f1", "two"))),
                struct3 = node("struct3").attr(Records.mOf(
                        rec("hello\nworld"),
                        turn(rec("b"),
                                turn(rec("c"), rec("here", "d"), rec("e")),
                                rec("f")),
                        rec("g"), rec("h")));
        final Graph g = graph("ex42").directed().node(
                struct1.link(
                        between(record("f1"), struct2.record("f0")),
                        between(record("f2"), struct3.record("here"))));
        Graphviz.fromGraph(g).renderToFile(new File("target/ex42.png"), "png", 300, 300);
    }

    @Test
    public void ex5() throws IOException {
        final Node
                reiser = node("Reiser cpp"), csh = node("Cshell"),
                ksh = node("ksh"), emacs = node("emacs"),
                vi = node("vi"), build = node("build"),
                bsh = node("Bourne sh"), sccs = node("SCCS"),
                rcs = node("RCS"), make = node("make"),
                yacc = node("yacc"), cron = node("cron"),
                nmake = node("nmake"), ifs = node("IFS"),
                ttu = node("TTU"), cs = node("C*"),
                ncpp = node("ncpp"), kshi = node("ksh-i"),
                curses = node("<curses>"), imx = node("IMX"),
                syned = node("SYNED"), cursesi = node("<curses-i>"),
                pg2 = node("PG2"), peggy = node("Peggy"),
                dag = node("DAG"), csas = node("CSAS"),
                ansiCpp = node("Ansi cpp"), fdelta = node("fdelta"),
                d3fs = node("3D File System"), nmake2 = node("nmake 2.0"),
                cia = node("CIA"), sbcs = node("SBCS"),
                pax = node("PAX"), ksh88 = node("ksh-88"),
                pegasus = node("PEGASUS/PML"), ciapp = node("CIA++"),
                app = node("APP"), ship = node("SHIP"),
                dataShare = node("DataShare"), ryacc = node("Ryacc"),
                mosaic = node("Mosaic"), backtalk = node("backtalk"),
                dot = node("DOT"), dia = node("DIA"),
                libft = node("libft"), coshell = node("CoShell"),
                sfio = node("sfio"), ifsi = node("IFS-i"),
                mlx = node("ML-X"), kyacc = node("kyacc"),
                yeast = node("yeast"), sis = node("Software IS"),
                cfg = node("Configuration Mgt"), archlib = node("Architecture & Libraries"),
                proc = node("Process"), adv = node("Adv. Software Technology");

        final Graph g = graph("ex5").directed()
                .general().attr("ranksep", .75, "size", "7.5,7.5")
                .node().attr(Shape.RECTANGLE)
                .graph(
                        graph().node().attr(Shape.NONE).node(
                                node("past").link(
                                        node("1978").link(
                                                node("1980").link(
                                                        node("1982").link(
                                                                node("1983").link(
                                                                        node("1985").link(
                                                                                node("1986").link(
                                                                                        node("1987").link(
                                                                                                node("1988").link(
                                                                                                        node("1989").link(
                                                                                                                node("1990").link(
                                                                                                                        node("future")))))))))))))
                                .node(bsh, make, sccs, reiser, csh, yacc, cron, rcs, emacs, build, vi, curses),
                        graph().general().attr(Rank.SAME).node().attr(Shape.ELLIPSE).node(sis, cfg, archlib, proc),
                        graph().general().attr(Rank.SAME).node("past").node(sccs, make, bsh, yacc, cron),
                        graph().general().attr(Rank.SAME).node("1978").node(reiser, csh),
                        graph().general().attr(Rank.SAME).node("1980").node(build, emacs, vi),
                        graph().general().attr(Rank.SAME).node("1982").node(rcs, curses, imx, syned),
                        graph().general().attr(Rank.SAME).node("1983").node(ksh, ifs, ttu),
                        graph().general().attr(Rank.SAME).node("1985").node(nmake, peggy),
                        graph().general().attr(Rank.SAME).node("1986").node(cs, ncpp, kshi, cursesi, pg2),
                        graph().general().attr(Rank.SAME).node("1987").node(dag, csas, ansiCpp, fdelta, d3fs, nmake2),
                        graph().general().attr(Rank.SAME).node("1988").node(cia, sbcs, pax, ksh88, pegasus, backtalk),
                        graph().general().attr(Rank.SAME).node("1989").node(ciapp, app, ship, dataShare, ryacc, mosaic),
                        graph().general().attr(Rank.SAME).node("1990").node(dot, dia, libft, coshell, sfio, ifsi, mlx, kyacc, yeast),
                        graph().general().attr(Rank.SAME).node("future").node(adv))
                .node(
                        sccs.link(rcs, nmake, d3fs),
                        make.link(build, nmake),
                        build.link(nmake2),
                        bsh.link(csh.link(ksh), ksh),
                        reiser.link(ncpp),
                        vi.link(curses, ksh),
                        emacs.link(ksh),
                        rcs.link(fdelta, sbcs),
                        curses.link(cursesi),
                        syned.link(peggy),
                        imx.link(ttu),
                        ksh.link(nmake, ksh88),
                        ifs.link(cursesi, sfio, ifsi),
                        ttu.link(pg2),
                        nmake.link(ksh, ncpp, d3fs, nmake2),
                        cs.link(csas),
                        ncpp.link(ansiCpp),
                        cursesi.link(fdelta),
                        csas.link(cia),
                        fdelta.link(sbcs, pax),
                        kshi.link(ksh88),
                        peggy.link(pegasus, ryacc),
                        pg2.link(backtalk),
                        cia.link(ciapp, dia),
                        pax.link(ship),
                        backtalk.link(dataShare),
                        yacc.link(ryacc),
                        dag.link(dot, dia, sis),
                        app.link(dia, sis),
                        nmake2.link(coshell, cfg),
                        ksh88.link(sfio, coshell, archlib),
                        pegasus.link(mlx, archlib),
                        ryacc.link(kyacc),
                        cron.link(yeast),
                        dot.link(sis),
                        ciapp.link(sis),
                        dia.link(sis),
                        libft.link(sis),
                        ansiCpp.link(cfg),
                        sbcs.link(cfg),
                        ship.link(cfg),
                        d3fs.link(cfg),
                        coshell.link(cfg, archlib),
                        sfio.link(archlib),
                        ifsi.link(archlib),
                        mlx.link(archlib),
                        dataShare.link(archlib),
                        kyacc.link(archlib),
                        mosaic.link(proc),
                        yeast.link(proc),
                        sis.link(adv),
                        cfg.link(adv),
                        archlib.link(adv),
                        proc.link(adv)
                );
        Graphviz.fromGraph(g).renderToFile(new File("target/ex5.png"), "png", 1000, 1000);
    }

    @Test
    public void ex6() throws IOException {
        final Node
                node0 = node("node0").attr(Records.of(rec("f0", ""), rec("f1", ""), rec("f2", ""), rec("f3", ""), rec("f4", ""), rec("f5", ""), rec("f6", ""))),
                node1 = node("node1").attr(Records.of(turn(rec("n", "n14"), rec("719"), rec("p", "")))),
                node2 = node("node2").attr(Records.of(turn(rec("n", "a1"), rec("805"), rec("p", "")))),
                node3 = node("node3").attr(Records.of(turn(rec("n", "i9"), rec("718"), rec("p", "")))),
                node4 = node("node4").attr(Records.of(turn(rec("n", "e5"), rec("989"), rec("p", "")))),
                node5 = node("node5").attr(Records.of(turn(rec("n", "t20"), rec("959"), rec("p", "")))),
                node6 = node("node6").attr(Records.of(turn(rec("n", "o15"), rec("794"), rec("p", "")))),
                node7 = node("node7").attr(Records.of(turn(rec("n", "s19"), rec("659"), rec("p", ""))));
        final Graph g = graph("ex6").directed()
                .general().attr(RankDir.LEFT_TO_RIGHT)
                .node(
                        node0.link(
                                between(record("f0"), node1.compass(W)),
                                between(record("f1"), node2.compass(W)),
                                between(record("f2"), node3.compass(W)),
                                between(record("f5"), node4.compass(W)),
                                between(record("f6"), node5.compass(W))),
                        node2.link(between(record("p"), node6.compass(W))),
                        node4.link(between(record("p"), node7.compass(W))));
        Graphviz.fromGraph(g).renderToFile(new File("target/ex6.png"), "png", 300, 300);
    }
}
