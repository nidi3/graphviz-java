package guru.nidi.graphviz.engine;

import guru.nidi.graphviz.model.Graph;
import org.junit.Test;

import java.io.File;

import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;
import static org.junit.Assert.*;


public class RendererTest {

    @Test
    public void toFile_parentFolderNotExists() throws Exception {
        Graph graph = graph("example1").directed().with(node("a").link(node("b")));

        File expectedFile = new File("target/testFolder/ex1.png");
        Graphviz.fromGraph(graph).width(200).render(Format.PNG).toFile(expectedFile);

        assertTrue(expectedFile.exists());

        expectedFile.delete();
        expectedFile.getParentFile().delete();
    }

}