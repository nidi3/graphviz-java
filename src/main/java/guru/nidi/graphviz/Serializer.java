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

import java.util.*;

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
            if (!graph.label.isEmpty()) {
                s.append(graph.label.serialized()).append(" ");
            }
        } else if (!graph.label.isEmpty()) {
            s.append("subgraph ").append(graph.label.serialized()).append(" ");
        }
        s.append("{\n");

        attributes("graph", graph.graphAttributes);
        attributes("node", graph.nodeAttributes);
        attributes("edge", graph.linkAttributes);
        for (final Map.Entry<String, Object> attr : graph.attributes.attributes.entrySet()) {
            s.append(Label.of(attr.getKey()).serialized())
                    .append("=")
                    .append(Label.of(attr.getValue().toString()).serialized())
                    .append("\n");
        }

        for (final Linkable linkable : linkedNodes(graph.nodes)) {
            if (linkable instanceof Node) {
                final Node node = (Node) linkable;
                if (!node.attributes.isEmpty() || (graph.nodes.contains(node) && node.links.isEmpty())) {
                    node(node);
                    s.append("\n");
                }
            }
        }

        graph.subgraphs.stream()
                .filter(subgraph -> subgraph.links.isEmpty())
                .forEach(subgraph -> {
                    graph(subgraph, false);
                    s.append("\n");
                });

        final List<Node> nodes = new ArrayList<>();
        final List<Graph> graphs = new ArrayList<>();
        final Collection<Linkable> linkables = linkedNodes(graph.nodes);
        linkables.addAll(linkedNodes(graph.subgraphs));
        for (final Linkable linkable : linkables) {
            if (linkable instanceof Node) {
                final Node node = (Node) linkable;
                final int i = indexOfLabel(nodes, node.label);
                if (i < 0) {
                    nodes.add(node);
                } else {
                    nodes.set(i, node.merged(nodes.get(i)));
                }
            } else {
                graphs.add((Graph) linkable);
            }
        }

        edges(nodes);
        edges(graphs);

        s.append("}");
    }

    private int indexOfLabel(List<Node> nodes, Label label) {
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).label.equals(label)) {
                return i;
            }
        }
        return -1;
    }

    private void attributes(String name, SimpleAttributed<?> attributed) {
        if (!attributed.attributes.isEmpty()) {
            s.append(name);
            attrs(attributed.attributes);
            s.append("\n");
        }
    }

    private Collection<Linkable> linkedNodes(Collection<? extends Linkable> nodes) {
        final Set<Linkable> visited = new LinkedHashSet<>();
        for (final Linkable node : nodes) {
            linkedNodes(node, visited);
        }
        return visited;
    }

    private void linkedNodes(Linkable node, Set<Linkable> visited) {
        if (!visited.contains(node)) {
            visited.add(node);
            node.getLinks().stream()
                    .filter(link -> link.to instanceof NodePoint)
                    .forEach(link -> linkedNodes(((NodePoint) link.to).node, visited));
        }
    }

    private void edges(List<? extends Linkable> linkables) {
        for (final Linkable linkable : linkables) {
            for (final Link link : linkable.getLinks()) {
                linkTarget(link.from);
                s.append(graph.directed ? " -> " : " -- ");
                linkTarget(link.to);
                attrs(link.attributes);
                s.append("\n");
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
        s.append(node.label.serialized());
        attrs(node.attributes);
    }

    private void point(NodePoint point) {
        s.append(point.node.label.serialized());
        if (point.record != null) {
            s.append(":");
            s.append(Label.of(point.record).serialized());
        }
        if (point.compass != null) {
            s.append(":").append(point.compass.name().toLowerCase());
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
                s.append(Label.of(attr.getKey()).serialized())
                        .append("=")
                        .append(Label.of(attr.getValue().toString()).serialized());
            }
            s.append("]");
        }
    }
}
