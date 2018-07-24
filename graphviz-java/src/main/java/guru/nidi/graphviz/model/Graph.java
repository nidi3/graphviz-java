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

import java.util.*;

public class Graph implements EdgeContainer, Linkable, EdgeSource {
    boolean strict;
    boolean directed;
    boolean cluster;
    String name;
    final Map<String, Node> nodes;
    final Set<Graph> subgraphs;
    final List<Edge> edges;

    public Graph() {
        this("");
    }

    public Graph(String name) {
        this(false, false, false, name, new LinkedHashMap<>(), new LinkedHashSet<>(), new ArrayList<>());
    }

    Graph(boolean strict, boolean directed, boolean cluster, String name,
          LinkedHashMap<String, Node> nodes, LinkedHashSet<Graph> subgraphs, List<Edge> edges) {
        this.strict = strict;
        this.directed = directed;
        this.cluster = cluster;
        this.name = name;
        this.nodes = nodes;
        this.subgraphs = subgraphs;
        this.edges = edges;
    }

    public Graph copy() {
        return new Graph(strict, directed, cluster, name,
                new LinkedHashMap<>(nodes), new LinkedHashSet<>(subgraphs), new ArrayList<>(edges));
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
        return GraphContext.use(this, actions);
    }

    public Node node(Node node) {
        return nodes.merge(node.name, node, Node::merge);
    }

    public Node node(String name) {
        return node(new Node(name));
    }

    @Override
    public EdgeContainer containedBy() {
        return this;
    }

    @Override
    public Linkable linkable() {
        return this;
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

    public Collection<Node> nodes() {
        return nodes.values();
    }

    public Collection<Graph> graphs() {
        return subgraphs;
    }

    public Collection<Edge> edges() {
        return edges;
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
        if (!edges.equals(graph.edges)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = (strict ? 1 : 0);
        result = 31 * result + (directed ? 1 : 0);
        result = 31 * result + (cluster ? 1 : 0);
        result = 31 * result + name.hashCode();
        result = 31 * result + nodes.hashCode();
        result = 31 * result + subgraphs.hashCode();
        result = 31 * result + edges.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return new Serializer(this).serialize();
    }

}
