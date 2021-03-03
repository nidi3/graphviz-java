/*
 * Copyright © 2015 Stefan Niederhauser (nidin@gmx.ch)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package guru.nidi.graphviz.parse;


import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import static guru.nidi.graphviz.parse.Token.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LexerTest {
    @Test
    void symbols() throws IOException {
        assertTokens(";: [  ] \n {}, =  -- ->",
                token(SEMICOLON, ";"), token(COLON, ":"), token(BRACKET_OPEN, "["), token(BRACKET_CLOSE, "]"),
                token(BRACE_OPEN, "{"), token(BRACE_CLOSE, "}"), token(COMMA, ","), token(EQUAL, "="),
                token(MINUS_MINUS, "--"), token(ARROW, "->"));
    }

    @Test
    void lineComment() throws IOException {
        assertTokens("= //comment{} \n=", token(EQUAL, "="), token(EQUAL, "="));
    }

    @Test
    void multilineComment() throws IOException {
        assertTokens("= /*comment{} * / \n [] */=", token(EQUAL, "="), token(EQUAL, "="));
    }

    @Test
    void hashComment() throws IOException {
        assertTokens("=\n   #{} \n=", token(EQUAL, "="), token(EQUAL, "="));
    }

    @Test
    void keyword() throws IOException {
        assertTokens("strict, GRAPH ; DiGraph \n Node edge \t subgraph",
                token(STRICT, "strict"), token(COMMA, ","), token(GRAPH, "GRAPH"), token(SEMICOLON, ";"),
                token(DIGRAPH, "DiGraph"), token(NODE, "Node"), token(EDGE, "edge"), token(SUBGRAPH, "subgraph"));
    }

    @Test
    void numeral() throws IOException {
        assertTokens(" -9  .1 \n 2 \t 34.56",
                token(ID, SUB_NUMERAL, "-9"), token(ID, SUB_NUMERAL, ".1"),
                token(ID, SUB_NUMERAL, "2"), token(ID, SUB_NUMERAL, "34.56"));
    }

    @Test
    void simpleId() throws IOException {
        assertTokens("simple  a_b \n x99 A\u0080",
                token(ID, SUB_SIMPLE, "simple"), token(ID, SUB_SIMPLE, "a_b"),
                token(ID, SUB_SIMPLE, "x99"), token(ID, SUB_SIMPLE, "A\u0080"));
    }

    @Test
    void quotedId() throws IOException {
        assertTokens("= \"simple\" , \"esc\\\"esc\" \"newline(\\\n)newline\" ",
                token(EQUAL, "="), token(ID, SUB_QUOTED, "simple"), token(COMMA, ","),
                token(ID, SUB_QUOTED, "esc\"esc"), token(ID, SUB_QUOTED, "newline()newline"));
    }

    @Test
    void emptyHtmlId() throws IOException {
        assertTokens("<>", token(ID, SUB_HTML, ""));
    }

    @Test
    void htmlId() throws IOException {
        assertTokens(" <text <tag> end>", token(ID, SUB_HTML, "text <tag> end"));
    }

    @Test
    void htmlIdWithoutspaces() throws IOException {
        assertTokens("<<text>>", token(ID, SUB_HTML, "<text>"));
    }

    private void assertTokens(String s, Token... expected) throws IOException {
        assertEquals(Arrays.asList(expected), lexAll(s));
    }

    private Token token(int type, int subtype, String value) {
        return new Token(type, subtype, value);
    }

    private Token token(int type, String value) {
        return new Token(type, value);
    }

    private List<Token> lexAll(String s) throws IOException {
        final Lexer lexer = new Lexer(new StringReader(s), "");
        final List<Token> tokens = new ArrayList<>();
        Token token;
        while ((token = lexer.token()).type != EOF) {
            tokens.add(token);
        }
        return tokens;
    }
}
