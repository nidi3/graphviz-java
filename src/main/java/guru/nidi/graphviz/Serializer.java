package guru.nidi.graphviz;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 */
class Serializer {
    private final Graph graph;
    private final StringBuilder s;

    public Serializer(Graph graph) {
        this.graph = graph;
        s = new StringBuilder();
    }

    public String serialize() {
        graph(graph);
        return s.toString();
    }

    private void graph(Graph graph) {
        s.append(graph.strict ? "strict " : "").append(graph.directed ? "digraph " : "graph ");
        name(graph.name);
        s.append(" {\n");
        if (!graph.attributes.isEmpty()) {
            s.append("graph");
            attrs(graph.attributes);
            s.append("\n");
        }
        for (final Node node : graph.nodes) {
            if (!node.attributes.isEmpty()) {
                node(node);
                s.append("\n");
            }
        }
        edges(graph.nodes);
        s.append("}");
    }

    private void edges(Collection<Node> nodes) {
        final HashSet<Node> visited = new HashSet<>();
        for (final Node node : nodes) {
            edges(node, visited);
        }
    }

    private void edges(Node node, Set<Node> visited) {
        if (!visited.contains(node)) {
            visited.add(node);
            for (final Link link : node.links) {
                node(node);
                s.append(graph.directed ? " -> " : " -- ");
                node(link.node);
                s.append("\n");
                edges(link.node, visited);
            }
        }
    }

    private void node(Node node) {
        name(node.name);
        attrs(node.attributes);
    }

    private void name(Name name) {
        if (name != null) {
            s.append(name.html ? ("<" + name.value + ">") : ("\"" + name.value.replace("\"", "\\\"") + "\""));
        }
    }

    private void attrs(Map<String, Object> attrs) {
        if (!attrs.isEmpty()) {
            s.append(" [");
            boolean first = true;
            for (final Map.Entry<String, Object> attr : attrs.entrySet()) {
                if (first) {
                    first = false;
                } else {
                    s.append(",");
                }
                name(Name.of(attr.getKey()));
                s.append("=");
                name(Name.of(attr.getValue().toString()));
            }
            s.append("]");
        }
    }
}
