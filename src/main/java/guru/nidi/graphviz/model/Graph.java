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
package guru.nidi.graphviz.model;

import guru.nidi.graphviz.attribute.Attributed;

import java.util.*;

/**
 *
 */
public class Graph implements Linkable, LinkSource, LinkTarget {
    public final boolean strict;
    public final boolean directed;
    public final boolean cluster;
    public final Label label;
    final Set<Node> nodes = new LinkedHashSet<>();
    final Set<Graph> subgraphs = new LinkedHashSet<>();
    final List<Link> links = new ArrayList<>();
    final SimpleAttributed<Graph> attributes = new SimpleAttributed<>(this);
    final SimpleAttributed<Graph> nodeAttributes = new SimpleAttributed<>(this);
    final SimpleAttributed<Graph> linkAttributes = new SimpleAttributed<>(this);
    final SimpleAttributed<Graph> graphAttributes = new SimpleAttributed<>(this);

    private Graph(boolean strict, boolean directed, boolean cluster, Label label) {
        this.strict = strict;
        this.directed = directed;
        this.cluster = cluster;
        this.label = label;
        final CreationContext ctx = CreationContext.current();
        if (ctx != null) {
            general().attr(ctx.graphs());
        }
    }

    public static Graph named(Label label) {
        return new Graph(false, false, false, label);
    }

    public static Graph named(String name) {
        return named(Label.of(name));
    }

    public static Graph nameless() {
        return named("");
    }

    public Graph strict() {
        return new Graph(true, directed, false, label);
    }

    public Graph directed() {
        return new Graph(strict, true, false, label);
    }

    public Graph cluster() {
        return new Graph(strict, directed, true, label);
    }

    public Graph labeled(Label label) {
        return new Graph(strict, directed, cluster, label);
    }

    public Graph withNodes(Node... nodes) {
        for (final Node node : nodes) {
            withNode(node);
        }
        return this;
    }

    Graph withNode(Node node) {
        nodes.add(node);
        return this;
    }

    public Graph withNodes(String... nodes) {
        for (final String node : nodes) {
            withNode(node);
        }
        return this;
    }

    Graph withNode(String node) {
        return withNode(Node.named(node));
    }

    public Graph withGraphs(Graph... subgraphs) {
        for (final Graph subgraph : subgraphs) {
            withGraph(subgraph);
        }
        return this;
    }

    Graph withGraph(Graph subgraph) {
        subgraphs.add(subgraph);
        return this;
    }

    public Graph with(LinkSource source) {
        if (source instanceof Node) {
            return withNode((Node) source);
        } else if (source instanceof NodePoint) {
            return withNode(((NodePoint) source).node);
        } else if (source instanceof Graph) {
            return withGraph((Graph) source);
        }
        throw new IllegalArgumentException("Unknown source of type " + source.getClass());
    }

    public Graph link(LinkTarget... targets) {
        for (final LinkTarget target : targets) {
            link(target);
        }
        return this;
    }

    @Override
    public Graph link(LinkTarget target) {
        final Link link = target.linkTo();
        links.add(Link.between(this, link.to).attr(link.attributes));
        return this;
    }

    public Attributed<Graph> nodes() {
        return nodeAttributes;
    }

    public Attributed<Graph> links() {
        return linkAttributes;
    }

    public Attributed<Graph> graphs() {
        return graphAttributes;
    }

    public Attributed<Graph> general() {
        return attributes;
    }

    @Override
    public Label getName() {
        return label;
    }

    @Override
    public Collection<Link> getLinks() {
        return links;
    }

    @Override
    public Link linkTo() {
        return Link.to(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Graph graph = (Graph) o;

        if (strict != graph.strict) {
            return false;
        }
        if (directed != graph.directed) {
            return false;
        }
        if (cluster != graph.cluster) {
            return false;
        }
        if (!label.equals(graph.label)) {
            return false;
        }
        if (!nodes.equals(graph.nodes)) {
            return false;
        }
        if (!subgraphs.equals(graph.subgraphs)) {
            return false;
        }
        if (!links.equals(graph.links)) {
            return false;
        }
        if (!attributes.equals(graph.attributes)) {
            return false;
        }
        if (!nodeAttributes.equals(graph.nodeAttributes)) {
            return false;
        }
        if (!linkAttributes.equals(graph.linkAttributes)) {
            return false;
        }
        return graphAttributes.equals(graph.graphAttributes);

    }

    @Override
    public int hashCode() {
        int result = (strict ? 1 : 0);
        result = 31 * result + (directed ? 1 : 0);
        result = 31 * result + (cluster ? 1 : 0);
        result = 31 * result + label.hashCode();
        result = 31 * result + nodes.hashCode();
        result = 31 * result + subgraphs.hashCode();
        result = 31 * result + links.hashCode();
        result = 31 * result + attributes.hashCode();
        result = 31 * result + nodeAttributes.hashCode();
        result = 31 * result + linkAttributes.hashCode();
        result = 31 * result + graphAttributes.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return new Serializer(this).serialize();
    }
}
