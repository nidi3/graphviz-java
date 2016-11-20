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
package guru.nidi.graphviz.parse;

import guru.nidi.graphviz.attribute.Attributed;
import guru.nidi.graphviz.model.*;
import guru.nidi.graphviz.parse.Lexer.Token;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static guru.nidi.graphviz.model.Factory.between;
import static guru.nidi.graphviz.parse.Lexer.Token.*;

/**
 *
 */
public class Parser {
    private final Lexer lexer;
    private Token token;

    public Parser(Lexer lexer) throws IOException {
        this.lexer = lexer;
        nextToken();
    }

    public Graph parse() throws IOException {
        Graph graph = Graph.nameless();
        if (token.type == STRICT) {
            graph = graph.strict();
            nextToken();
        }
        if (token.type == DIGRAPH) {
            graph = graph.directed();
        } else if (token.type != GRAPH) {
            throw new ParserException("'graph' or 'digraph' expected");
        }
        nextToken();
        if (token.type == ID) {
            graph = graph.labeled(label(token));
            nextToken();
        }
        statementList(graph);
        assertToken(EOF, "end of file");
        return graph;
    }

    private Label label(Token token) {
        return token.subtype == SUB_HTML ? Label.html(token.value) : Label.of(token.value);
    }

    private void statementList(Graph graph) throws IOException {
        assertToken(BRACE_OPEN, "{");
        while (statement(graph)) {
            if (token.type == SEMICOLON) {
                nextToken();
            }
        }
        assertToken(BRACE_CLOSE, "}");
    }

    private boolean statement(Graph graph) throws IOException {
        final Token base = token;
        switch (base.type) {
            case ID:
                nextToken();
                if (token.type == EQUAL) {
                    applyMutableAttributes(graph.general(), Arrays.asList(base, nextToken(ID, "identifier")));
                    nextToken();
                } else {
                    final List<Token> nodeId = nodeId(base);
                    if (token.type == MINUS_MINUS || token.type == ARROW) {
                        edgeStatement(graph, nodeId);
                    } else {
                        nodeStatement(graph, nodeId);
                    }
                }
                return true;
            case SUBGRAPH:
            case BRACE_OPEN:
                Graph sub = Graph.nameless();
                if (token.type == SUBGRAPH) {
                    nextToken();
                    if (token.type == ID) {
                        sub = sub.labeled(label(token));
                        nextToken();
                    }
                }
                statementList(sub);
                graph.graph(sub);
                return true;
            case GRAPH:
            case NODE:
            case EDGE:
                attributeStatement(graph);
                return true;
            default:
                return false;
        }
    }

    private void edgeStatement(Graph graph, List<Token> nodeId) throws IOException {
        final List<List<Token>> nodes = new ArrayList<>();
        nodes.add(nodeId);
        do {
            if (graph.directed && token.type == MINUS_MINUS) {
                throw new ParserException("-- used in digraph. Use -> instead.");
            }
            if (!graph.directed && token.type == ARROW) {
                throw new ParserException("-> used in graph. Use -- instead.");
            }
            nextToken();
            if (token.type == ID) {
                final Token id = token;
                nextToken();
                nodes.add(nodeId(id));
            }
        } while (token.type == MINUS_MINUS || token.type == ARROW);
        final List<Token> attrs = (token.type == BRACKET_OPEN) ? attributeList() : Collections.emptyList();
        for (int i = 0; i < nodes.size() - 1; i++) {
            final NodePoint from = point(nodes.get(i));
            final NodePoint to = point(nodes.get(i + 1));
            graph.node(from.node.link(applyAttributes(between(from, to), attrs)));
        }
    }

    private NodePoint point(List<Token> tokens) {
        NodePoint node = NodePoint.of(Node.named(label(tokens.get(0))));
        if (tokens.size() == 2) {
            node = node.loc(compass(tokens.get(1).value));
        }
        if (tokens.size() == 3) {
            node = node.loc(tokens.get(1).value, compass(tokens.get(2).value));
        }
        return node;
    }

    private Compass compass(String name) {
        final Compass c = Compass.of(name);
        if (c == null) {
            throw new ParserException("Invalid compass value '" + name + "'");
        }
        return c;
    }

    private void nodeStatement(Graph graph, List<Token> nodeId) throws IOException {
        Node node = Factory.node(label(nodeId.get(0))); //TODO ignore port and compass?
        if (token.type == BRACKET_OPEN) {
            node = applyAttributes(node, attributeList());
        }
        graph.node(node);
    }

    private List<Token> nodeId(Token base) throws IOException {
        final List<Token> res = new ArrayList<>();
        res.add(base);
        if (token.type == COLON) {
            res.add(nextToken(ID, "identifier"));
            nextToken();
            if (token.type == COLON) {
                res.add(nextToken(ID, "identifier"));
                nextToken();
            }
        }
        return res;
    }

    private void attributeStatement(Graph graph) throws IOException {
        final Attributed<Graph> target = attributes(graph, token);
        nextToken();
        applyMutableAttributes(target, attributeList());
    }

    private void applyMutableAttributes(Attributed<?> attributed, List<Token> tokens) throws IOException {
        for (int i = 0; i < tokens.size(); i += 2) {
            attributed.attr(tokens.get(i).value, tokens.get(i + 1).value);
        }
    }

    private <T extends Attributed<T>> T applyAttributes(T attributed, List<Token> tokens) throws IOException {
        for (int i = 0; i < tokens.size(); i += 2) {
            attributed = attributed.attr(tokens.get(i).value, tokens.get(i + 1).value);
        }
        return attributed;
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
            assertToken(BRACKET_OPEN, "[");
            if (token.type == ID) {
                res.addAll(aList());
            }
            assertToken(BRACKET_CLOSE, "]");
        } while (token.type == BRACKET_OPEN);
        return res;
    }

    private List<Token> aList() throws IOException {
        final List<Token> res = new ArrayList<>();
        do {
            res.add(token);
            nextToken(EQUAL, "=");
            res.add(nextToken(ID, "identifier"));
            nextToken();
            if (token.type == SEMICOLON || token.type == COMMA) {
                nextToken();
            }
        } while (token.type == ID);
        return res;
    }

    private Token nextToken() throws IOException {
        return token = lexer.token();
    }

    private Token nextToken(int type, String value) throws IOException {
        nextToken();
        checkToken(type, value);
        return token;
    }

    private Token assertToken(int type, String value) throws IOException {
        checkToken(type, value);
        return nextToken();
    }

    private void checkToken(int type, String value) {
        if (token.type != type) {
            throw new ParserException("'" + value + "' expected");
        }
    }
}
