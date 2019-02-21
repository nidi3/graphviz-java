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
import java.util.function.Function;

class ImmutableGraph extends MutableGraph implements Graph {
    ImmutableGraph() {
    }

    private ImmutableGraph(boolean strict, boolean directed, boolean cluster, String name,
                           LinkedHashSet<MutableNode> nodes, LinkedHashSet<MutableGraph> subgraphs, List<Link> links,
                           MutableAttributed<MutableGraph, ForNode> nodeAttributes,
                           MutableAttributed<MutableGraph, ForLink> linkAttributes,
                           MutableAttributed<MutableGraph, ForGraph> graphAttributes) {
        super(strict, directed, cluster, name, nodes, subgraphs, links,
                nodeAttributes, linkAttributes, graphAttributes);
    }

    private ImmutableGraph copyOfMut() {
        return new ImmutableGraph(strict, directed, cluster, name,
                new LinkedHashSet<>(nodes), new LinkedHashSet<>(subgraphs), new ArrayList<>(links),
                nodeAttrs, linkAttrs, graphAttrs);
    }

    public Graph strict() {
        return (ImmutableGraph) copyOfMut().setStrict(true);
    }

    public Graph directed() {
        return (ImmutableGraph) copyOfMut().setDirected(true);
    }

    public Graph cluster() {
        return (ImmutableGraph) copyOfMut().setCluster(true);
    }

    public Graph named(String name) {
        return (ImmutableGraph) copyOfMut().setName(name);
    }

    public Graph with(LinkSource... sources) {
        return (ImmutableGraph) copyOfMut().add(sources);
    }

    public Graph with(List<? extends LinkSource> sources) {
        return (ImmutableGraph) copyOfMut().add(sources);
    }

    public Graph link(LinkTarget... targets) {
        return (ImmutableGraph) copyOfMut().addLink(targets);
    }

    public Graph link(LinkTarget target) {
        return (ImmutableGraph) copyOfMut().addLink(target);
    }

    public Attributed<Graph, ForNode> nodeAttr() {
        return new GraphAttributed<>(MutableGraph::nodeAttrs);
    }

    public Attributed<Graph, ForLink> linkAttr() {
        return new GraphAttributed<>(MutableGraph::linkAttrs);
    }

    public Attributed<Graph, ForGraph> graphAttr() {
        return new GraphAttributed<>(MutableGraph::graphAttrs);
    }

    @Override
    public List<Link> links() {
        return Collections.unmodifiableList(super.links());
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
        return strict == that.strict
                && directed == that.directed
                && cluster == that.cluster
                && Objects.equals(name, that.name)
                && Objects.equals(nodes, that.nodes)
                && Objects.equals(subgraphs, that.subgraphs)
                && Objects.equals(links, that.links)
                && Objects.equals(nodeAttrs, that.nodeAttrs)
                && Objects.equals(linkAttrs, that.linkAttrs)
                && Objects.equals(graphAttrs, that.graphAttrs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(strict, directed, cluster, name, nodes, subgraphs, links, nodeAttrs, linkAttrs, graphAttrs);
    }

    @Override
    public String toString() {
        return new Serializer(this).serialize();
    }

    private class GraphAttributed<F extends For> implements Attributed<Graph, F> {
        private final Function<ImmutableGraph, MutableAttributed<MutableGraph, F>> attributeSource;

        public GraphAttributed(Function<ImmutableGraph, MutableAttributed<MutableGraph, F>> attributeSource) {
            this.attributeSource = attributeSource;
        }

        @Override
        public Graph with(Attributes<? extends F> attrs) {
            return (ImmutableGraph) attributeSource.apply(copyOfMut()).add(attrs);
        }

        @Override
        public Attributes<? super F> applyTo(MapAttributes<? super F> attrs) {
            return attributeSource.apply(ImmutableGraph.this).applyTo(attrs);
        }
    }

}
