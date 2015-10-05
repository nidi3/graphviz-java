# graphviz-java [![Build Status](https://travis-ci.org/nidi3/graphviz-java.svg)](https://travis-ci.org/nidi3/graphviz-java)
Use graphviz with pure java.

Uses the [javascript version](https://github.com/mdaines/viz.js) of graphviz created using 
[Emscripten](https://github.com/kripken/emscripten).

The javascript code is executed either with [J2V8](https://github.com/eclipsesource/J2V8) or, 
as a fallback with Java 8's Nashorn engine.

The basic usage is as follows:
    
    import static guru.nidi.graphviz.Factory.*;

    Graph g = graph("example").directed().node(node("a").link(node("b")));
    Graphviz.fromGraph(g).renderToFile(new File("example.png"), "png", 300, 300);
    
![bla](https://raw.githubusercontent.com/nidi3/graphviz-java/master/example/ex1.png)