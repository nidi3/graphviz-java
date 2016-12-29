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
import guru.nidi.graphviz.attribute.MutableAttributed;

import java.util.*;
import java.util.function.Function;

class ImmutableGraph extends MutableGraph implements Graph {
    ImmutableGraph() {
    }

    ImmutableGraph(boolean strict, boolean directed, boolean cluster, Label label,
                   Set<MutableNode> nodes, Set<MutableGraph> subgraphs, List<Link> links,
                   MutableAttributed<MutableGraph> attributes,
                   MutableAttributed<MutableGraph> nodeAttributes,
                   MutableAttributed<MutableGraph> linkAttributes,
                   MutableAttributed<MutableGraph> graphAttributes) {
        super(strict, directed, cluster, label, nodes, subgraphs, links, attributes, nodeAttributes, linkAttributes, graphAttributes);
    }

    public ImmutableGraph copyOfMut() {
        return new ImmutableGraph(strict, directed, cluster, label,
                new LinkedHashSet<>(nodes), new LinkedHashSet<>(subgraphs), new ArrayList<>(links),
                generalAttrs, nodeAttrs, linkAttrs, graphAttrs);
    }

    public Graph strict() {
        return (ImmutableGraph) copyOfMut().setStrict();
    }

    public Graph directed() {
        return (ImmutableGraph) copyOfMut().setDirected();
    }

    public Graph cluster() {
        return (ImmutableGraph) copyOfMut().setCluster();
    }

    public Graph labeled(Label label) {
        return (ImmutableGraph) copyOfMut().setLabel(label);
    }

    public ImmutableGraph nodes(Node... nodes) {
        final ImmutableGraph copy = copyOfMut();
        for (final Node node : nodes) {
            copy.addNode((MutableNode) node);
        }
        return copy;
    }

    public ImmutableGraph nodes(String... nodes) {
        return (ImmutableGraph) copyOfMut().addNodes(nodes);
    }

    public ImmutableGraph graphs(Graph... subgraphs) {
        final ImmutableGraph copy = copyOfMut();
        for (final Graph subgraph : subgraphs) {
            copy.addGraph((MutableGraph) subgraph);
        }
        return copy;
    }

    public ImmutableGraph link(LinkTarget... targets) {
        return (ImmutableGraph) copyOfMut().addLink(targets);
    }

    public Attributed<Graph> nodes() {
        return new GraphAttributed(MutableGraph::getNodeAttrs);
    }

    public Attributed<Graph> links() {
        return new GraphAttributed(MutableGraph::getLinkAttrs);
    }

    public Attributed<Graph> graphs() {
        return new GraphAttributed(MutableGraph::getGraphAttrs);
    }

    public Attributed<Graph> general() {
        return new GraphAttributed(MutableGraph::getGeneralAttrs);
    }

    public Graph link(LinkTarget target) {
        return (ImmutableGraph) copyOfMut().addLink(target);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ImmutableGraph graph = (ImmutableGraph) o;

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
        if (!generalAttrs.equals(graph.generalAttrs)) {
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
        result = 31 * result + label.hashCode();
        result = 31 * result + nodes.hashCode();
        result = 31 * result + subgraphs.hashCode();
        result = 31 * result + links.hashCode();
        result = 31 * result + generalAttrs.hashCode();
        result = 31 * result + nodeAttrs.hashCode();
        result = 31 * result + linkAttrs.hashCode();
        result = 31 * result + graphAttrs.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return new Serializer(this).serialize();
    }

    private class GraphAttributed implements Attributed<Graph> {
        private final Function<ImmutableGraph, MutableAttributed<MutableGraph>> attributeSource;

        private GraphAttributed(Function<ImmutableGraph, MutableAttributed<MutableGraph>> attributeSource) {
            this.attributeSource = attributeSource;
        }

        @Override
        public Graph attr(Map<String, Object> attrs) {
            return (ImmutableGraph) attributeSource.apply(copyOfMut()).addAttr(attrs);
        }

        @Override
        public Map<String, Object> applyTo(Map<String, Object> attrs) {
            return attributeSource.apply(ImmutableGraph.this).applyTo(attrs);
        }
    }

}
