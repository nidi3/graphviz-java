package guru.nidi.graphviz;

import org.junit.Test;

import static guru.nidi.graphviz.Factory.*;
import static org.junit.Assert.assertEquals;

/**
 *
 */
public class SerializerTest {
    @Test
    public void simple() {
        assertGraph("graph 'x' {\n}", graph("x"));
    }

    @Test
    public void directed() {
        assertGraph("digraph 'x' {\n}", graph("x").directed());
    }

    @Test
    public void strict() {
        assertGraph("strict graph 'x' {\n}", graph("x").strict());
    }

    @Test
    public void escapeName() {
        assertGraph("graph 'b\\'la' {\n}", graph("b\"la"));
    }

    @Test
    public void htmlName() {
        assertGraph("graph <bla> {\n}", graph(html("bla")));
    }

    @Test
    public void graphAttr() {
        assertGraph("graph 'x' {\ngraph ['bla'='blu']\n}", graph("x").attr("bla", "blu"));
    }

    @Test
    public void nodes() {
        assertGraph("graph 'x' {\n'x' ['bla'='blu']\n}", graph("x")
                .node(node("x").attr("bla", "blu")));
    }

    @Test
    public void simpleEdge() {
        assertGraph("graph 'x' {\n'x' -- 'y'\n}", graph("x")
                .node(node("x").link(to(node("y")))));
    }

    @Test
    public void attrEdge() {
        assertGraph("graph 'x' {\n'x' -- 'y' ['bla'='blu']\n}", graph("x")
                .node(node("x").link(to(node("y")).attr("bla", "blu"))));
    }

    @Test
    public void complexEdge() {
        assertGraph("digraph 'x' {\n'x' -> 'y'\n'y' -> 'z'\n'a' -> 'x'\n}", graph("x").directed()
                .node(
                        node("x").link(to(node("y").link(to(node("z"))))))
                .node(
                        node("a").link(to(node("x")))));
    }

    private void assertGraph(String expected, Graph graph) {
        assertEquals(expected.replace("'", "\""), new Serializer(graph).serialize());
    }
}
