package guru.nidi.graphviz.parse;

import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Label;
import guru.nidi.graphviz.model.Node;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static guru.nidi.graphviz.model.Compass.NORTH_EAST;
import static guru.nidi.graphviz.model.Compass.SOUTH_WEST;
import static guru.nidi.graphviz.model.Factory.*;
import static org.junit.Assert.assertEquals;

/**
 *
 */
public class ParserTest {
    @Test
    public void emptyGraph() throws IOException {
        assertEquals(Graph.named("bla"),
                parse("graph bla{}"));
    }

    @Test
    public void emptyStrictDigraph() throws IOException {
        assertEquals(Graph.named(Label.html("bla")).strict().directed(),
                parse("strict digraph <bla>{}"));
    }

    @Test
    public void attributesGraph() throws IOException {
        assertEquals(graph()
                        .general().attr("x", "y")
                        .graph().attr("a", "b")
                        .node().attr("c", "d")
                        .link().attr("e", "f", "g", "h", "i", "j"),
                parse("graph { x=y; graph [a=b]; node[c=d] edge[e=f,g=h][i=j] }"));
    }

    @Test
    public void nodes() throws IOException {
        assertEquals(graph().node("simple").node(node("attr").attr("a", "b")),
                parse("graph { simple attr[\"a\"=b]}")); //TODO with port? "d:1 full:1:ne
    }

    @Test
    public void links() throws IOException {
        final Node
                simple = node("simple"),
                d = node("d"),
                full = node("full");
        assertEquals(graph().node(
                simple.link(to(d.loc(SOUTH_WEST)).attr("a", "b")),
                d.link(between(loc(SOUTH_WEST), full.loc("2", NORTH_EAST)).attr("a", "b"))),
                parse("graph { simple -- d:sw -- full:2:ne [a=b]}"));
    }

    @Test
    public void subgraph() throws IOException {
        assertEquals(graph().graph(graph("s"), graph(), graph()),
                parse("graph { subgraph s {}; subgraph {}; {} }"));
    }

//    @Test
//    public void subgraphEdge() throws IOException {
//        assertEquals(graph().graph(graph().link(node("x"))),
//                parse("graph{ {} -- x }"));
//    }

    private Graph parse(String s) throws IOException {
        final Parser parser = new Parser(new Lexer(new StringReader(s)));
        return parser.parse();
    }
}
