package guru.nidi.graphviz;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class Link {
    final Node node;
    final Map<String, Object> attributes = new HashMap<>();

    private Link(Node node) {
        this.node = node;
    }

    public static Link to(Node node) {
        return new Link(node);
    }

    public Link attr(String name, Object value) {
        attributes.put(name, value);
        return this;
    }

}
