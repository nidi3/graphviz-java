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
