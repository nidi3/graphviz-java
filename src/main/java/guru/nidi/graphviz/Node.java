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

import guru.nidi.graphviz.attribute.Attribute;
import guru.nidi.graphviz.attribute.Attributes;

import java.util.*;

import static java.util.stream.Collectors.joining;

/**
 *
 */
public class Node implements Linkable, Attributed<Node> {
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

    public static Node named(String name) {
        return named(Label.of(name));
    }

    public NodePoint record(String record) {
        return NodePoint.of(this).record(record);
    }

    public NodePoint compass(Compass compass) {
        return NodePoint.of(this).compass(compass);
    }

    public Node links(Link... links) {
        final List<Link> newLinks = new ArrayList<>(this.links);
        for (final Link link : links) {
            newLinks.add(Link.between(from(link), link.to).attrs(link.attributes));
        }
        return new Node(label, newLinks, attributes);
    }

    public Node link(Link link) {
        final List<Link> newLinks = new ArrayList<>(this.links);
        newLinks.add(Link.between(from(link), link.to).attrs(link.attributes));
        return new Node(label, newLinks, attributes);
    }

    public Node attr(String name, Object value) {
        final Map<String, Object> newAttrs = new HashMap<>(this.attributes);
        newAttrs.put(name, value);
        return new Node(label, links, newAttrs);
    }

    public Node attrs(Map<String, Object> attrs) {
        final Map<String, Object> newAttrs = new HashMap<>(this.attributes);
        newAttrs.putAll(attrs);
        return new Node(label, links, newAttrs);
    }

    public Node attrs(Object... keysAndValues) {
        return attrs(Attributes.from(keysAndValues));
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

        return !(label != null ? !label.equals(node.label) : node.label != null);
    }

    @Override
    public int hashCode() {
        return label != null ? label.hashCode() : 0;
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
}
