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

public class Graph implements Linkable, LinkSource<Graph>, LinkTarget {
    protected boolean strict;
    protected boolean directed;
    protected boolean cluster;
    protected String name;
    protected final Map<String, Node> nodes;
    protected final Set<Graph> subgraphs;
    protected final List<Link> links;
    protected final Attributed<Graph> nodeAttrs;
    protected final Attributed<Graph> linkAttrs;
    protected final Attributed<Graph> graphAttrs;

    Graph() {
        this(false, false, false, "", new LinkedHashMap<>(), new LinkedHashSet<>(), new ArrayList<>(),
                null, null, null);
    }

    Graph(boolean strict, boolean directed, boolean cluster, String name,
          LinkedHashMap<String, Node> nodes, LinkedHashSet<Graph> subgraphs, List<Link> links,
          Attributes nodeAttrs, Attributes linkAttrs, Attributes graphAttrs) {
        this.strict = strict;
        this.directed = directed;
        this.cluster = cluster;
        this.name = name;
        this.nodes = nodes;
        this.subgraphs = subgraphs;
        this.links = links;
        this.nodeAttrs = new SimpleAttributed<>(this, nodeAttrs);
        this.linkAttrs = new SimpleAttributed<>(this, linkAttrs);
        this.graphAttrs = new SimpleAttributed<>(this, graphAttrs);
    }

    public Graph copy() {
        return new Graph(strict, directed, cluster, name,
                new LinkedHashMap<>(nodes), new LinkedHashSet<>(subgraphs), new ArrayList<>(links),
                nodeAttrs, linkAttrs, graphAttrs);
    }

    public Graph strict() {
        return strict(true);
    }

    public Graph strict(boolean strict) {
        this.strict = strict;
        return this;
    }

    public Graph directed() {
        return directed(true);
    }

    public Graph directed(boolean directed) {
        this.directed = directed;
        return this;
    }

    public Graph cluster() {
        return cluster(true);
    }

    public Graph cluster(boolean cluster) {
        this.cluster = cluster;
        return this;
    }

    public Graph name(String name) {
        this.name = name;
        return this;
    }

    public Graph use(ThrowingConsumer<Graph> actions) {
        GraphContext.use(this, actions);
        return this;
    }

    public Graph with(LinkSource... sources) {
        for (final LinkSource source : sources) {
            with(source);
        }
        return this;
    }

    public Graph with(LinkSource source) {
        if (source instanceof Node) {
            addNode((Node) source);
            return this;
        }
        if (source instanceof Port) {
            addNode(((Port) source).node);
            return this;
        }
        if (source instanceof Graph) {
            subgraphs.add((Graph) source);
            return this;
        }
        throw new IllegalArgumentException("Unknown source of type " + source.getClass());
    }

    private void addNode(Node node) {
        nodes.merge(node.name, node, Node::merge);
    }

    public Graph link(LinkTarget... targets) {
        for (final LinkTarget target : targets) {
            link(target);
        }
        return this;
    }

    public Graph link(LinkTarget target) {
        final Link link = target.linkTo();
        links.add(Link.between(this, link.to).with(link.attributes));
        return this;
    }

    public Collection<Node> nodes() {
        return nodes.values();
    }

    public Collection<Graph> graphs() {
        return subgraphs;
    }

    public Collection<Link> links() {
        return links;
    }

    @Override
    public Link linkTo() {
        return Link.to(this);
    }

    public boolean isStrict() {
        return strict;
    }

    public boolean isDirected() {
        return directed;
    }

    public boolean isCluster() {
        return cluster;
    }

    public String name() {
        return name;
    }

    public Attributed<Graph> nodeAttr() {
        return nodeAttrs;
    }

    public Attributed<Graph> linkAttr() {
        return linkAttrs;
    }

    public Attributed<Graph> graphAttr() {
        return graphAttrs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Graph graph = (Graph) o;

        if (strict != graph.strict) {
            return false;
        }
        if (directed != graph.directed) {
            return false;
        }
        if (cluster != graph.cluster) {
            return false;
        }
        if (!name.equals(graph.name)) {
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
        if (!nodeAttrs.equals(graph.nodeAttrs)) {
            return false;
        }
        if (!linkAttrs.equals(graph.linkAttrs)) {
            return false;
        }
        return graphAttrs.equals(graph.graphAttrs);

    }

    @Override
    public int hashCode() {
        int result = (strict ? 1 : 0);
        result = 31 * result + (directed ? 1 : 0);
        result = 31 * result + (cluster ? 1 : 0);
        result = 31 * result + name.hashCode();
        result = 31 * result + nodes.hashCode();
        result = 31 * result + subgraphs.hashCode();
        result = 31 * result + links.hashCode();
        result = 31 * result + nodeAttrs.hashCode();
        result = 31 * result + linkAttrs.hashCode();
        result = 31 * result + graphAttrs.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return new Serializer(this).serialize();
    }

}
