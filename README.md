# graphviz-java [![Build Status](https://travis-ci.org/nidi3/graphviz-java.svg)](https://travis-ci.org/nidi3/graphviz-java)
Use graphviz with pure java.

Uses this [javascript version](https://github.com/mdaines/viz.js) of graphviz created using 
[Emscripten](https://github.com/kripken/emscripten).

The javascript code is executed either with [J2V8](https://github.com/eclipsesource/J2V8) or, 
as a fallback with Java 8's Nashorn engine.

The basic usage is as follows:
  
```
import static guru.nidi.graphviz.Factory.*;

Graph g = graph("example").directed().node(node("a").link(node("b")));
Graphviz.fromGraph(g).renderToFile(new File("example.png"), "png", 300, 300);
```
    
![](https://raw.githubusercontent.com/nidi3/graphviz-java/master/example/ex1.png)

```
Node
    init = node("init"),
    execute = node("execute"),
    compare = node("compare").attr(Shape.RECTANGLE, Style.FILLED, Color.hsv(.7, .3, 1.0)),
    make_string = node("make_string").attr(Label.of("make a\nstring")),
    printf = node("printf");

Graph g = graph("example2").directed().node(
    node("main").attr(Shape.RECTANGLE).link(
        to(node("parse").link(execute)).attr("weight", 8),
        to(init).attr(Style.DOTTED),
        node("cleanup"),
        to(printf).attr(Style.BOLD, Label.of("100 times"), Color.RED)),
    execute.link(
        graph().node(make_string, printf),
        to(compare).attr(Color.RED)),
    init.link(make_string));

Graphviz.fromGraph(g).renderToFile(new File("example/ex2.png"), "png", 300, 300);
```
    
![](https://raw.githubusercontent.com/nidi3/graphviz-java/master/example/ex2.png)
