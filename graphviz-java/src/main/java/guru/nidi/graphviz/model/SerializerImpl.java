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
import guru.nidi.graphviz.attribute.validate.AttributeValidator;
import guru.nidi.graphviz.attribute.validate.ValidatorMessage;
import guru.nidi.graphviz.attribute.validate.ValidatorMessage.Location;

import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static guru.nidi.graphviz.attribute.validate.AttributeValidator.Scope;

//TODO is creating a new Location always too costly?
class SerializerImpl {
    private final MutableGraph graph;
    private final StringBuilder str;
    private final AttributeValidator validator;
    @Nullable
    private final Consumer<ValidatorMessage> messageConsumer;

    SerializerImpl(MutableGraph graph, AttributeValidator validator,
                   @Nullable Consumer<ValidatorMessage> messageConsumer) {
        this.graph = graph;
        this.validator = validator;
        this.messageConsumer = messageConsumer;
        str = new StringBuilder();
    }

    String serialize() {
        toplevelGraph(graph);
        return str.toString();
    }

    private void toplevelGraph(MutableGraph graph) {
        final boolean useDir = hasDifferentlyDirectedSubgraphs(graph);
        str.append(graph.strict ? "strict " : "").append(graph.directed || useDir ? "digraph " : "graph ");
        if (!graph.name.isContentEmpty()) {
            str.append(graph.name.serialized()).append(' ');
        }
        doGraph(graph, useDir, Scope.GRAPH);
    }

    private void subGraph(MutableGraph graph, boolean useDir) {
        if (!graph.name.isContentEmpty() || graph.cluster) {
            str.append("subgraph ")
                    .append((graph.cluster ? Label.of("cluster_" + graph.name) : graph.name).serialized())
                    .append(' ');
        }
        doGraph(graph, useDir, graph.cluster ? Scope.CLUSTER : Scope.SUB_GRAPH);
    }

    private void doGraph(MutableGraph graph, boolean useDir, Scope scope) {
        str.append("{\n");
        if (useDir && graph.graphAttrs.get("dir") == null) {
            attributes("edge", Attributes.attr("dir", graph.directed ? "forward" : "none"), Scope.EDGE,
                    new Location(Location.Type.LINK, graph));
        }
        graphAttrs(graph, scope);

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

    private void graphAttrs(MutableGraph graph, Scope scope) {
        attributes("graph", graph.graphAttrs, scope, new Location(Location.Type.GRAPH_ATTRS, graph));
        attributes("node", graph.nodeAttrs, Scope.NODE, new Location(Location.Type.NODE_ATTRS, graph));
        attributes("edge", graph.linkAttrs, Scope.EDGE, new Location(Location.Type.LINK_ATTRS, graph));
    }

    private int indexOfName(List<MutableNode> nodes, Label name) {
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).name.equals(name)) {
                return i;
            }
        }
        return -1;
    }

    private void attributes(String name, Attributes<?> attributed, Scope scope, Location location) {
        final int len = str.length();
        str.append(name);
        attrs(attributed, scope, location);
        if (str.length() == len + name.length()) {
            str.delete(len, str.length());
        } else {
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
        attrs(node.attributes, Scope.NODE, new Location(Location.Type.NODE, node));
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
                attrs(link.attributes, Scope.EDGE, new Location(Location.Type.LINK, link));
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

    private void attrs(Attributes<?> attrs, Scope scope, Location location) {
        boolean first = true;
        for (final Entry<String, Object> attr : attrs) {
            if (!attr.getKey().startsWith("$") && attr.getValue() != null) {
                if (first) {
                    str.append(" [");
                    first = false;
                } else {
                    str.append(',');
                }
                attr(attr.getKey(), attr.getValue(), scope, location);
            }
        }
        if (!first) {
            str.append(']');
        }
    }

    private void attr(String key, Object value, Scope scope, Location location) {
        str.append(SimpleLabel.of(key).serialized())
                .append('=')
                .append(SimpleLabel.of(value).serialized());
        validate(key, value, scope, location);
    }

    private void validate(String key, Object value, Scope scope, Location location) {
        if (messageConsumer != null) {
            validator.validate(key, value, scope).forEach(msg -> messageConsumer.accept(msg.at(location)));
        }
    }
}
