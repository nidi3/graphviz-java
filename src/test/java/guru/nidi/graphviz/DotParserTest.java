/*
 * Copyright (C) 2015 Stefan Niederhauser (nidin@gmx.ch)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
