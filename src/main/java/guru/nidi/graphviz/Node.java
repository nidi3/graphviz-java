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

import guru.nidi.graphviz.attribute.Attributes;

import java.util.*;

import static java.util.stream.Collectors.joining;

/**
 *
 */
public class Node implements Linkable, Attributed<Node>, LinkSource {
    final Label label;
    final List<Link> links;
    final Map<String, Object> attributes;

    Node(Label label) {
        this(label, Collections.emptyList(), Collections.emptyMap());
    }

    private Node(Label label, List<Link> links, Map<String, Object> attributes) {
        this.label = label;
        this.links = links;
        this.attributes = attributes;
    }

    public static Node named(Label label) {
        final CreationContext ctx = CreationContext.current();
        return ctx == null ? new Node(label) : ctx.getOrCreateNode(label);
    }

    public Node merged(Node n) {
        final List<Link> newLinks = new ArrayList<>(links);
        newLinks.addAll(n.links);
        final Map<String, Object> newAttrs = new HashMap<>(attributes);
        newAttrs.putAll(n.attributes);
        return new Node(label, newLinks, newAttrs);
    }

    public static Node named(String name) {
        return named(Label.of(name));
    }

    public NodePoint record(String record) {
        return NodePoint.of(this).record(record);
    }

    public NodePoint compass(Compass compass) {
        return NodePoint.of(this).compass(compass);
    }

    public Node link(LinkSource source) {
        final List<Link> newLinks = new ArrayList<>(this.links);
        final Link link = source.linkFrom();
        newLinks.add(Link.between(from(link), link.to).attr(link.attributes));
        return new Node(label, newLinks, attributes);
    }

    public Node link(LinkSource... sources) {
        final List<Link> newLinks = new ArrayList<>(this.links);
        for (final LinkSource source : sources) {
            final Link link = source.linkFrom();
            newLinks.add(Link.between(from(link), link.to).attr(link.attributes));
        }
        return new Node(label, newLinks, attributes);
    }

    public Node link(String node) {
        return link(Node.named(node));
    }

    public Node link(String... nodes) {
        final Node[] ns = new Node[nodes.length];
        for (int i = 0; i < ns.length; i++) {
            ns[i] = Node.named(nodes[i]);
        }
        return link(ns);
    }

    public Node attr(String name, Object value) {
        final Map<String, Object> newAttrs = new HashMap<>(this.attributes);
        newAttrs.put(name, value);
        return new Node(label, links, newAttrs);
    }

    public Node attr(Map<String, Object> attrs) {
        final Map<String, Object> newAttrs = new HashMap<>(this.attributes);
        newAttrs.putAll(attrs);
        return new Node(label, links, newAttrs);
    }

    public Node attr(Object... keysAndValues) {
        return attr(Attributes.from(keysAndValues));
    }

    @Override
    public void apply(Map<String, Object> attrs) {
        attrs.putAll(attributes);
    }

    private NodePoint from(Link link) {
        if (link.from instanceof NodePoint) {
            final NodePoint f = (NodePoint) link.from;
            return NodePoint.of(this).record(f.record).compass(f.compass);
        }
        return NodePoint.of(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Node node = (Node) o;

        if (label != null ? !label.equals(node.label) : node.label != null) {
            return false;
        }
        if (links != null ? !links.equals(node.links) : node.links != null) {
            return false;
        }
        return !(attributes != null ? !attributes.equals(node.attributes) : node.attributes != null);

    }

    @Override
    public int hashCode() {
        int result = label != null ? label.hashCode() : 0;
        result = 31 * result + (links != null ? links.hashCode() : 0);
        result = 31 * result + (attributes != null ? attributes.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return label + attributes.toString() + "->" +
                links.stream().map(l -> l.to.getName().toString()).collect(joining(","));
    }

    @Override
    public Collection<Link> getLinks() {
        return links;
    }

    @Override
    public Link linkFrom() {
        return Link.to(this);
    }
}
