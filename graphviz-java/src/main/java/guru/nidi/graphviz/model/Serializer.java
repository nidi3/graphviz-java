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

import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.SimpleLabel;

import java.util.*;
import java.util.Map.Entry;

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
        final Collection<LinkSource> linkSources = linkedNodes(graph.nodes);
        linkSources.addAll(linkedNodes(graph.subgraphs));
        for (final LinkSource linkSource : linkSources) {
            if (linkSource instanceof MutableNode) {
                final MutableNode node = (MutableNode) linkSource;
                final int i = indexOfName(nodes, node.name);
                if (i < 0) {
                    nodes.add(node);
                } else {
                    nodes.set(i, node.copy().merge(nodes.get(i)));
                }
            } else {
                graphs.add((MutableGraph) linkSource);
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

    private void attributes(String name, MutableAttributed<?, ?> attributed) {
        if (!attributed.isEmpty()) {
            str.append(name);
            attrs(attributed);
            str.append('\n');
        }
    }

    private Collection<LinkSource> linkedNodes(Collection<? extends LinkSource> nodes) {
        final Set<LinkSource> visited = new LinkedHashSet<>();
        for (final LinkSource node : nodes) {
            linkedNodes(node, visited);
        }
        return visited;
    }

    private void linkedNodes(LinkSource linkSource, Set<LinkSource> visited) {
        if (!visited.contains(linkSource)) {
            visited.add(linkSource);
            for (final Link link : linkSource.links()) {
                linkedNodes(link.to.asLinkSource(), visited);
            }
        }
    }

    private void nodes(MutableGraph graph, List<MutableNode> nodes) {
        for (final MutableNode node : nodes) {
            if (!node.attributes.isEmpty()
                    || (graph.nodes.contains(node) && node.links.isEmpty() && !isLinked(node, nodes))) {
                node(node);
                str.append('\n');
            }
        }
    }

    private void node(MutableNode node) {
        str.append(node.name.serialized());
        attrs(node.attributes);
    }

    private boolean isLinked(MutableNode node, List<MutableNode> nodes) {
        for (final MutableNode m : nodes) {
            for (final Link link : m.links) {
                if (isNode(link.to, node)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isLinked(MutableGraph graph, List<? extends LinkSource> linkSources) {
        for (final LinkSource linkSource : linkSources) {
            for (final Link link : linkSource.links()) {
                if (link.to.equals(graph)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isNode(LinkTarget target, MutableNode node) {
        return target == node || (target instanceof ImmutablePortNode && ((ImmutablePortNode) target).node() == node);
    }

    private void graphs(List<MutableGraph> graphs, List<MutableNode> nodes) {
        for (final MutableGraph graph : graphs) {
            if (graph.links.isEmpty() && !isLinked(graph, nodes) && !isLinked(graph, graphs)) {
                graph(graph, false);
                str.append('\n');
            }
        }
    }

    private void edges(List<? extends LinkSource> linkSources) {
        for (final LinkSource linkSource : linkSources) {
            for (final Link link : linkSource.links()) {
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
            str.append(((MutableNode) linkable).name.serialized());
        } else if (linkable instanceof ImmutablePortNode) {
            port((ImmutablePortNode) linkable);
        } else if (linkable instanceof MutableGraph) {
            graph((MutableGraph) linkable, false);
        } else {
            throw new IllegalStateException("unexpected link target " + linkable);
        }
    }

    private void port(ImmutablePortNode portNode) {
        str.append(portNode.name().serialized());
        final String record = portNode.port().record();
        if (record != null) {
            str.append(':').append(SimpleLabel.of(record).serialized());
        }
        final Compass compass = portNode.port().compass();
        if (compass != null) {
            str.append(':').append(compass.value);
        }
    }

    private void attrs(MutableAttributed<?, ?> attrs) {
        if (!attrs.isEmpty()) {
            str.append(" [");
            boolean first = true;
            for (final Entry<String, Object> attr : attrs) {
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
