# graphviz-java [![Build Status](https://travis-ci.org/nidi3/graphviz-java.svg)](https://travis-ci.org/nidi3/graphviz-java)
Use graphviz with pure java.

Uses this [javascript version](https://github.com/mdaines/viz.js) of graphviz created using 
[Emscripten](https://github.com/kripken/emscripten).

The javascript code is executed either with [J2V8](https://github.com/eclipsesource/J2V8) or, 
as a fallback with Java 8's Nashorn engine.

### Usage
The basic usage is as follows:
  
```java
import static guru.nidi.graphviz.model.Factory.*;

Graph g = graph("example").directed().node(node("a").link(node("b")));
Graphviz.fromGraph(g).renderToFile(new File("example.png"));
```
![](https://raw.githubusercontent.com/nidi3/graphviz-java/master/example/ex1.png)

### Complex example

```java
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

Graphviz.fromGraph(g).renderToFile(new File("example/ex2.png"));
```
![](https://raw.githubusercontent.com/nidi3/graphviz-java/master/example/ex2.png)

### Example with records

```java
    Node
        node0 = node("node0").attr(Records.of(rec("f0", ""), rec("f1", ""), rec("f2", ""), rec("f3", ""), rec("f4", ""))),
        node1 = node("node1").attr(Records.of(turn(rec("n4"), rec("v", "719"), rec("")))),
        node2 = node("node2").attr(Records.of(turn(rec("a1"), rec("805"), rec("p","")))),
        node3 = node("node3").attr(Records.of(turn(rec("i9"), rec("718"), rec("")))),
        node4 = node("node4").attr(Records.of(turn(rec("e5"), rec("989"), rec("p","")))),
        node5 = node("node5").attr(Records.of(turn(rec("t2"), rec("v", "959"), rec("")))),
        node6 = node("node6").attr(Records.of(turn(rec("o1"), rec("794"), rec("")))),
        node7 = node("node7").attr(Records.of(turn(rec("s7"), rec("659"), rec(""))));
    Graph g = graph("example3").directed()
        .general().attr(RankDir.LEFT_TO_RIGHT)
        .node(
            node0.link(
                between(loc("f0"), node1.loc("v", SOUTH)),
                between(loc("f1"), node2.loc(WEST)),
                between(loc("f2"), node3.loc(WEST)),
                between(loc("f3"), node4.loc(WEST)),
                between(loc("f4"), node5.loc("v", NORTH))),
            node2.link(between(loc("p"), node6.loc(NORTH_WEST))),
            node4.link(between(loc("p"), node7.loc(SOUTH_WEST))));
    Graphviz.fromGraph(g).renderToFile(new File("example/ex3.png"));
```
![](https://raw.githubusercontent.com/nidi3/graphviz-java/master/example/ex3.png)
