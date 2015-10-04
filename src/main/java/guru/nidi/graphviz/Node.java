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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.joining;

/**
 *
 */
public class Node extends Attributed<Node> implements Linkable {
    final Label label;
    final List<Link> links = new ArrayList<>();

    Node(Label label) {
        this.label = label;
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

        return !(label != null ? !label.equals(node.label) : node.label != null);
    }

    @Override
    public int hashCode() {
        return label != null ? label.hashCode() : 0;
    }

    @Override
    public String toString() {
        return label + attributes.toString() + "->" +
                links.stream().map(l -> l.to.name().toString()).collect(joining(","));
    }

    @Override
    public Collection<Link> links() {
        return links;
    }
}
