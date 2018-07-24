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
package guru.nidi.graphviz.model;

import guru.nidi.graphviz.attribute.Attributes;
import guru.nidi.graphviz.engine.GraphvizException;

import java.util.Stack;

public final class GraphContext {
    final Attributes nodeAttrs = Attributes.attrs();
    final Attributes edgeAttrs = Attributes.attrs();
    final Attributes graphAttrs = Attributes.attrs();
    private final Stack<Graph> graphs = new Stack<>();

    private static final ThreadLocal<GraphContext> CONTEXT = new ThreadLocal<>();

    private GraphContext() {
    }

    static Graph use(Graph graph, ThrowingConsumer<Graph> actions) {
        begin(graph);
        try {
            actions.accept(graph);
            return graph;
        } catch (Exception e) {
            throw new GraphvizException("Exception during graph creation", e);
        } finally {
            end();
        }
    }

    private static void begin(Graph graph) {
        GraphContext context = CONTEXT.get();
        if (context == null) {
            context = new GraphContext();
            CONTEXT.set(context);
        }
        context.graphs.push(graph);
    }

    private static void end() {
        final GraphContext context = CONTEXT.get();
        context.graphs.pop();
        if (context.graphs.isEmpty()) {
            CONTEXT.remove();
        }
    }

    static GraphContext context() {
        final GraphContext context = CONTEXT.get();
        if (context == null) {
            throw new IllegalStateException("All operations must be executed inside a Graph.");
        }
        return context;
    }

    static Graph graph(){
        return context().graphs.peek();
    }

    public static Node node(String name) {
        return node(new Node(name));
    }

    public static Node node(Node node) {
        return nodes.merge(node.name, node, Node::merge);

    }

    public static void nodeAttrs(Attributes... attributes) {
        get().nodeAttrs.with(attributes);
    }

    public static void edgeAttrs(Attributes... attributes) {
        get().edgeAttrs.with(attributes);
    }

    public static void graphAttrs(Attributes... attributes) {
        get().graphAttrs.with(attributes);
    }

}
