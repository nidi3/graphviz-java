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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;

import static guru.nidi.graphviz.model.Factory.mutNode;
import static java.util.stream.Collectors.joining;

@Nonnull
public class MutableNode implements MutableAttributed<MutableNode>, LinkSource, LinkTarget {
    protected Label name;
    protected final LinkList links;
    protected final MutableAttributed<MutableNode> attributes;

    MutableNode(Label name) {
        this(name, new ArrayList<>(), Attributes.attrs());
    }

    protected MutableNode(Label name, List<Link> links, Attributes attributes) {
        this.links = new LinkList(this, links);
        this.attributes = new SimpleMutableAttributed<>(this, attributes);
        this.name = name; //satisfy code analyzers
        setName(name);
    }

    public MutableNode copy() {
        return new MutableNode(name, links, attributes.applyTo(Attributes.attrs()));
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

    public MutableNode merge(MutableNode n) {
        links.addAll(n.links);
        attributes.add(n.attributes);
        return this;
    }

    public MutablePortNode withRecord(@Nullable String record) {
        return new MutablePortNode().setNode(this).setRecord(record);
    }

    public MutablePortNode withCompass(Compass compass) {
        return new MutablePortNode().setNode(this).setCompass(compass);
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

    public MutableNode add(Attributes attrs) {
        attributes.add(attrs);
        return this;
    }

    @Override
    public void addTo(MutableGraph graph) {
        graph.nodes.add(this);
    }

    @Override
    public Iterator<Entry<String, Object>> iterator() {
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

    private Link adjustLink(Link link) {
        final MutablePortNode me = new MutablePortNode().setNode(this);
        if (link.from == null) {
            return Link.between(me, link.to).with(link.attributes);
        }
        if (link.from instanceof MutablePortNode) {
            final MutablePortNode f = (MutablePortNode) link.from;
            return f.node != null && f.node != this
                    ? Link.between(me, link.from.asLinkTarget())
                    : Link.between(me.setRecord(f.record()).setCompass(f.compass()), link.to).with(link.attributes);
        }
        if (link.from instanceof MutableNode) {
            return link.from != this
                    ? Link.between(me, link.from.asLinkTarget())
                    : Link.between(me, link.to).with(link.attributes);
        }
        throw new IllegalStateException("Unexpected element " + link.from + " in link");
    }

    @Nullable
    public Label name() {
        return name;
    }

    @Override
    public List<Link> links() {
        return links;
    }

    public MutableAttributed<MutableNode> attrs() {
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

        if (!name.equals(node.name)) {
            return false;
        }
        if (!links.equals(node.links)) {
            return false;
        }
        return attributes.equals(node.attributes);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + links.hashCode();
        result = 31 * result + attributes.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return name + attributes.toString() + "->"
                + links.stream().map(l -> l.to.toString()).collect(joining(","));
    }
}
