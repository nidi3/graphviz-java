package guru.nidi.graphviz.parse;

import guru.nidi.graphviz.parse.Lexer.Token;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static guru.nidi.graphviz.parse.Lexer.Token.*;
import static org.junit.Assert.assertEquals;

/**
 *
 */
public class LexerTest {
    @Test
    public void symbols() throws IOException {
        assertTokens(";: [  ] \n {}, =  -- ->",
                token(SEMICOLON, ";"), token(COLON, ":"), token(BRACKET_OPEN, "["), token(BRACKET_CLOSE, "]"),
                token(BRACE_OPEN, "{"), token(BRACE_CLOSE, "}"), token(COMMA, ","), token(EQUAL, "="),
                token(MINUS_MINUS, "--"), token(ARROW, "->"));
    }

    @Test
    public void lineComment() throws IOException {
        assertTokens("= //comment{} \n=", token(EQUAL, "="), token(EQUAL, "="));
    }

    @Test
    public void multilineComment() throws IOException {
        assertTokens("= /*comment{} * / \n [] */=", token(EQUAL, "="), token(EQUAL, "="));
    }

    @Test
    public void hashComment() throws IOException {
        assertTokens("=\n#{} \n=", token(EQUAL, "="), token(EQUAL, "="));
    }

    @Test
    public void keyword() throws IOException {
        assertTokens("strict, GRAPH ; DiGraph \n Node edge \t subgraph",
                token(STRICT, "strict"), token(COMMA, ","), token(GRAPH, "GRAPH"), token(SEMICOLON, ";"),
                token(DIGRAPH, "DiGraph"), token(NODE, "Node"), token(EDGE, "edge"), token(SUBGRAPH, "subgraph"));
    }

    @Test
    public void numeral() throws IOException {
        assertTokens(" -9  .1 \n 2 \t 34.56",
                token(ID, SUB_NUMERAL, "-9"), token(ID, SUB_NUMERAL, ".1"),
                token(ID, SUB_NUMERAL, "2"), token(ID, SUB_NUMERAL, "34.56"));
    }

    @Test
    public void simpleId() throws IOException {
        assertTokens("simple  a_b \n x99 A\u0080",
                token(ID, SUB_SIMPLE, "simple"), token(ID, SUB_SIMPLE, "a_b"),
                token(ID, SUB_SIMPLE, "x99"), token(ID, SUB_SIMPLE, "A\u0080"));
    }

    @Test
    public void quotedId() throws IOException {
        assertTokens("= \"simple\" , \"esc\\\"esc\" \"newline(\\\n)newline\" ",
                token(EQUAL, "="), token(ID, SUB_QUOTED, "simple"), token(COMMA, ","),
                token(ID, SUB_QUOTED, "esc\"esc"), token(ID, SUB_QUOTED, "newline()newline"));
    }

    @Test
    public void htmlId() throws IOException {
        assertTokens(" <text <tag> end>", token(ID, SUB_HTML, "text <tag> end"));
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
        final Lexer lexer = new Lexer(new StringReader(s));
        final List<Token> tokens = new ArrayList<>();
        Token token;
        while ((token = lexer.token()).type != EOF) {
            tokens.add(token);
        }
        return tokens;
    }
}
