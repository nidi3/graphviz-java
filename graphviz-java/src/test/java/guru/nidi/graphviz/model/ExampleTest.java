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

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Font;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.*;
import guru.nidi.graphviz.engine.*;
import org.junit.jupiter.api.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static guru.nidi.graphviz.attribute.Attributes.attr;
import static guru.nidi.graphviz.attribute.Attributes.attrs;
import static guru.nidi.graphviz.attribute.Rank.RankDir.LEFT_TO_RIGHT;
import static guru.nidi.graphviz.attribute.Rank.RankType.SAME;
import static guru.nidi.graphviz.attribute.Records.rec;
import static guru.nidi.graphviz.attribute.Records.turn;
import static guru.nidi.graphviz.engine.Format.*;
import static guru.nidi.graphviz.engine.Rasterizer.BATIK;
import static guru.nidi.graphviz.engine.Rasterizer.SALAMANDER;
import static guru.nidi.graphviz.model.Compass.WEST;
import static guru.nidi.graphviz.model.Factory.*;
import static guru.nidi.graphviz.model.Link.between;
import static guru.nidi.graphviz.model.Link.to;

class ExampleTest {
    @BeforeAll
    static void init() {
        Graphviz.useEngine(new GraphvizV8Engine(), new GraphvizJdkEngine());
    }

    @AfterAll
    static void end() {
        Graphviz.releaseEngine();
    }

    @Test
    void ex1a() throws IOException {
        final Graph g = CreationContext.use(ctx -> graph("ex1a").directed().with(
                node("main").link(
                        node("parse"), node("init"), node("cleanup"), node("printf")),
                node("parse").link(
                        node("execute")),
                node("execute").link(
                        node("make_string"), node("printf"), node("compare")),
                node("init").link(
                        node("make_string"))));
        Graphviz.fromGraph(g).render(PNG).toFile(new File("target/ex1a.png"));
    }

    @Test
    void ex1b() throws IOException {
        final Node
                printf = node("printf"),
                make_string = node("make_string");
        final Graph g = graph().graphAttr().with("dpi", 300).directed().with(
                node("main").with(Color.rgb("ffcc00"), Style.FILLED).link(
                        node("parse").link(node("execute")
                                .link(make_string, printf, node("compare"))),
                        node("init").link(make_string),
                        node("cleanup"),
                        printf));
        Graphviz.fromGraph(g).render(PNG).toFile(new File("target/ex1b.png"));
    }

    @Test
    void ex2() throws IOException {
        final Node
                init = node("init"),
                execute = node("execute"),
                compare = node("compare").with(Shape.RECTANGLE, Style.FILLED, Color.hsv(.7, .3, 1.0)),
                make_string = node("make_string").with(Label.of("make a\nstring")),
                printf = node("printf");
        final Graph g = graph("ex2").directed()
                .graphAttr().with(Color.rgb("222222").background())
                .nodeAttr().with(Font.config("Arial", 15), Color.rgb("bbbbbb").fill(), Style.FILLED)
                .with(
                        node("main").with(Shape.RECTANGLE).link(
                                to(node("parse").link(execute)).with(LinkAttr.weight(8)),
                                to(init).with(Style.DOTTED),
                                node("cleanup"),
                                to(printf).with(Style.BOLD, Label.of("100 times"), Color.RED)),
                        execute.link(graph().with(make_string, printf), to(compare).with(Color.RED)),
                        init.link(make_string));
        final Graphviz graphviz = Graphviz.fromGraph(g);
        graphviz.render(PNG).toFile(new File("target/ex2.png"));
        graphviz.render(PNG).withGraphics(gr -> {
            gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }).toFile(new File("target/ex2-anti-all-on.png"));
        graphviz.render(PNG).withGraphics(gr -> {
            gr.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
            gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        }).toFile(new File("target/ex2-anti-off.png"));
        graphviz.render(PNG).withGraphics(gr -> {
            gr.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
            gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        }).toFile(new File("target/ex2-anti-gasp.png"));
        graphviz.render(PNG).withGraphics(gr -> {
            gr.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        }).toFile(new File("target/ex2-anti-on.png"));
        graphviz.engine(Engine.CIRCO).render(PNG).toFile(new File("target/ex2-ci.png"));
        graphviz.engine(Engine.NEATO).render(PNG).toFile(new File("target/ex2-ne.png"));
        graphviz.engine(Engine.OSAGE).render(PNG).toFile(new File("target/ex2-os.png"));
        graphviz.engine(Engine.TWOPI).render(PNG).toFile(new File("target/ex2-tp.png"));
        graphviz.engine(Engine.FDP).render(PNG).toFile(new File("target/ex2-fdp.png"));
        graphviz.render(SVG).toFile(new File("target/ex2.svg"));
        graphviz.render(JSON).toFile(new File("target/ex2.json"));
        graphviz.render(JSON0).toFile(new File("target/ex2.json0"));
        graphviz.render(PS).toFile(new File("target/ex2.ps"));
        graphviz.render(PS2).toFile(new File("target/ex2.ps2"));
        graphviz.render(PLAIN).toFile(new File("target/ex2.plain"));
        graphviz.render(PLAIN_EXT).toFile(new File("target/ex2.plain-ext"));
    }

