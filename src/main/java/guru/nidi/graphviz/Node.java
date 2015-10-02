package guru.nidi.graphviz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;

/**
 *
 */
public class Node {
    final Name name;
    final Map<String, Object> attributes = new HashMap<>();
    final List<Link> links = new ArrayList<>();

    private Node(Name name) {
        this.name = name;
    }

    public static Node named(Name name) {
        return new Node(name);
    }

    public static Node named(String name) {
        return named(Name.of(name));
    }

    public Node attr(String name, Object value) {
        attributes.put(name, value);
        return this;
    }

    public Node link(Link link) {
        links.add(link);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Node node = (Node) o;

        return !(name != null ? !name.equals(node.name) : node.name != null);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return name + attributes.toString() + "->" +
                links.stream().map(l -> l.node.name.toString()).collect(joining(","));
    }

}
