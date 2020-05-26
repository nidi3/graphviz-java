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

import guru.nidi.graphviz.attribute.*;

import javax.annotation.Nullable;
import java.util.*;

public final class CreationContext {
    private static final ThreadLocal<Stack<CreationContext>> CONTEXT = ThreadLocal.withInitial(Stack::new);
    @Nullable
    private final MutableGraph graph;
    private final Map<String, ImmutableNode> immutableNodes = new HashMap<>();
    private final Map<String, MutableNode> mutableNodes = new HashMap<>();
    private final MutableAttributed<CreationContext, ForNode> nodeAttributes = new SimpleMutableAttributed<>(this);
    private final MutableAttributed<CreationContext, ForLink> linkAttributes = new SimpleMutableAttributed<>(this);
    private final MutableAttributed<CreationContext, ForGraph> graphAttributes = new SimpleMutableAttributed<>(this);

    private CreationContext(@Nullable MutableGraph graph) {
        this.graph = graph;
    }

    public static <T> T use(ThrowingFunction<CreationContext, T> actions) {
        return use(null, actions);
    }

    public static <T> T use(@Nullable MutableGraph graph, ThrowingFunction<CreationContext, T> actions) {
        final CreationContext ctx = begin(graph);
        try {
            return actions.applyNotThrowing(ctx);
        } finally {
            end();
        }
    }

    public <T> T reuse(ThrowingFunction<CreationContext, T> actions) {
        CONTEXT.get().push(this);
        try {
            return actions.applyNotThrowing(this);
        } finally {
            end();
        }
    }

    public static Optional<CreationContext> current() {
        final Stack<CreationContext> cs = CONTEXT.get();
        return cs.empty() ? Optional.empty() : Optional.of(cs.peek());
    }

    public static CreationContext get() {
        final Stack<CreationContext> cs = CONTEXT.get();
        if (cs.empty()) {
            throw new IllegalStateException("Not in a CreationContext");
        }
        return cs.peek();
    }

    private static CreationContext begin(@Nullable MutableGraph graph) {
        final CreationContext ctx = new CreationContext(graph);
        CONTEXT.get().push(ctx);
        return ctx;
    }

    private static void end() {
        final Stack<CreationContext> cs = CONTEXT.get();
        if (!cs.empty()) {
            final CreationContext ctx = cs.pop();
            if (ctx.graph != null) {
                ctx.graph.graphAttrs().add(ctx.graphAttributes);
            }
        }
    }

    public MutableAttributed<CreationContext, ForNode> nodeAttrs() {
        return nodeAttributes;
    }

    public MutableAttributed<CreationContext, ForLink> linkAttrs() {
        return linkAttributes;
    }

    public MutableAttributed<CreationContext, ForGraph> graphAttrs() {
        return graphAttributes;
    }

    static ImmutableNode createNode(Label name) {
        return current()
                .map(ctx -> ctx.newNode(name))
                .orElseGet(() -> new ImmutableNode(name));
    }

    private ImmutableNode newNode(Label name) {
        return immutableNodes.computeIfAbsent(name.value(), l -> new ImmutableNode(name)).with(nodeAttributes);
    }

    static MutableGraph createMutGraph() {
        return current()
                .map(CreationContext::newMutGraph)
                .orElseGet(MutableGraph::new);
    }

    private MutableGraph newMutGraph() {
        final MutableGraph mg = new MutableGraph();
        if (graph != null) {
            graph.add(mg);
        }
        return mg;
    }

    static MutableNode createMutNode(Label name) {
        return current()
                .map(ctx -> ctx.newMutNode(name))
                .orElseGet(() -> new MutableNode(name));
    }

    private MutableNode newMutNode(Label name) {
        return mutableNodes.computeIfAbsent(name.value(), l -> addMutNode(new MutableNode(name)).add(nodeAttributes));
    }

    private MutableNode addMutNode(MutableNode node) {
        if (graph != null) {
            graph.add(node);
        }
        return node;
    }

    static Link createLink(@Nullable LinkSource from, LinkTarget to) {
        final Link link = new Link(from, to, Attributes.attrs());
        return current()
                .map(ctx -> link.with(ctx.linkAttributes))
                .orElse(link);
    }
}