    @Test
    void ex3() throws IOException {
        final Node
                a = node("a").with(Shape.polygon(5).rotation(20), attr("peripheries", 3), Color.LIGHTBLUE, Style.FILLED),
                c = node("c").with(Shape.polygon(4).skew(.4), Label.html("hello world")),
                d = node("d").with(Shape.INV_TRIANGLE),
                e = node("e").with(Shape.polygon(4).distortion(.7));
        final Graph g = graph("ex3").directed().with(
                a.link(node("b").link(c, d)),
                e);
        Graphviz.fromGraph(g).render(PNG).toFile(new File("target/ex3.png"));
    }

    @Test
    void ex4a() throws IOException {
        final Node
                struct1 = node("struct1").with(Records.label("<f0> left|<f1> mid\\ dle|<f2> right")),
                struct2 = node("struct2").with(Records.label("<f0> one|<f1> two")),
                struct3 = node("struct3").with(Records.label("hello\nworld |{ b |{c|<here> d|e}| f}| g | h"));
        final Graph g = graph("ex4a").directed().with(
                struct1.link(
                        between(port("f1"), struct2.port("f0")),
                        between(port("f2"), struct3.port("here"))));
        Graphviz.fromGraph(g).height(500).rasterize(SALAMANDER).toFile(new File("target/ex4a-s.png"));
        Graphviz.fromGraph(g).height(500).rasterize(BATIK).toFile(new File("target/ex4a-b.png"));
    }

    @Test
    void ex4b() throws IOException {
        final Graph g = CreationContext.use(ctx -> {
            ctx
                    .graphAttrs().add(Color.YELLOWGREEN.background())
                    .nodeAttrs().add(Color.LIGHTBLUE3.fill(), Style.FILLED, Color.VIOLET.font())
                    .linkAttrs().add(Style.DOTTED);
            final Node
                    struct1 = node("struct1").with(Records.mOf(rec("f0", "left"), rec("f1", "mid dle"), rec("f2", "right"))),
                    struct2 = node("struct2").with(Records.mOf(rec("f0", "one"), rec("f1", "two"))),
                    struct3 = node("struct3").with(Records.mOf(
                            rec("hello\nworld"),
                            turn(rec("b"),
                                    turn(rec("c"), rec("here", "d"), rec("e")),
                                    rec("f")),
                            rec("g"), rec("h")));
            return graph("ex4b").directed().with(
                    struct1.link(
                            between(port("f1"), struct2.port("f0")),
                            between(port("f2"), struct3.port("here"))));
        });
        Graphviz.fromGraph(g).render(PNG).toFile(new File("target/ex4b.png"));
    }

