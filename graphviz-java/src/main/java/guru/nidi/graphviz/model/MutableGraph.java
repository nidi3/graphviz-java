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

public class MutableGraph implements Linkable, MutableLinkSource<MutableGraph>, LinkTarget {
    protected boolean strict;
    protected boolean directed;
    protected boolean cluster;
    protected String name;
    protected final Set<MutableNode> nodes;
    protected final Set<MutableGraph> subgraphs;
    protected final List<Link> links;
    protected final MutableAttributed<MutableGraph> nodeAttrs;
    protected final MutableAttributed<MutableGraph> linkAttrs;
    protected final MutableAttributed<MutableGraph> graphAttrs;

    MutableGraph() {
        this(false, false, false, "", new LinkedHashSet<>(), new LinkedHashSet<>(), new ArrayList<>(),
                null, null, null);
        CreationContext.current().ifPresent(ctx -> {
            graphAttrs().add(ctx.graphs());
        });
    }

    protected MutableGraph(boolean strict, boolean directed, boolean cluster, String name,
                           LinkedHashSet<MutableNode> nodes, LinkedHashSet<MutableGraph> subgraphs, List<Link> links,
                           Attributes nodeAttrs, Attributes linkAttrs, Attributes graphAttrs) {
        this.strict = strict;
        this.directed = directed;
        this.cluster = cluster;
        this.name = name;
        this.nodes = nodes;
        this.subgraphs = subgraphs;
        this.links = links;
        this.nodeAttrs = new SimpleMutableAttributed<>(this, nodeAttrs);
        this.linkAttrs = new SimpleMutableAttributed<>(this, linkAttrs);
        this.graphAttrs = new SimpleMutableAttributed<>(this, graphAttrs);
    }

    public MutableGraph copy() {
        return new MutableGraph(strict, directed, cluster, name,
                new LinkedHashSet<>(nodes), new LinkedHashSet<>(subgraphs), new ArrayList<>(links),
                nodeAttrs, linkAttrs, graphAttrs);
    }

    public MutableGraph setStrict(boolean strict) {
        this.strict = strict;
        return this;
    }

    public MutableGraph setDirected(boolean directed) {
        this.directed = directed;
        return this;
    }

    public MutableGraph setCluster(boolean cluster) {
        this.cluster = cluster;
        return this;
    }

    public MutableGraph setName(String name) {
        this.name = name;
        return this;
    }

    public MutableGraph add(LinkSource... sources) {
        for (final LinkSource source : sources) {
            add(source);
        }
        return this;
    }

    public MutableGraph add(LinkSource source) {
        if (source instanceof MutableNode) {
            nodes.add((MutableNode) source);
            return this;
        }
        if (source instanceof MutablePortNode) {
            nodes.add(((MutablePortNode) source).node);
            return this;
        }
        if (source instanceof MutableGraph) {
            subgraphs.add((MutableGraph) source);
            return this;
        }
        throw new IllegalArgumentException("Unknown source of type " + source.getClass());
    }

    public MutableGraph addLink(LinkTarget... targets) {
        for (final LinkTarget target : targets) {
            addLink(target);
        }
        return this;
    }

    public MutableGraph addLink(LinkTarget target) {
        final Link link = target.linkTo();
        links.add(Link.between(this, link.to).with(link.attributes));
        return this;
    }

    public Collection<MutableNode> rootNodes() {
        return nodes;
    }

    public Collection<MutableNode> nodes() {
        final HashSet<MutableNode> ns = new HashSet<>();
        for (final MutableNode node : nodes) {
            collectNodes(node, ns);
        }
        return ns;
    }

    private void collectNodes(MutableNode node, Set<MutableNode> visited) {
        if (!visited.contains(node)) {
            visited.add(node);
            for (final Link link : node.links()) {
                if (link.to instanceof MutablePortNode) {
                    collectNodes(((MutablePortNode) link.to).node, visited);
                }
            }
        }
    }

    public Collection<MutableGraph> graphs() {
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

    public MutableAttributed<MutableGraph> nodeAttrs() {
        return nodeAttrs;
    }

    public MutableAttributed<MutableGraph> linkAttrs() {
        return linkAttrs;
    }

    public MutableAttributed<MutableGraph> graphAttrs() {
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

        final MutableGraph graph = (MutableGraph) o;

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
