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
import java.util.Map.Entry;
import java.util.stream.Stream;

class Serializer {
    private final MutableGraph graph;
    private final StringBuilder str;

    Serializer(MutableGraph graph) {
        this.graph = graph;
        str = new StringBuilder();
    }

    String serialize() {
        toplevelGraph(graph);
        return str.toString();
    }

    private void toplevelGraph(MutableGraph graph) {
        final boolean useDir = hasDifferentlyDirectedSubgraphs(graph);
        str.append(graph.strict ? "strict " : "").append(graph.directed || useDir ? "digraph " : "graph ");
        if (!graph.name.isEmpty()) {
            str.append(SimpleLabel.of(graph.name).serialized()).append(' ');
        }
        doGraph(graph, useDir);
    }

    private void subGraph(MutableGraph graph, boolean useDir) {
        if (!graph.name.isEmpty() || graph.cluster) {
            str.append("subgraph ")
                    .append(Label.of((graph.cluster ? "cluster_" : "") + graph.name).serialized())
                    .append(' ');
        }
        doGraph(graph, useDir);
    }

    private void doGraph(MutableGraph graph, boolean useDir) {
        str.append("{\n");
        if (useDir && graph.graphAttrs.get("dir") == null) {
            attributes("edge", Attributes.attr("dir", graph.directed ? "forward" : "none"));
        }
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
        graphs(graphs, nodes, useDir);

        edges(nodes, useDir);
        edges(graphs, useDir);
        str.append('}');
    }

    private boolean hasDifferentlyDirectedSubgraphs(MutableGraph graph) {
        return Stream.concat(linkedNodes(graph.nodes).stream(), linkedNodes(graph.subgraphs).stream())
                .filter(n -> n instanceof MutableGraph)
                .map(n -> (MutableGraph) n)
                .anyMatch(sub -> sub.directed != graph.directed);
    }

    private void graphAttrs(MutableGraph graph) {
        attributes("graph", graph.graphAttrs);
        attributes("node", graph.nodeAttrs);
        attributes("edge", graph.linkAttrs);
    }

    private int indexOfName(List<MutableNode> nodes, Label name) {
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).name.equals(name)) {
                return i;
            }
        }
        return -1;
    }

    private void attributes(String name, Attributes<?> attributed) {
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

    private void graphs(List<MutableGraph> graphs, List<MutableNode> nodes, boolean useDir) {
        for (final MutableGraph graph : graphs) {
            if (graph.links.isEmpty() && !isLinked(graph, nodes) && !isLinked(graph, graphs)) {
                subGraph(graph, useDir);
                str.append('\n');
            }
        }
    }

    private void edges(List<? extends LinkSource> linkSources, boolean useDir) {
        for (final LinkSource linkSource : linkSources) {
            for (final Link link : linkSource.links()) {
                linkTarget(link.from, useDir);
                str.append(graph.directed || useDir ? " -> " : " -- ");
                linkTarget(link.to, useDir);
                attrs(link.attributes);
                str.append('\n');
            }
        }
    }

    private void linkTarget(Object linkable, boolean useDir) {
        if (linkable instanceof MutableNode) {
            str.append(((MutableNode) linkable).name.serialized());
        } else if (linkable instanceof ImmutablePortNode) {
            port((ImmutablePortNode) linkable);
        } else if (linkable instanceof MutableGraph) {
            subGraph((MutableGraph) linkable, useDir);
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

    private void attrs(Attributes<?> attrs) {
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
