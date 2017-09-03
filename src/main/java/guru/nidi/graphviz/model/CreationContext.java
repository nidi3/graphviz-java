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

import guru.nidi.graphviz.attribute.MutableAttributed;
import guru.nidi.graphviz.attribute.SimpleMutableAttributed;

import java.util.*;
import java.util.concurrent.Callable;

public final class CreationContext {
    private static final ThreadLocal<Stack<CreationContext>> CONTEXT = ThreadLocal.withInitial(Stack::new);
    private final Map<Label, ImmutableNode> immutableNodes = new HashMap<>();
    private final Map<Label, MutableNode> mutableNodes = new HashMap<>();
    private final MutableAttributed<CreationContext> nodeAttributes = new SimpleMutableAttributed<>(this);
    private final MutableAttributed<CreationContext> linkAttributes = new SimpleMutableAttributed<>(this);
    private final MutableAttributed<CreationContext> graphAttributes = new SimpleMutableAttributed<>(this);

    private CreationContext() {
    }

    public static <T> T use(Callable<T> actions) {
        begin();
        try {
            return actions.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            end();
        }
    }

    public static Optional<CreationContext> current() {
        final Stack<CreationContext> cs = CONTEXT.get();
        return cs.empty() ? Optional.empty() : Optional.of(cs.peek());
    }

    public static CreationContext begin() {
        final CreationContext ctx = new CreationContext();
        CONTEXT.get().push(ctx);
        return ctx;
    }

    public static void end() {
        final Stack<CreationContext> cs = CONTEXT.get();
        if (!cs.empty()) {
            cs.pop();
        }
    }

    public MutableAttributed<CreationContext> nodes() {
        return nodeAttributes;
    }

    public MutableAttributed<CreationContext> links() {
        return linkAttributes;
    }

    public MutableAttributed<CreationContext> graphs() {
        return graphAttributes;
    }

    Node immutableNode(Label label) {
        return immutableNodes.computeIfAbsent(label, ImmutableNode::new).with(nodeAttributes);
    }

    MutableNode mutableNode(Label label) {
        return mutableNodes.computeIfAbsent(label, l -> new MutableNode().setLabel(l)).add(nodeAttributes);
    }
}
