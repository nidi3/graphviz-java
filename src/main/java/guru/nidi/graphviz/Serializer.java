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

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class Serializer {
    private final Graph graph;
    private final StringBuilder s;

    public Serializer(Graph graph) {
        this.graph = graph;
        s = new StringBuilder();
    }

    public String serialize() {
        graph(graph, true);
        return s.toString();
    }

    private void graph(Graph graph, boolean toplevel) {
        if (toplevel) {
            s.append(graph.strict ? "strict " : "").append(graph.directed ? "digraph " : "graph ");
            if (!graph.name.isEmpty()) {
                name(graph.name);
                s.append(" ");
            }
        } else if (!graph.name.isEmpty()) {
            s.append("subgraph ");
            name(graph.name);
            s.append(" ");
        }
        s.append("{\n");
        if (!graph.attributes.isEmpty()) {
            s.append("graph");
            attrs(graph.attributes);
            s.append("\n");
        }
        for (final Node node : linkedNodes(graph.nodes)) {
            if (!node.attributes.isEmpty() || (graph.nodes.contains(node) && node.links.isEmpty())) {
                node(node);
                s.append("\n");
            }
        }
        for (final Graph subgraph : graph.subgraphs) {
            if (subgraph.links.isEmpty()) {
                graph(subgraph, false);
                s.append("\n");
            }
        }

        edges(graph.nodes);
        edges(graph.subgraphs);

        s.append("}");
    }

    private Collection<Node> linkedNodes(Collection<Node> nodes) {
        final HashSet<Node> visited = new HashSet<>();
        for (final Node node : nodes) {
            linkedNodes(node, visited);
        }
        return visited;
    }

    private void linkedNodes(Node node, Set<Node> visited) {
        if (!visited.contains(node)) {
            visited.add(node);
            for (final Link link : node.links) {
                if (link.to instanceof NodePoint) {
                    linkedNodes(((NodePoint) link.to).node, visited);
                }
            }
        }
    }

    private Collection<Linkable> edges(Collection<? extends Linkable> linkables) {
        final HashSet<Linkable> visited = new HashSet<>();
        for (final Linkable linkable : linkables) {
            edges(linkable, visited);
        }
        return visited;
    }

    private void edges(Linkable linkable, Set<Linkable> visited) {
        if (!visited.contains(linkable)) {
            visited.add(linkable);
            for (final Link link : linkable.links()) {
                linkTarget(link.from);
                s.append(graph.directed ? " -> " : " -- ");
                linkTarget(link.to);
                attrs(link.attributes);
                s.append("\n");
                if (link.to instanceof NodePoint) {
                    edges(((NodePoint) link.to).node, visited);
                }
            }
        }
    }

    private void linkTarget(LinkTarget linkable) {
        if (linkable instanceof NodePoint) {
            point((NodePoint) linkable);
        } else if (linkable instanceof Graph) {
            graph((Graph) linkable, false);
        }
    }

    private void node(Node node) {
        name(node.name);
        attrs(node.attributes);
    }

    private void point(NodePoint point) {
        name(point.node.name);
        if (point.record != null) {
            s.append(":");
            name(Name.of(point.record));
        }
        if (point.compass != null) {
            s.append(":").append(point.compass.name().toLowerCase());
        }
    }

    private void name(Name name) {
        if (name != null) {
            s.append(name.html ? ("<" + name.value + ">") : ("\"" + name.value.replace("\"", "\\\"").replace("\n", "\\n") + "\""));
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
