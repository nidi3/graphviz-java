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

/**
 *
 */
public class NodeContext {
    private final static ThreadLocal<NodeContext> context = new ThreadLocal<>();
    private final Map<Name, Node> nodes = new HashMap<>();

    public NodeContext() {
    }

    public NodeContext(Runnable runnable) {
        context.set(this);
        try {
            runnable.run();
        } finally {
            context.remove();
        }
    }

    public static void begin() {
        context.set(new NodeContext());
    }

    public static void end() {
        context.remove();
    }

    static Node getOrCreateNode(Name name) {
        final NodeContext ctx = context.get();
        if (ctx == null) {
            return new Node(name);
        }
        return ctx.nodes.computeIfAbsent(name, Node::new);
    }
}
