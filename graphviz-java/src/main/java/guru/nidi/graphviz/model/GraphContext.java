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

import guru.nidi.graphviz.engine.GraphvizException;

import java.util.*;

final class GraphContext {
    private static final ThreadLocal<Stack<GraphContext>> CONTEXT = ThreadLocal.withInitial(Stack::new);
    private final Map<String, Node> nodes = new HashMap<>();

    private GraphContext() {}

    public static void use(Graph graph, ThrowingConsumer<Graph> actions) {
        begin();
        try {
            actions.accept(graph);
        } catch (Exception e) {
            throw new GraphvizException("Exception during graph creation", e);
        } finally {
            end();
        }
    }

    public static GraphContext get() {
        final Stack<GraphContext> cs = CONTEXT.get();
        if (cs.empty()) {
            throw new IllegalStateException("All operations must be executed inside a Graph.");
        }
        return cs.peek();
    }

    public static void begin() {
        CONTEXT.get().push(new GraphContext());
    }

    public static void end() {
        final Stack<GraphContext> cs = CONTEXT.get();
        if (!cs.empty()) {
            cs.pop();
        }
    }

    public static Node node(String name) {
        return get().newNode(name);
    }

    private Node newNode(String name) {
        return nodes.computeIfAbsent(name, Node::new);
    }

}
