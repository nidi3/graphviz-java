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

import static guru.nidi.graphviz.model.Factory.mutNode;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;

public class MutableNode implements MutableAttributed<MutableNode, ForNode>, LinkSource, LinkTarget {
    private static final SafeRecursion<MutableNode> RECURSION = new SafeRecursion<>();
    protected Label name;
    protected final LinkList links;
    protected final MutableAttributed<MutableNode, ForNode> attributes;

    MutableNode(Label name) {
        this(name, new ArrayList<>(), Attributes.attrs());
    }

    protected MutableNode(Label name, List<Link> links, Attributes<? extends ForNode> attributes) {
        this.links = new LinkList(this, links);
        this.attributes = new SimpleMutableAttributed<>(this, attributes);
        this.name = name; //satisfy code analyzers
        setName(name);
    }

    public MutableNode copy() {
        return new MutableNode(name, links, attributes.copy());
    }

    public final MutableNode setName(Label name) {
        this.name = name;
        if (name.isExternal()) {
            this.name = Label.of("");
            attributes.add(name);
        } else if (name.isHtml()) {
            attributes.add(name);
        }
        return this;
    }

    public MutableNode setName(String name) {
        return setName(Label.of(name));
    }

    MutableNode merge(MutableNode n) {
        links.addAll(n.links);
        attributes.add(n.attributes);
        return this;
    }

    public PortNode port(@Nullable String record) {
        return port(record, null);
    }

    public PortNode port(@Nullable Compass compass) {
        return port(null, compass);
    }

    public PortNode port(@Nullable String record, @Nullable Compass compass) {
        return new ImmutablePortNode(this, new Port(record, compass));
    }

    @Override
    public Link linkTo(LinkTarget target) {
        return adjustLink(target.linkTo());
    }

    @Override
    public Link linkTo() {
        return Link.to(this);
    }

    public MutableNode addLink(LinkTarget target) {
        links.add(linkTo(target));
        return this;
    }

    public MutableNode addLink(LinkTarget... targets) {
        return addLink(asList(targets));
    }

    public MutableNode addLink(List<? extends LinkTarget> targets) {
        for (final LinkTarget target : targets) {
            addLink(target);
        }
        return this;
    }

    public MutableNode addLink(String node) {
        return addLink(mutNode(node));
    }

    public MutableNode addLink(String... nodes) {
        for (final String node : nodes) {
            addLink(node);
        }
        return this;
    }

    @Override
    public LinkTarget asLinkTarget() {
        return this;
    }

    @Override
    public LinkSource asLinkSource() {
        return this;
    }

    public MutableNode add(Attributes<? extends ForNode> attrs) {
        attributes.add(attrs);
        return this;
    }

    @Override
    public void addTo(MutableGraph graph) {
        graph.nodes.add(this);
    }

    @Override
    public Attributes<? super ForNode> applyTo(MapAttributes<? super ForNode> attrs) {
        return attributes.applyTo(attrs);
    }

    @Override
    public Object get(String key) {
        return attributes.get(key);
    }

    private Link adjustLink(Link link) {
        if (link.from == null) {
            return Link.between(this, link.to).with(link.attributes);
        }
        if (link.from instanceof ImmutablePortNode) {
            final ImmutablePortNode f = (ImmutablePortNode) link.from;
            return f.node().name.equals(name)
                    ? Link.between(new ImmutablePortNode(this, f.port()), link.to).with(link.attributes)
                    : Link.between(this, link.from.asLinkTarget());
        }
        if (link.from instanceof PortSource) {
            final PortSource f = (PortSource) link.from;
            return Link.between(new ImmutablePortNode(this, f.port), link.to).with(link.attributes);
        }
        if (link.from instanceof MutableNode) {
            return ((MutableNode) link.from).name.equals(name)
                    ? Link.between(this, link.to).with(link.attributes)
                    : Link.between(this, link.from.asLinkTarget());
        }
        throw new IllegalStateException("Unexpected element " + link.from + " in link");
    }

    public Label name() {
        return name;
    }

    @Override
    public List<Link> links() {
        return links;
    }

    public MutableAttributed<MutableNode, ForNode> attrs() {
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
        final MutableNode node = (MutableNode) o;
        return RECURSION.recurse(this, true, () -> Objects.equals(name, node.name)
                && Objects.equals(links, node.links)
                && Objects.equals(attributes, node.attributes));
    }

    @Override
    public int hashCode() {
        return RECURSION.recurse(this, 0, () -> Objects.hash(name, links, attributes));
    }

    @Override
    public String toString() {
        return name + attributes.toString() + "->"
                + links.stream().map(l -> l.to.toString()).collect(joining(","));
    }
}