    @Test
    void ex5() throws IOException {
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
                .graphAttr().with(Rank.sep(.75), GraphAttr.sizeMax(7.5, 7.5))
                .nodeAttr().with(Shape.RECTANGLE)
                .with(
                        graph().nodeAttr().with(Shape.NONE).with(
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
                                                                                                                        node("future")))))))))))),
                                bsh, make, sccs, reiser, csh, yacc, cron, rcs, emacs, build, vi, curses),
                        graph().graphAttr().with(Rank.inSubgraph(SAME)).nodeAttr().with(Shape.ELLIPSE).with(sis, cfg, archlib, proc),
                        graph().graphAttr().with(Rank.inSubgraph(SAME)).with(node("past"), sccs, make, bsh, yacc, cron),
                        graph().graphAttr().with(Rank.inSubgraph(SAME)).with(node("1978"), reiser, csh),
                        graph().graphAttr().with(Rank.inSubgraph(SAME)).with(node("1980"), build, emacs, vi),
                        graph().graphAttr().with(Rank.inSubgraph(SAME)).with(node("1982"), rcs, curses, imx, syned),
                        graph().graphAttr().with(Rank.inSubgraph(SAME)).with(node("1983"), ksh, ifs, ttu),
                        graph().graphAttr().with(Rank.inSubgraph(SAME)).with(node("1985"), nmake, peggy),
                        graph().graphAttr().with(Rank.inSubgraph(SAME)).with(node("1986"), cs, ncpp, kshi, cursesi, pg2),
                        graph().graphAttr().with(Rank.inSubgraph(SAME)).with(node("1987"), dag, csas, ansiCpp, fdelta, d3fs, nmake2),
                        graph().graphAttr().with(Rank.inSubgraph(SAME)).with(node("1988"), cia, sbcs, pax, ksh88, pegasus, backtalk),
                        graph().graphAttr().with(Rank.inSubgraph(SAME)).with(node("1989"), ciapp, app, ship, dataShare, ryacc, mosaic),
                        graph().graphAttr().with(Rank.inSubgraph(SAME)).with(node("1990"), dot, dia, libft, coshell, sfio, ifsi, mlx, kyacc, yeast),
                        graph().graphAttr().with(Rank.inSubgraph(SAME)).with(node("future"), adv),
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
        Graphviz.fromGraph(g).render(PNG).toFile(new File("target/ex5.png"));
    }

    @Test
    void ex6() throws IOException {
        final Node
                node0 = node("node0").with(Records.of(rec("f0", ""), rec("f1", ""), rec("f2", ""), rec("f3", ""), rec("f4", ""), rec("f5", ""), rec("f6", ""))),
                node1 = node("node1").with(Records.of(turn(rec("n", "n14"), rec("719"), rec("p", "")))),
                node2 = node("node2").with(Records.of(turn(rec("n", "a1"), rec("805"), rec("p", "")))),
                node3 = node("node3").with(Records.of(turn(rec("n", "i9"), rec("718"), rec("p", "")))),
                node4 = node("node4").with(Records.of(turn(rec("n", "e5"), rec("989"), rec("p", "")))),
                node5 = node("node5").with(Records.of(turn(rec("n", "t20"), rec("959"), rec("p", "")))),
                node6 = node("node6").with(Records.of(turn(rec("n", "o15"), rec("794"), rec("p", "")))),
                node7 = node("node7").with(Records.of(turn(rec("n", "s19"), rec("659"), rec("p", ""))));
        final Graph g = graph("ex6").directed()
                .graphAttr().with(Rank.dir(LEFT_TO_RIGHT))
                .with(
                        node0.link(
                                between(port("f0"), node1.port(WEST)),
                                between(port("f1"), node2.port(WEST)),
                                between(port("f2"), node3.port(WEST)),
                                between(port("f5"), node4.port(WEST)),
                                between(port("f6"), node5.port(WEST))),
                        node2.link(between(port("p"), node6.port(WEST))),
                        node4.link(between(port("p"), node7.port(WEST))));
        Graphviz.fromGraph(g).render(PNG).toFile(new File("target/ex6.png"));
    }

    @Test
    void ex7() throws IOException {
        final Graph g = graph("ex7").directed()
                .with(
                        graph().cluster()
                                .nodeAttr().with(Style.FILLED, Color.WHITE)
                                .graphAttr().with(Style.FILLED, Color.LIGHTGREY, Label.of("process #1"))
                                .with(node("a0").link(node("a1").link(node("a2").link(node("a3"))))),
                        graph("x").cluster()
                                .nodeAttr().with(Style.FILLED)
                                .graphAttr().with(Color.BLUE, Label.of("process #2"))
                                .with(node("b0").link(node("b1").link(node("b2").link(node("b3"))))),
                        node("start").with(Shape.M_DIAMOND).link("a0", "b0"),
                        node("a1").link("b3"),
                        node("b2").link("a3"),
                        node("a3").link("a0"),
                        node("a3").link("end"),
                        node("b3").link("end"),
                        node("end").with(Shape.M_SQUARE)
                );
        Graphviz.fromGraph(g).render(PNG).toFile(new File("target/ex7"));
    }

    @Test
    void ex8() throws IOException {
        final Node split = node("split types"),
                redString = node("reduce 'string' line"),
                redNum = node("reduce 'numbers' line"),
                succString = node("doOnSuccess print1"),
                succNum = node("doOnSuccess print2"),
                end = node("");
        final Attributes<ForAll> attr = attrs(Color.rgb("bbbbbb"), Color.rgb("bbbbbb").font(), Font.name("Arial"));
        final Graph g = graph("ex8").directed()
                .graphAttr().with(Color.rgb("444444").background())
                .nodeAttr().with(Style.combine(Style.DASHED, Style.ROUNDED), attr, Color.BLACK.font(), Color.rgb("bbbbbb").fill(), Shape.RECTANGLE)
                .linkAttr().with(Color.rgb("888888"), Style.lineWidth(2))
                .with(
                        node("input").link(split.link(redString, redNum)),
                        graph().cluster()
                                .graphAttr().with(attr, Label.of("stringPrint"))
                                .with(redString.link(succString)),
                        graph("x").cluster()
                                .graphAttr().with(attr, Label.of("numberPrint"))
                                .with(redNum.link(succNum)),
                        succString.link(end),
                        succNum.link(end)
                );
        final Graphviz viz = Graphviz.fromGraph(g).width(320);
        viz.rasterize(SALAMANDER).toFile(new File("target/ex8s.png"));
        viz.rasterize(BATIK).toFile(new File("target/ex8b.png"));
        viz.render(SVG).toFile(new File("target/ex8.svg"));
    }

    @Test
    void ex9() throws IOException {
        final Graph g = graph("ex9").directed()
                .with(node("first").link(to(node("second")).with(Arrow.DOT.open().size(2))));
        Graphviz.fromGraph(g).render(PNG).toFile(new File("target/ex9.png"));
    }

    @Test
    void ex10() throws IOException {
        final Node parent = node("parent").with(Shape.NONE, Label.html("<table border='1' cellborder='1'>" +
                "<tr><td colspan='3'>The foo, the bar and the baz</td></tr>" +
                "<tr><td port='one'>First port</td><td port='two'>Second port</td><td port='three'>Third port</td></tr>" +
                "</table>"));
        final Graph g = graph("ex10").directed()
                .with(parent.link(between(port("one"), node("first")), between(port("two"), node("second"))),
                        node("third").link(to(parent.port("three", Compass.SOUTH))));
        Graphviz.fromGraph(g).render(PNG).toFile(new File("target/ex10.png"));
    }

    @Test
    void ex11() throws IOException {
        Graphviz.fromGraph(mutGraph("example11").setDirected(true).use((g, ctx) -> {
            nodeAttrs().add(Color.RED);
            linkAttrs().add(Arrow.DIAMOND);
            graphAttrs().add(Label.of("Ex6"));
            mutNode("a").addLink("b");
            mutGraph("sub").setCluster(true).use((g2, ctx2) -> {
                mutNode("sa").addLink("sb");
            }).addLink(mutNode("a"));
        })).render(Format.PNG).toFile(new File("target/ex11"));
    }

    @Test
    void ex12() throws IOException {
        final Graph g = graph().nodeAttr().with(Shape.RECTANGLE).with(
                node("fixed size").with(Size.mode(Size.Mode.FIXED).size(.5, .5)),
                node("min").with(Size.mode(Size.Mode.MINIMUM).size(1, .5)),
                node("min long label").with(Size.mode(Size.Mode.MINIMUM).size(.5, .5)),
                node("min margin").with(Size.mode(Size.Mode.MINIMUM).size(.5, .5).margin(0, .5)),
                node("shape size").with(Size.mode(Size.Mode.SHAPE).size(.5, .5))
        );
        Graphviz.fromGraph(g).render(PNG).toFile(new File("target/ex12.png"));
    }

}
