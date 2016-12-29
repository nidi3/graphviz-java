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
package guru.nidi.graphviz.model;

import guru.nidi.graphviz.attribute.MutableAttributed;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class CreationContext {
    private final static ThreadLocal<CreationContext> context = new ThreadLocal<>();
    private final Map<Label, ImmutableNode> nodes = new HashMap<>();
    private final MutableAttributed<CreationContext> nodeAttributes = new SimpleMutableAttributed<>(this);
    private final MutableAttributed<CreationContext> linkAttributes = new SimpleMutableAttributed<>(this);
    private final MutableAttributed<CreationContext> graphAttributes = new SimpleMutableAttributed<>(this);

    private CreationContext() {
    }

    public CreationContext(Consumer<CreationContext> consumer) {
        context.set(this);
        try {
            consumer.accept(this);
        } finally {
            context.remove();
        }
    }

    public static Optional<CreationContext> current() {
        return Optional.ofNullable(context.get());
    }

    public static CreationContext begin() {
        return current().orElseGet(() -> {
            final CreationContext ctx = new CreationContext();
            context.set(ctx);
            return ctx;
        });
    }

    public static void end() {
        context.remove();
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

    Node getOrCreateNode(Label label) {
        return nodes.computeIfAbsent(label, ImmutableNode::new).attr(nodeAttributes);
    }
}
