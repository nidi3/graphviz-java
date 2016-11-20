package guru.nidi.graphviz;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.cesta.parsers.dot.DotLexer;
import org.cesta.parsers.dot.DotParser;
import org.cesta.parsers.dot.DotTree;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;

public class DotParserTest {
    @Test
    public void simple() throws IOException, RecognitionException {
        final DotLexer lexer = new DotLexer(new ANTLRReaderStream(new InputStreamReader(getClass().getResourceAsStream("/test1.dot"), "utf-8")));
        final DotParser parser = new DotParser(new CommonTokenStream(lexer));
        final DotParser.graph_return graph = parser.graph();
        System.out.println(graph);
        final DotTree tree = new DotTree(new CommonTreeNodeStream(graph.getTree()));
        final DotTree.graph_return graph1 = tree.graph();
        System.out.println(graph1);

    }
}
