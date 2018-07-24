/*
 * Copyright © 2015 Stefan Niederhauser (nidin@gmx.ch)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package guru.nidi.graphviz.model;

import guru.nidi.graphviz.attribute.*;

import java.util.*;

public class Serializer {
    private final Graph graph;
    private final StringBuilder str;

    public Serializer(Graph graph) {
        this.graph = graph;
        str = new StringBuilder();
    }

    public String serialize() {
        graph(graph, true);
        return str.toString();
    }

    private void graph(Graph graph, boolean toplevel) {
        graphInit(graph, toplevel);
        graphAttrs(graph);

        final List<Node> nodes = new ArrayList<>();
        final List<Graph> graphs = new ArrayList<>();
        final Collection<EdgeContainer> edgeContainers = linkedNodes(graph.nodes.values());
        edgeContainers.addAll(linkedNodes(graph.subgraphs));
        for (final EdgeContainer edgeContainer : edgeContainers) {
            if (edgeContainer instanceof Node) {
                final Node node = (Node) edgeContainer;
                final int i = indexOfName(nodes, node.name);
                if (i < 0) {
                    nodes.add(node);
                } else {
                    nodes.set(i, node.copy().merge(nodes.get(i)));
                }
            } else {
                graphs.add((Graph) edgeContainer);
            }
        }

        nodes(graph, nodes);
        graphs(graphs, nodes);

        edges(nodes);
        edges(graphs);

        str.append('}');
    }

    private void graphAttrs(Graph graph) {
        attributes("graph", graph.graphAttrs);
        attributes("node", graph.nodeAttrs);
        attributes("edge", graph.edgeAttrs);
    }

    private void graphInit(Graph graph, boolean toplevel) {
        if (toplevel) {
            str.append(graph.strict ? "strict " : "").append(graph.directed ? "digraph " : "graph ");
            if (!graph.name.isEmpty()) {
                str.append(SimpleLabel.of(graph.name).serialized()).append(' ');
            }
        } else if (!graph.name.isEmpty() || graph.cluster) {
            str.append("subgraph ")
                    .append(Label.of((graph.cluster ? "cluster_" : "") + graph.name).serialized())
                    .append(' ');
        }
        str.append("{\n");
    }

    private int indexOfName(List<Node> nodes, String name) {
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).name.equals(name)) {
                return i;
            }
        }
        return -1;
    }

    private void attributes(String name, Attributed<?> attributed) {
        if (!attributed.isEmpty()) {
            str.append(name);
            attrs(attributed);
            str.append('\n');
        }
    }

    private Collection<EdgeContainer> linkedNodes(Collection<? extends EdgeContainer> nodes) {
        final Set<EdgeContainer> visited = new LinkedHashSet<>();
        for (final EdgeContainer node : nodes) {
            linkedNodes(node, visited);
        }
        return visited;
    }

    private void linkedNodes(EdgeContainer edgeContainer, Set<EdgeContainer> visited) {
        if (!visited.contains(edgeContainer)) {
            visited.add(edgeContainer);
            for (final Edge edge : edgeContainer.edges()) {
                linkedNodes(edge.to.containedBy(),visited);
//                if (link.to instanceof Node) {
//                    linkedNodes((Node) link.to, visited);
//                } else if (link.to instanceof Port) {
//                    linkedNodes(((Port) link.to).node, visited);
//                } else if (link.to instanceof Graph) {
//                    linkedNodes((Graph) link.to, visited);
//                } else {
//                    throw new IllegalStateException("unexpected link to " + link.to + " of " + link.to.getClass());
//                }
            }
        }
    }

    private void nodes(Graph graph, List<Node> nodes) {
        for (final Node node : nodes) {
            if (!node.attributes.isEmpty() || (graph.nodes.values().contains(node) && node.edges.isEmpty())) {
                node(node);
                str.append('\n');
            }
        }
    }

    private void graphs(List<Graph> graphs, List<Node> nodes) {
        for (final Graph graph : graphs) {
            if (graph.edges.isEmpty() && !isLinked(graph, nodes) && !isLinked(graph, graphs)) {
                graph(graph, false);
                str.append('\n');
            }
        }
    }

    private boolean isLinked(Graph graph, List<? extends EdgeContainer> linkables) {
        for (final EdgeContainer edgeContainer : linkables) {
            for (final Edge edge : edgeContainer.edges()) {
                if (edge.to.equals(graph)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void edges(List<? extends EdgeContainer> linkables) {
        for (final EdgeContainer edgeContainer : linkables) {
            for (final Edge edge : edgeContainer.edges()) {
                linkTarget(edge.from);
                str.append(graph.directed ? " -> " : " -- ");
                linkTarget(edge.to);
                attrs(edge.attributes);
                str.append('\n');
            }
        }
    }

    private void linkTarget(Object linkable) {
        if (linkable instanceof Node) {
            node((Node) linkable);
        } else if (linkable instanceof Port) {
            port((Port) linkable);
        } else if (linkable instanceof Graph) {
            graph((Graph) linkable, false);
        } else {
            throw new IllegalStateException("unexpected link target " + linkable);
        }
    }

    private void node(Node node) {
        str.append(node.name);
        attrs(node.attributes);
    }

    private void port(Port port) {
        str.append(port.node.name);
        if (port.record != null) {
            str.append(':').append(SimpleLabel.of(port.record).serialized());
        }
        if (port.compass != null) {
            str.append(':').append(port.compass.value);
        }
    }

    private void attrs(Attributed<?> attrs) {
        if (!attrs.isEmpty()) {
            str.append(" [");
            boolean first = true;
            for (final Map.Entry<String, Object> attr : attrs) {
                if (first) {
                    first = false;
                } else {
                    str.append(',');
                }
                attr(attr.getKey(), attr.getValue());
            }
            str.append(']');
        }
    }

    private void attr(String key, Object value) {
        str.append(SimpleLabel.of(key).serialized())
                .append('=')
                .append(SimpleLabel.of(value).serialized());
    }
}
