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
package guru.nidi.graphviz;

import java.util.*;

/**
 *
 */
public class Graph implements Linkable, LinkTarget {
    final boolean strict;
    final boolean directed;
    final Label label;
    final Set<Node> nodes = new LinkedHashSet<>();
    final Set<Graph> subgraphs = new LinkedHashSet<>();
    final List<Link> links = new ArrayList<>();
    final SimpleAttributed<Graph> attributes = new SimpleAttributed<>(this);
    final SimpleAttributed<Graph> nodeAttributes = new SimpleAttributed<>(this);
    final SimpleAttributed<Graph> linkAttributes = new SimpleAttributed<>(this);
    final SimpleAttributed<Graph> graphAttributes = new SimpleAttributed<>(this);

    private Graph(boolean strict, boolean directed, Label label) {
        this.strict = strict;
        this.directed = directed;
        this.label = label;
        final CreationContext ctx = CreationContext.current();
        if (ctx != null) {
            general().attrs(ctx.graphs());
        }
    }

    public static Graph named(Label label) {
        return new Graph(false, false, label);
    }

    public static Graph named(String name) {
        return named(Label.of(name));
    }

    public static Graph nameless() {
        return named("");
    }

    public Graph strict() {
        return new Graph(true, directed, label);
    }

    public Graph directed() {
        return new Graph(strict, true, label);
    }

    public Graph with(Node... nodes) {
        for (final Node node : nodes) {
            with(node);
        }
        return this;
    }

    public Graph nodes(String... nodes) {
        for (final String node : nodes) {
            with(Node.named(node));
        }
        return this;
    }

    public Graph with(Node node) {
        nodes.add(node);
        return this;
    }

    public Graph with(Graph... subgraphs) {
        for (final Graph subgraph : subgraphs) {
            with(subgraph);
        }
        return this;
    }

    public Graph with(Graph subgraph) {
        subgraphs.add(subgraph);
        return this;
    }

    public Graph links(Link... links) {
        for (final Link link : links) {
            link(link);
        }
        return this;
    }

    public Graph link(Link link) {
        links.add(Link.between(this, link.to).attrs(link.attributes));
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

}
