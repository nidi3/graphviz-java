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

import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;

import static java.util.Arrays.asList;

public class MutableGraph implements LinkSource, LinkTarget {
    private static final SafeRecursion<MutableGraph> RECURSION = new SafeRecursion<>();
    private static final String CONTEXT = "$context";

    protected boolean strict;
    protected boolean directed;
    protected boolean cluster;
    protected Label name;
    protected final Set<MutableNode> nodes;
    protected final Set<MutableGraph> subgraphs;
    protected final LinkList links;
    protected final MutableAttributed<MutableGraph, ForNode> nodeAttrs;
    protected final MutableAttributed<MutableGraph, ForLink> linkAttrs;
    protected final MutableAttributed<MutableGraph, ForGraph> graphAttrs;

    MutableGraph() {
        this(false, false, false, Label.of(""), new LinkedHashSet<>(), new LinkedHashSet<>(), new ArrayList<>(),
                null, null, null);
        CreationContext.current().ifPresent(ctx -> graphAttrs().add(ctx.graphAttrs()));
    }

    protected MutableGraph(boolean strict, boolean directed, boolean cluster, Label name,
                           LinkedHashSet<MutableNode> nodes, LinkedHashSet<MutableGraph> subgraphs, List<Link> links,
                           @Nullable Attributes<? extends ForNode> nodeAttrs,
                           @Nullable Attributes<? extends ForLink> linkAttrs,
                           @Nullable Attributes<? extends ForGraph> graphAttrs) {
        this.strict = strict;
        this.directed = directed;
        this.cluster = cluster;
        this.name = name;
        this.nodes = nodes;
        this.subgraphs = subgraphs;
        this.links = new LinkList(this, links);
        this.nodeAttrs = new SimpleMutableAttributed<>(this, nodeAttrs);
        this.linkAttrs = new SimpleMutableAttributed<>(this, linkAttrs);
        this.graphAttrs = new SimpleMutableAttributed<>(this, graphAttrs);
    }

    public MutableGraph copy() {
        return new MutableGraph(strict, directed, cluster, name,
                new LinkedHashSet<>(nodes), new LinkedHashSet<>(subgraphs), links,
                nodeAttrs, linkAttrs, graphAttrs);
    }

    public Graph toImmutable() {
        return ImmutableGraph.copyOfMut(this);
    }

    public MutableGraph use(ThrowingBiConsumer<MutableGraph, CreationContext> actions) {
        return CreationContext.use(this, ctx -> {
            graphAttrs.add(CONTEXT, ctx);
            actions.accept(this, ctx);
            return this;
        });
    }

    public MutableGraph reuse(ThrowingBiConsumer<MutableGraph, CreationContext> actions) {
        final Object c = graphAttrs.get(CONTEXT);
        if (c instanceof CreationContext) {
            return ((CreationContext) c).reuse(ctx -> {
                actions.accept(this, ctx);
                return this;
            });
        }
        return use(actions);
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
        this.name = Label.of(name);
        return this;
    }

    public MutableGraph add(LinkSource... sources) {
        return add(asList(sources));
    }

    public MutableGraph add(List<? extends LinkSource> sources) {
        for (final LinkSource source : sources) {
            add(source);
        }
        return this;
    }

    public MutableGraph add(LinkSource source) {
        source.addTo(this);
        return this;
    }

    public MutableGraph addLink(LinkTarget... targets) {
        for (final LinkTarget target : targets) {
            addLink(target);
        }
        return this;
    }

    public MutableGraph addLink(LinkTarget target) {
        links.add(linkTo(target));
        return this;
    }

    @Override
    public Link linkTo(LinkTarget target) {
        final Link link = target.linkTo();
        return Link.between(this, link.to).with(link.attributes);
    }

    @Override
    public Link linkTo() {
        return Link.to(this);
    }

    @Override
    public void addTo(MutableGraph graph) {
        graph.subgraphs.add(this);
    }

    @Override
    public LinkTarget asLinkTarget() {
        return this;
    }

    @Override
    public LinkSource asLinkSource() {
        return this;
    }

    public Collection<MutableNode> rootNodes() {
        return nodes;
    }

    public Collection<MutableNode> nodes() {
        return collectNodes(new HashSet<>(), new HashSet<>()).getKey();
    }

    public Collection<MutableGraph> graphs() {
        return subgraphs;
    }

    public List<Link> links() {
        return links;
    }

    public Collection<Link> rootEdges() {
        return links;
    }

    public Collection<Link> edges() {
        return collectNodes(new HashSet<>(), new HashSet<>()).getValue();
    }

    private Entry<Set<MutableNode>, Set<Link>> collectNodes(Set<MutableNode> nodes, Set<Link> links) {
        for (final MutableGraph graph : subgraphs) {
            graph.collectNodes(nodes, links);
        }
        for (final MutableNode node : this.nodes) {
            collectNodes(node, nodes, links);
        }
        collectLinks(this.links, nodes, links);
        return new AbstractMap.SimpleEntry<>(nodes, links);
    }

    private void collectNodes(MutableNode node, Set<MutableNode> nodes, Set<Link> links) {
        if (!nodes.contains(node)) {
            nodes.add(node);
            collectLinks(node.links(), nodes, links);
        }
    }

    private void collectLinks(List<Link> edges, Set<MutableNode> nodes, Set<Link> links) {
        for (final Link link : edges) {
            links.add(link);
            if (link.to instanceof ImmutablePortNode) {
                collectNodes(((ImmutablePortNode) link.to).node(), nodes, links);
            }
            //TODO link.to is graph
        }
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

    public Label name() {
        return name;
    }

    public MutableAttributed<MutableGraph, ForNode> nodeAttrs() {
        return nodeAttrs;
    }

    public MutableAttributed<MutableGraph, ForLink> linkAttrs() {
        return linkAttrs;
    }

    public MutableAttributed<MutableGraph, ForGraph> graphAttrs() {
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
        final MutableGraph that = (MutableGraph) o;
        return RECURSION.recurse(this, true, () -> strict == that.strict
                && directed == that.directed
                && cluster == that.cluster
                && Objects.equals(name, that.name)
                && Objects.equals(nodes, that.nodes)
                && Objects.equals(subgraphs, that.subgraphs)
                && Objects.equals(links, that.links)
                && Objects.equals(nodeAttrs, that.nodeAttrs)
                && Objects.equals(linkAttrs, that.linkAttrs)
                && Objects.equals(graphAttrs, that.graphAttrs));
    }

    @Override
    public int hashCode() {
        return RECURSION.recurse(this, 0, () -> Objects.hash(
                strict, directed, cluster, name, nodes, subgraphs, links, nodeAttrs, linkAttrs, graphAttrs));
    }

    @Override
    public String toString() {
        return new Serializer().notValidating().serialize(this);
    }
}
