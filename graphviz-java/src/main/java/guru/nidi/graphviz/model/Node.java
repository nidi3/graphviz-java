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

import static java.util.stream.Collectors.joining;

public class Node implements Linkable, Attributed<Node>, LinkTarget,
        LinkSource<Node> {
    protected final String name;
    protected final List<Link> links;
    protected final Attributed<Node> attributes;

    Node(String name) {
        this(name, new ArrayList<>(), Attributes.attrs());
    }

    Node(String name, List<Link> links, Attributes attributes) {
        this.name = name;
        this.links = links;
        this.attributes = new SimpleAttributed<>(this, attributes);
    }

    public Node copy() {
        return new Node(name, new ArrayList<>(links), attributes.applyTo(Attributes.attrs()));
    }

    public Node merge(Node n) {
        links.addAll(n.links);
        attributes.with(n.attributes);
        return this;
    }

    public Port port() {
        return new Port(this, null, null);
    }

    public Port port(String record) {
        return new Port(this, record, null);
    }

    public Port port(Compass compass) {
        return new Port(this, null, compass);
    }

    public Port port(String record, Compass compass) {
        return new Port(this, record, compass);
    }

    public Node addLink(LinkTarget target) {
        final Link link = target.linkTo();
        links.add(Link.between(from(link), link.to).with(link.attributes));
        return this;
    }

    public Node link(LinkTarget target) {
        final Link link = target.linkTo();
        links.add(Link.between(from(link), link.to).with(link.attributes));
        return this;
    }

    public Node link(LinkTarget... targets) {
        for (final LinkTarget target : targets) {
            link(target);
        }
        return this;
    }

    public Node link(String node) {
        return link(new Node(node));
    }

    public Node link(String... nodes) {
        for (final String node : nodes) {
            link(node);
        }
        return this;
    }

    public Node with(Attributes attrs) {
        attributes.with(attrs);
        return this;
    }

    @Override
    public Iterator<Map.Entry<String, Object>> iterator() {
        return attributes.iterator();
    }

    @Override
    public Attributes applyTo(MapAttributes attrs) {
        return attributes.applyTo(attrs);
    }

    @Override
    public Object get(String key) {
        return attributes.get(key);
    }

    private Port from(Link link) {
        if (link.from instanceof Port) {
            final Port f = (Port) link.from;
            return new Port().setNode(this).setRecord(f.record()).setCompass(f.compass());
        }
        return new Port().setNode(this);
    }

    public String name() {
        return name;
    }

    @Override
    public Collection<Link> links() {
        return links;
    }

    @Override
    public Link linkTo() {
        return Link.to(this);
    }

    public Attributed<Node> attrs() {
        return attributes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Node node = (Node) o;

        if (name != null ? !name.equals(node.name) : node.name != null) {
            return false;
        }
        if (links != null ? !links.equals(node.links) : node.links != null) {
            return false;
        }
        return !(attributes != null ? !attributes.equals(node.attributes) : node.attributes != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (links != null ? links.hashCode() : 0);
        result = 31 * result + (attributes != null ? attributes.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return name + attributes.toString() + "->"
                + links.stream().map(l -> l.to.toString()).collect(joining(","));
    }
}
