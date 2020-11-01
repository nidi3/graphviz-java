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
import guru.nidi.graphviz.attribute.validate.AttributeValidator;
import guru.nidi.graphviz.attribute.validate.AttributeValidator.Scope;
import guru.nidi.graphviz.attribute.validate.ValidatorMessage;
import guru.nidi.graphviz.model.*;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

import static guru.nidi.graphviz.model.Factory.mutGraph;
import static guru.nidi.graphviz.model.Factory.mutNode;
import static guru.nidi.graphviz.parse.Token.*;

final class ParserImpl {
    private Token token;
    private final Lexer lexer;
    private final AttributeValidator validator;
    @Nullable
    private final Consumer<ValidatorMessage> messageConsumer;

    ParserImpl(Lexer lexer, AttributeValidator validator,
               @Nullable Consumer<ValidatorMessage> messageConsumer) throws IOException {
        this.lexer = lexer;
        this.validator = validator;
        this.messageConsumer = messageConsumer;
        token = nextToken();
    }

    MutableGraph parse() {
        return CreationContext.use(ctx -> {
            final MutableGraph graph = mutGraph();
            if (token.type == STRICT) {
                graph.setStrict(true);
                nextToken();
            }
            if (token.type == DIGRAPH) {
                graph.setDirected(true);
            } else if (token.type != GRAPH) {
                fail("'graph' or 'digraph' expected.");
            }
            nextToken();
            if (token.type == ID) {
                graph.setName(label(token).toString());
                nextToken();
            }
            statementList(graph, false);
            assertToken(EOF);
            return deduplicateNodes(graph);
        });
    }

    private Label label(Token token) {
        return token.subtype == SUB_HTML ? Label.html(token.value) : Label.of(token.value);
    }

    private void statementList(MutableGraph graph, boolean isSub) throws IOException {
        assertToken(BRACE_OPEN);
        while (statement(graph, isSub)) {
            if (token.type == SEMICOLON) {
                nextToken();
            }
        }
        assertToken(BRACE_CLOSE);
    }

    private boolean statement(MutableGraph graph, boolean isSub) throws IOException {
        final Token base = token;
        switch (base.type) {
            case ID:
                final Position pos = lexer.pos.copy(-base.value.length());
                nextToken();
                if (token.type == EQUAL) {
                    final Token value = nextToken(ID);
                    final Scope scope = isSub ? Scope.SUB_GRAPH : Scope.GRAPH;
                    validate(base, value, scope, pos);
                    applyMutableAttributes(graph.graphAttrs(), Arrays.asList(base, value));
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
                attributeStatement(graph, isSub);
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
                    final String name = label(token).toString();
                    if (name.startsWith("cluster_")) {
                        sub.setName(name.substring(8));
                        sub.setCluster(true);
                    } else {
                        sub.setName(name);
                    }
                    nextToken();
                }
            }
            statementList(sub, true);
            return deduplicateNodes(sub);
        });
    }

    //we add nodes (and others?) which possibly change afterwards
    //-> the hashcode changes -> same node is multiple times in the set of nodes
    //copy() normalizes this
    private MutableGraph deduplicateNodes(MutableGraph g) {
        return g.copy();
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
            } else {
                fail("node or 'graph' or '{' expected.");
            }
        } while (token.type == MINUS_MINUS || token.type == ARROW);
        final List<Token> attrs = (token.type == BRACKET_OPEN) ? attributeList(Scope.EDGE) : Collections.emptyList();
        for (int i = 0; i < points.size() - 1; i++) {
            final LinkSource from = points.get(i);
            final LinkTarget to = (LinkTarget) points.get(i + 1);
            from.links().add(applyAttributes(from.linkTo(to), attrs));
            graph.add(from);
        }
    }

    private Compass compass(String name) {
        return Compass.of(name).orElseThrow(() ->
                new ParserException(lexer.pos, "Invalid compass value '" + name + "'."));
    }

    private void nodeStatement(MutableGraph graph, PortNode nodeId) throws IOException {
        final MutableNode node = mutNode(nodeId.name());
        if (token.type == BRACKET_OPEN) {
            applyMutableAttributes(node, attributeList(Scope.NODE));
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

    private void attributeStatement(MutableGraph graph, boolean isSub) throws IOException {
        final MutableAttributed<?, ?> target = attributes(graph, token);
        final Scope scope = scope(token, isSub);
        nextToken();
        applyMutableAttributes(target, attributeList(scope));
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

    private Scope scope(Token token, boolean isSub) {
        switch (token.type) {
            case GRAPH:
                return isSub ? Scope.SUB_GRAPH : Scope.GRAPH;
            case NODE:
                return Scope.NODE;
            case EDGE:
                return Scope.EDGE;
            default:
                throw new IllegalArgumentException("Unexpected token " + token);
        }
    }

    private List<Token> attributeList(Scope scope) throws IOException {
        final List<Token> res = new ArrayList<>();
        do {
            assertToken(BRACKET_OPEN);
            if (token.type == ID) {
                res.addAll(attrListElement(scope));
            }
            assertToken(BRACKET_CLOSE);
        } while (token.type == BRACKET_OPEN);
        return res;
    }

    private List<Token> attrListElement(Scope scope) throws IOException {
        final List<Token> res = new ArrayList<>();
        do {
            final Token key = token;
            final Position pos = lexer.pos.copy(-key.value.length());
            nextToken(EQUAL);
            final Token value = nextToken(ID);
            validate(key, value, scope, pos);
            res.add(key);
            res.add(value);
            nextToken();
            if (token.type == SEMICOLON || token.type == COMMA) {
                nextToken();
            }
        } while (token.type == ID);
        return res;
    }

    private void validate(Token key, Token value, Scope scope, Position pos) {
        if (messageConsumer != null) {
            validator.validate(key.value, value.value, scope).forEach(msg -> messageConsumer.accept(
                    msg.at(new ValidatorMessage.Position(pos.getName(), pos.getLine(), pos.getCol()))));
        }
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
            fail("'" + Token.desc(type) + "' expected.");
        }
    }

    private void fail(String msg) {
        throw new ParserException(lexer.pos, msg);
    }
}
