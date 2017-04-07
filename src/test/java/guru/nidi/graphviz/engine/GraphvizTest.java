package guru.nidi.graphviz.engine;

import guru.nidi.graphviz.model.Graph;
import org.junit.Test;

import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;


public class GraphvizTest {

    @Test
    public void scaleMethodChainCheck(){
        final Graph graph = graph().with(node("a").link("b"));

        final Graphviz graphviz = Graphviz.fromGraph(graph).height(20).width(30).scale(3);

        assertThat(graphviz.width, is(30) );
        assertThat(graphviz.height, is(20) );
        assertThat(graphviz.scale, is(3d ));
    }

    @Test
    public void heightMethodChainCheck(){
        final Graph graph = graph().with(node("a").link("b"));

        final Graphviz graphviz = Graphviz.fromGraph(graph).scale(3).width(30).height(20);

        assertThat(graphviz.width, is(30) );
        assertThat(graphviz.height, is(20) );
        assertThat(graphviz.scale, is(3d ));
    }

    @Test
    public void widthMethodChainCheck(){
        final Graph graph = graph().with(node("a").link("b"));

        final Graphviz graphviz = Graphviz.fromGraph(graph).scale(3).height(20).width(30);

        assertThat(graphviz.width, is(30) );
        assertThat(graphviz.height, is(20) );
        assertThat(graphviz.scale, is(3d ));
    }
}