package guru.nidi.graphviz;

import java.util.*;

/**
 *
 */
public class Graph {
    final boolean strict;
    final boolean directed;
    final Name name;
    final Map<String, Object> attributes = new HashMap<>();
    final Set<Node> nodes = new LinkedHashSet<>();

    private Graph(boolean strict, boolean directed, Name name) {
        this.strict = strict;
        this.directed = directed;
        this.name = name;
    }

    public static Graph named(Name name) {
        return new Graph(false, false, name);
    }

    public static Graph named(String name) {
        return named(Name.of(name));
    }

    public Graph strict() {
        return new Graph(true, directed, name);
    }

    public Graph directed() {
        return new Graph(strict, true, name);
    }

    public Graph attr(String name, Object value) {
        attributes.put(name, value);
        return this;
    }

    public Graph node(Node node) {
        nodes.add(node);
        return this;
    }
}
