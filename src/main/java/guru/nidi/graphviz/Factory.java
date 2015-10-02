package guru.nidi.graphviz;

/**
 *
 */
public class Factory {
    private Factory() {
    }

    public static Graph graph(String name) {
        return Graph.named(name);
    }

    public static Graph graph(Name name) {
        return Graph.named(name);
    }

    public static Node node(String name) {
        return Node.named(name);
    }

    public static Node node(Name name) {
        return Node.named(name);
    }

    public static Name name(String name) {
        return Name.of(name);
    }

    public static Name html(String html) {
        return Name.html(html);
    }

    public static Link to(Node node) {
        return Link.to(node);
    }
}
