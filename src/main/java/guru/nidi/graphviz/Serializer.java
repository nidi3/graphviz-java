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
class Serializer {
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
        } else {
            s.append("subgraph ");
        }
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
        for (final Graph subgraph : graph.subgraphs) {
            graph(subgraph, false);
        }
        edges(graph.nodes);
        s.append("}\n");
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
                point(link.from);
                s.append(graph.directed ? " -> " : " -- ");
                point(link.to);
                attrs(link.attributes);
                s.append("\n");
                edges(link.to.node, visited);
            }
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
        attrs(point.node.attributes);
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
