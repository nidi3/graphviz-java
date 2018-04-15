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
    private final MutableGraph graph;
    private final StringBuilder str;

    public Serializer(MutableGraph graph) {
        this.graph = graph;
        str = new StringBuilder();
    }

    public String serialize() {
        graph(graph, true);
        return str.toString();
    }

    private void graph(MutableGraph graph, boolean toplevel) {
        graphInit(graph, toplevel);
        graphAttrs(graph);

        final List<MutableNode> nodes = new ArrayList<>();
        final List<MutableGraph> graphs = new ArrayList<>();
        final Collection<Linkable> linkables = linkedNodes(graph.nodes);
        linkables.addAll(linkedNodes(graph.subgraphs));
        for (final Linkable linkable : linkables) {
            if (linkable instanceof MutableNode) {
                final MutableNode node = (MutableNode) linkable;
                final int i = indexOfName(nodes, node.name);
                if (i < 0) {
                    nodes.add(node);
                } else {
                    nodes.set(i, node.copy().merge(nodes.get(i)));
                }
            } else {
                graphs.add((MutableGraph) linkable);
            }
        }

        nodes(graph, nodes);
        graphs(graphs, nodes);

        edges(nodes);
        edges(graphs);

        str.append('}');
    }

    private void graphAttrs(MutableGraph graph) {
        attributes("graph", graph.graphAttrs);
        attributes("node", graph.nodeAttrs);
        attributes("edge", graph.linkAttrs);
    }

    private void graphInit(MutableGraph graph, boolean toplevel) {
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

    private int indexOfName(List<MutableNode> nodes, Label name) {
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).name.equals(name)) {
                return i;
            }
        }
        return -1;
    }

    private void attributes(String name, MutableAttributed<?> attributed) {
        if (!attributed.isEmpty()) {
            str.append(name);
            attrs(attributed);
            str.append('\n');
        }
    }

    private Collection<Linkable> linkedNodes(Collection<? extends Linkable> nodes) {
        final Set<Linkable> visited = new LinkedHashSet<>();
        for (final Linkable node : nodes) {
            linkedNodes(node, visited);
        }
        return visited;
    }

    private void linkedNodes(Linkable linkable, Set<Linkable> visited) {
        if (!visited.contains(linkable)) {
            visited.add(linkable);
            for (final Link link : linkable.links()) {
                if (link.to instanceof MutableNode) {
                    linkedNodes((MutableNode) link.to, visited);
                } else if (link.to instanceof MutablePortNode) {
                    linkedNodes(((MutablePortNode) link.to).node, visited);
                } else if (link.to instanceof MutableGraph) {
                    linkedNodes((MutableGraph) link.to, visited);
                } else {
                    throw new IllegalStateException("unexpected link to " + link.to + " of " + link.to.getClass());
                }
            }
        }
    }

    private void nodes(MutableGraph graph, List<MutableNode> nodes) {
        for (final MutableNode node : nodes) {
            if (!node.attributes.isEmpty() || (graph.nodes.contains(node) && node.links.isEmpty())) {
                node(node);
                str.append('\n');
            }
        }
    }

    private void graphs(List<MutableGraph> graphs, List<MutableNode> nodes) {
        for (final MutableGraph graph : graphs) {
            if (graph.links.isEmpty() && !isLinked(graph, nodes) && !isLinked(graph, graphs)) {
                graph(graph, false);
                str.append('\n');
            }
        }
    }

    private boolean isLinked(MutableGraph graph, List<? extends Linkable> linkables) {
        for (final Linkable linkable : linkables) {
            for (final Link link : linkable.links()) {
                if (link.to.equals(graph)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void edges(List<? extends Linkable> linkables) {
        for (final Linkable linkable : linkables) {
            for (final Link link : linkable.links()) {
                linkTarget(link.from);
                str.append(graph.directed ? " -> " : " -- ");
                linkTarget(link.to);
                attrs(link.attributes);
                str.append('\n');
            }
        }
    }

    private void linkTarget(Object linkable) {
        if (linkable instanceof MutableNode) {
            node((MutableNode) linkable);
        } else if (linkable instanceof MutablePortNode) {
            port((MutablePortNode) linkable);
        } else if (linkable instanceof MutableGraph) {
            graph((MutableGraph) linkable, false);
        } else {
            throw new IllegalStateException("unexpected link target " + linkable);
        }
    }

    private void node(MutableNode node) {
        str.append(node.name.serialized());
        attrs(node.attributes);
    }

    private void port(MutablePortNode portNode) {
        str.append(portNode.node.name.serialized());
        if (portNode.record != null) {
            str.append(':').append(SimpleLabel.of(portNode.record).serialized());
        }
        if (portNode.compass != null) {
            str.append(':').append(portNode.compass.value);
        }
    }

    private void attrs(MutableAttributed<?> attrs) {
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
