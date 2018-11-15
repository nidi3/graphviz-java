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

import guru.nidi.graphviz.attribute.Attributed;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static guru.nidi.graphviz.model.Factory.mutGraph;
import static guru.nidi.graphviz.model.Factory.mutNode;
import static guru.nidi.graphviz.parse.Token.*;

public final class Parser {
    private final Lexer lexer;
    private Token token;

    public static MutableGraph read(File file) throws IOException {
        return read(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8), file.getName());
    }

    public static MutableGraph read(InputStream is) throws IOException {
        return read(new InputStreamReader(is, StandardCharsets.UTF_8), "<input stream>");
    }

    public static MutableGraph read(String dot) throws IOException {
        return read(new StringReader(dot), "<string>");
    }

    public static MutableGraph read(Reader dot, String name) throws IOException {
        return new Parser(new Lexer(dot, name)).parse();
    }

    private Parser(Lexer lexer) throws IOException {
        this.lexer = lexer;
        token = nextToken();
    }

    private MutableGraph parse() {
        return CreationContext.use(ctx -> {
            final MutableGraph graph = mutGraph();
            if (token.type == STRICT) {
                graph.setStrict(true);
                nextToken();
            }
            if (token.type == DIGRAPH) {
                graph.setDirected(true);
            } else if (token.type != GRAPH) {
                fail("'graph' or 'digraph' expected");
            }
            nextToken();
            if (token.type == ID) {
                graph.setName(label(token).toString());
                nextToken();
            }
            statementList(graph);
            assertToken(EOF);
            return graph;
        });
    }

    private Label label(Token token) {
        return token.subtype == SUB_HTML ? Label.html(token.value) : Label.of(token.value);
    }

    private void statementList(MutableGraph graph) throws IOException {
        assertToken(BRACE_OPEN);
        while (statement(graph)) {
            if (token.type == SEMICOLON) {
                nextToken();
            }
        }
        assertToken(BRACE_CLOSE);
    }

    private boolean statement(MutableGraph graph) throws IOException {
        final Token base = token;
        switch (base.type) {
            case ID:
                nextToken();
                if (token.type == EQUAL) {
                    applyMutableAttributes(graph.graphAttrs(), Arrays.asList(base, nextToken(ID)));
                    nextToken();
                } else {
                    final PortNode nodeId = nodeId(base);
                    if (token.type == MINUS_MINUS || token.type == ARROW) {
                        edgeStatement(graph, nodeId);
                    } else {
                        nodeStatement(graph, nodeId);
                    }
                }
                return true;
            case SUBGRAPH:
            case BRACE_OPEN:
                final MutableGraph sub = subgraph(graph.isDirected());
                if (token.type == MINUS_MINUS || token.type == ARROW) {
                    edgeStatement(graph, sub);
                } else {
                    graph.add(sub);
                }
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

    private MutableGraph subgraph(boolean directed) {
        return CreationContext.use(ctx -> {
            final MutableGraph sub = mutGraph().setDirected(directed);
            if (token.type == SUBGRAPH) {
                nextToken();
                if (token.type == ID) {
                    sub.setName(label(token).toString());
                    nextToken();
                }
            }
            statementList(sub);
            return sub;
        });
    }

    private void edgeStatement(MutableGraph graph, LinkSource linkSource)
            throws IOException {
        final List<LinkSource> points = new ArrayList<>();
        points.add(linkSource);
        do {
            if (graph.isDirected() && token.type == MINUS_MINUS) {
                fail("-- used in digraph. Use -> instead.");
            }
            if (!graph.isDirected() && token.type == ARROW) {
                fail("-> used in graph. Use -- instead.");
            }
            nextToken();
            if (token.type == ID) {
                final Token id = token;
                nextToken();
                points.add(nodeId(id));
            } else if (token.type == SUBGRAPH || token.type == BRACE_OPEN) {
                points.add(subgraph(graph.isDirected()));
            }
        } while (token.type == MINUS_MINUS || token.type == ARROW);
        final List<Token> attrs = (token.type == BRACKET_OPEN) ? attributeList() : Collections.emptyList();
        for (int i = 0; i < points.size() - 1; i++) {
            final LinkSource from = points.get(i);
            final LinkTarget to = (LinkTarget) points.get(i + 1);
            from.links().add(applyAttributes(Link.to(to), attrs));
            graph.add(from);
        }
    }

    private Compass compass(String name) {
        return Compass.of(name).orElseThrow(() ->
                new ParserException(lexer.pos, "Invalid compass value '" + name + "'"));
    }

    private void nodeStatement(MutableGraph graph, PortNode nodeId) throws IOException {
        final MutableNode node = mutNode(nodeId.name());
        if (token.type == BRACKET_OPEN) {
            applyMutableAttributes(node, attributeList());
        }
        graph.add(node);
    }

    private PortNode nodeId(Token base) throws IOException {
        String record = null;
        Compass compass = null;
        if (token.type == COLON) {
            final String second = nextToken(ID).value;
            nextToken();
            if (token.type == COLON) {
                record = second;
                compass = compass(nextToken(ID).value);
                nextToken();
            } else {
                if (Compass.of(second).isPresent()) {
                    compass = compass(second);
                } else {
                    record = second;
                }
            }
        }
        return mutNode(label(base)).port(record, compass);
    }

    private void attributeStatement(MutableGraph graph) throws IOException {
        final MutableAttributed<?, ?> target = attributes(graph, token);
        nextToken();
        applyMutableAttributes(target, attributeList());
    }

    private void applyMutableAttributes(MutableAttributed<?, ?> attributed, List<Token> tokens) {
        for (int i = 0; i < tokens.size(); i += 2) {
            final String key = tokens.get(i).value;
            final Token value = tokens.get(i + 1);
            if ("label".equals(key) || "xlabel".equals(key) || "headlabel".equals(key) || "taillabel".equals(key)) {
                attributed.add(key, label(value));
            } else {
                attributed.add(key, value.value);
            }
        }
    }

    private <T extends Attributed<T, ?>> T applyAttributes(T attributed, List<Token> tokens) {
        T res = attributed;
        for (int i = 0; i < tokens.size(); i += 2) {
            res = res.with(tokens.get(i).value, tokens.get(i + 1).value);
        }
        return res;
    }

    private MutableAttributed<?, ?> attributes(MutableGraph graph, Token token) {
        switch (token.type) {
            case GRAPH:
                return graph.graphAttrs();
            case NODE:
                return CreationContext.get().nodeAttrs();
            case EDGE:
                return CreationContext.get().linkAttrs();
            default:
                throw new IllegalArgumentException("Unexpected token " + token);
        }
    }

    private List<Token> attributeList() throws IOException {
        final List<Token> res = new ArrayList<>();
        do {
            assertToken(BRACKET_OPEN);
            if (token.type == ID) {
                res.addAll(attrListElement());
            }
            assertToken(BRACKET_CLOSE);
        } while (token.type == BRACKET_OPEN);
        return res;
    }

    private List<Token> attrListElement() throws IOException {
        final List<Token> res = new ArrayList<>();
        do {
            res.add(token);
            nextToken(EQUAL);
            res.add(nextToken(ID));
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

    private Token nextToken(int type) throws IOException {
        nextToken();
        checkToken(type);
        return token;
    }

    private Token assertToken(int type) throws IOException {
        checkToken(type);
        return nextToken();
    }

    private void checkToken(int type) {
        if (token.type != type) {
            fail("'" + Token.desc(type) + "' expected");
        }
    }

    private void fail(String msg) {
        throw new ParserException(lexer.pos, msg);
    }
}
