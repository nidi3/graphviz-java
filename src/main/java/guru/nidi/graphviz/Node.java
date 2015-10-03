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
public class Node implements Linkable {
    final Name name;
    final Map<String, Object> attributes = new HashMap<>();
    final List<Link> links = new ArrayList<>();

    Node(Name name) {
        this.name = name;
    }

    public static Node named(Name name) {
        return NodeContext.getOrCreateNode(name);
    }

    public static Node named(String name) {
        return named(Name.of(name));
    }

    public Node attr(String name, Object value) {
        attributes.put(name, value);
        return this;
    }

    public Node attrs(Map<String, Object> attrs) {
        attributes.putAll(attrs);
        return this;
    }

    public Node attrs(Object... keysAndValues) {
        return attrs(Attributes.from(keysAndValues));
    }

    public NodePoint record(String record) {
        return NodePoint.of(this).record(record);
    }

    public NodePoint compass(Compass compass) {
        return NodePoint.of(this).compass(compass);
    }

    public Node links(Link... links) {
        for (final Link link : links) {
            link(link);
        }
        return this;
    }

    public Node link(Link link) {
        final NodePoint from;
        if (link.from instanceof NodePoint) {
            final NodePoint f = (NodePoint) link.from;
            from = NodePoint.of(this).record(f.record).compass(f.compass);
        } else {
            from = NodePoint.of(this);
        }
        links.add(Link.between(from, link.to).attrs(link.attributes));
        return this;
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

        return !(name != null ? !name.equals(node.name) : node.name != null);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return name + attributes.toString() + "->" +
                links.stream().map(l -> l.to.name().toString()).collect(joining(","));
    }

    @Override
    public Collection<Link> links() {
        return links;
    }
}
