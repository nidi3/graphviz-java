# graphviz-java 
[![Build Status](https://travis-ci.org/nidi3/graphviz-java.svg)](https://travis-ci.org/nidi3/graphviz-java)
[![codecov](https://codecov.io/gh/nidi3/graphviz-java/branch/master/graph/badge.svg)](https://codecov.io/gh/nidi3/graphviz-java)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

Use graphviz with pure java.

Uses this [javascript version](https://github.com/mdaines/viz.js) of graphviz created using 
[Emscripten](https://github.com/kripken/emscripten).

The javascript code is executed either with [J2V8](https://github.com/eclipsesource/J2V8) or, 
as a fallback with Java 8's Nashorn engine.

## Usage

### Maven

This project is available via Maven:

```xml
<dependency>
    <groupId>guru.nidi</groupId>
    <artifactId>graphviz-java</artifactId>
    <version>0.5.3</version>
</dependency>
```

### Logging
Graphviz-java uses the [SLF4J](https://www.slf4j.org/) facade to log. 
Users must therefore provide a logging implementation like [LOGBack](https://logback.qos.ch/)
```xml
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.2.3</version>
</dependency>
```
or [Log4j](https://logging.apache.org/log4j/2.x/)
```xml
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-core</artifactId>
    <version>2.8.2</version>
</dependency>
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-slf4j-impl</artifactId>
    <version>2.8.2</version>
</dependency>
```

### Simple example
The basic usage is as follows (assuming `import static guru.nidi.graphviz.model.Factory.*`):

[//]: # (basic)
```java
Graph g = graph("example1").directed().with(node("a").link(node("b")));
Graphviz.fromGraph(g).width(200).render(Format.PNG).toFile(new File("example/ex1.png"));
```
[//]: # (end)
<img src="https://rawgit.com/nidi3/graphviz-java/master/graphviz-java/example/ex1.png" width="100">

### Configuration
The size of the resulting image, the rendering engine and the output format can be configured:

[//]: # (config)
```java
Graph g = graph("example5").directed().with(node("abc").link(node("xyz")));
Graphviz viz = Graphviz.fromGraph(g);
viz.width(200).render(Format.SVG).toFile(new File("example/ex5.svg"));
viz.width(200).rasterize(Rasterizer.BATIK).toFile(new File("example/ex5b.png"));
viz.width(200).rasterize(Rasterizer.SALAMANDER).toFile(new File("example/ex5s.png"));
String json = viz.engine(Engine.NEATO).render(Format.JSON).toString();
BufferedImage image = viz.render(Format.PNG).toImage();
```
[//]: # (end)
<img src="https://rawgit.com/nidi3/graphviz-java/master/graphviz-java/example/ex5b.png" width="100">
<img src="https://rawgit.com/nidi3/graphviz-java/master/graphviz-java/example/ex5s.png" width="100">
<img src="https://rawgit.com/nidi3/graphviz-java/master/graphviz-java/example/ex5.svg" width="100">

### Complex example

[//]: # (complex)
```java
Node
        main = node("main").with(Label.html("<b>main</b>"), Color.rgb("1020d0").font()),
        init = node("init"),
        execute = node("execute"),
        compare = node("compare").with(Shape.RECTANGLE, Style.FILLED, Color.hsv(.7, .3, 1.0)),
        mkString = node("mkString").with(Label.of("make a\nstring")),
        printf = node("printf");

Graph g = graph("example2").directed().with(
        main.link(
                to(node("parse").link(execute)).with("weight", 8),
                to(init).with(Style.DOTTED),
                node("cleanup"),
                to(printf).with(Style.BOLD, Label.of("100 times"), Color.RED)),
        execute.link(
                graph().with(mkString, printf),
                to(compare).with(Color.RED)),
        init.link(mkString));

Graphviz.fromGraph(g).width(900).render(Format.PNG).toFile(new File("example/ex2.png"));
```
[//]: # (end)
<img src="https://rawgit.com/nidi3/graphviz-java/master/graphviz-java/example/ex2.png" width="500">

### Example with records
```java
import static guru.nidi.graphviz.attribute.Records.*;
import static guru.nidi.graphviz.model.Compass.*;
```
[//]: # (records)
```java
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
        .graphAttr().with(RankDir.LEFT_TO_RIGHT)
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
```
[//]: # (end)
<img src="https://rawgit.com/nidi3/graphviz-java/master/graphviz-java/example/ex3.png" width="500">

### Read and manipulate graphs

Dot files can be parsed and thus manipulated:

```
graph {
    { rank=same; white}
    { rank=same; cyan; yellow; pink}
    { rank=same; red; green; blue}
    { rank=same; black}

    white -- cyan -- blue
    white -- yellow -- green
    white -- pink -- red

    cyan -- green -- black
    yellow -- red -- black
    pink -- blue -- black
}
```
<img src="https://rawgit.com/nidi3/graphviz-java/master/graphviz-java/example/ex4-1.png" width="400">

[//]: # (manipulate)
```java
MutableGraph g = Parser.read(getClass().getResourceAsStream("/color.dot"));
Graphviz.fromGraph(g).width(700).render(Format.PNG).toFile(new File("example/ex4-1.png"));

g.graphAttrs()
        .add(Color.WHITE.gradient(Color.rgb("888888")).background().angle(90))
        .nodeAttrs().add(Color.WHITE.fill())
        .nodes().forEach(node ->
        node.add(
                Color.named(node.name().toString()),
                Style.lineWidth(4).and(Style.FILLED)));
Graphviz.fromGraph(g).width(700).render(Format.PNG).toFile(new File("example/ex4-2.png"));
```
[//]: # (end)
<img src="https://rawgit.com/nidi3/graphviz-java/master/graphviz-java/example/ex4-2.png" width="400">
