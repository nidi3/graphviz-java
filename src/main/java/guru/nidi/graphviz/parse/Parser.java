package guru.nidi.graphviz.parse;

import guru.nidi.graphviz.attribute.Attributed;
import guru.nidi.graphviz.model.Factory;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Label;
import guru.nidi.graphviz.model.Node;
import guru.nidi.graphviz.parse.Lexer.Token;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static guru.nidi.graphviz.parse.Lexer.Token.*;

/**
 *
 */
public class Parser {
    private final Lexer lexer;
    private Token token;

    public Parser(Lexer lexer) throws IOException {
        this.lexer = lexer;
        token();
    }

    public Graph parse() throws IOException {
        Graph graph = Graph.nameless();
        if (token.type == STRICT) {
            graph = graph.strict();
            token();
        }
        if (token.type == DIGRAPH) {
            graph = graph.directed();
        } else if (token.type != GRAPH) {
            throw new ParserException("'graph' or 'digraph' expected");
        }
        token();
        if (token.type == ID) {
            graph = graph.labeled(label(token));
            token();
        }
        token(BRACE_OPEN, "{");
        statementList(graph);
        token(BRACE_CLOSE, "}");
        token(EOF, "end of file");
        return graph;
    }

    private Label label(Token token) {
        return token.subtype == SUB_HTML ? Label.html(token.value) : Label.of(token.value);
    }

    private void statementList(Graph graph) throws IOException {
        final Token t = token;
        switch (t.type) {
            case ID:
                token();
                if (token.type == EQUAL) {
                    //TODO
                } else {
                    final List<Token> nodeId = nodeId(t);
                    if (token.type == MINUS_MINUS || token.type == ARROW) {
                        edgeStatement(graph,nodeId);
                    } else {
                        nodeStatement(nodeId);
                    }
                }
                break;
            case SUBGRAPH:
                break;
            case BRACE_OPEN:
                break;
            case GRAPH:
            case NODE:
            case EDGE:
                attributeStatement(graph);
                break;
        }
    }

    private void edgeStatement(Graph graph,List<Token> nodeId) throws IOException {

        if (graph.directed && token.type == MINUS_MINUS) {
            throw new ParserException("-- used in digraph. Use -> instead.");
        }
        if (!graph.directed && token.type == ARROW) {
            throw new ParserException("-> used in graph. Use -- instead.");
        }
        token();

        if (token.type==ID){
            final List<Token> target = nodeId(token());

        }
    }
    private void nodeStatement(List<Token> nodeId) throws IOException {
        if (token.type == BRACKET_OPEN) {
            final Node node = Factory.node(label(nodeId.get(0))); //TODO ignore port and compass?
            applyAttributes(node, attributeList());
        }
    }

    private List<Token> nodeId(Token base) throws IOException {
        final List<Token> res = new ArrayList<>();
        res.add(base);
        if (token.type == COLON) {
            token();
            res.add(token(ID, "identifier"));
            if (token.type == COLON) {
                token();
                res.add(token(ID, "identifier"));
            }
        }
        return res;
    }

    private void attributeStatement(Graph graph) throws IOException {
        applyAttributes(attributes(graph, token), attributeList());
    }

    private void applyAttributes(Attributed<?> attributed, List<Token> tokens) throws IOException {
        for (int i = 0; i < tokens.size(); i += 2) {
            attributed.attr(tokens.get(i).value, tokens.get(i + 1).value);
        }
    }

    private Attributed<Graph> attributes(Graph graph, Token token) {
        switch (token.type) {
            case GRAPH:
                return graph.graph();
            case NODE:
                return graph.node();
            case EDGE:
                return graph.link();
            default:
                return null;
        }
    }

    private List<Token> attributeList() throws IOException {
        final List<Token> res = new ArrayList<>();
        do {
            token(BRACKET_OPEN, "[");
            if (token.type == ID) {
                res.addAll(aList());
            }
            token(BRACKET_CLOSE, "]");
        } while (token.type == BRACKET_OPEN);
        return res;
    }

    private List<Token> aList() throws IOException {
        final List<Token> res = new ArrayList<>();
        do {
            res.add(token);
            token(EQUAL, "=");
            res.add(token(ID, "identifier"));
            if (token.type == SEMICOLON || token.type == COMMA) {
                token();
            }
        } while (token.type == ID);
        return res;
    }

    private Token token() throws IOException {
        return token = lexer.token();
    }

    private Token token(int type, String value) throws IOException {
        if (token.type != type) {
            throw new ParserException("'" + value + "' expected");
        }
        return token();
    }
}
