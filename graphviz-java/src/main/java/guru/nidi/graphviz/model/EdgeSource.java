package guru.nidi.graphviz.model;

/**
 * can be linked.
 */
public interface EdgeSource {
    Linkable linkable();

    default Edge link(Port port) {
        return new Edge(linkable(), port);
    }

    default Edge link(Graph graph) {
        return new Edge(linkable(), graph);
    }

    default Edge link(Edge edge) {
        return new Edge(linkable(), edge.from);
    }

    default Edge link(Node node) {
        return link(node.port());
    }

    default Edge link(String node) {
        return link(GraphContext.get().node(node));
    }
}
