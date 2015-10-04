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
package guru.nidi.graphviz;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 *
 */
public class CreationContext {
    private final static ThreadLocal<CreationContext> context = new ThreadLocal<>();
    private final Map<Label, Node> nodes = new HashMap<>();
    private final SimpleAttributed<CreationContext> nodeAttributes = new SimpleAttributed<>(this);
    private final SimpleAttributed<CreationContext> linkAttributes = new SimpleAttributed<>(this);
    private final SimpleAttributed<CreationContext> graphAttributes = new SimpleAttributed<>(this);

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

    public static CreationContext current() {
        return context.get();
    }

    public static CreationContext begin() {
        CreationContext ctx = current();
        if (ctx == null) {
            ctx = new CreationContext();
            context.set(ctx);
        }
        return ctx;
    }

    public static void end() {
        context.remove();
    }

    public Attributed<CreationContext> nodes() {
        return nodeAttributes;
    }

    public Attributed<CreationContext> links() {
        return linkAttributes;
    }

    public Attributed<CreationContext> graphs() {
        return graphAttributes;
    }

    Link initLink(Link link) {
        return link.attr(linkAttributes.attributes);
    }

    Node getOrCreateNode(Label label) {
        return nodes.computeIfAbsent(label, Node::new).attr(nodeAttributes.attributes);
    }
}
