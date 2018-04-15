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

public class MutableNode implements Linkable, MutableAttributed<MutableNode>, LinkTarget,
        MutableLinkSource<MutableNode> {
    protected Label name;
    protected final List<Link> links;
    protected final MutableAttributed<MutableNode> attributes;

    MutableNode() {
        this(null, new ArrayList<>(), Attributes.attrs());
    }

    protected MutableNode(Label name, List<Link> links, Attributes attributes) {
        this.links = links;
        this.attributes = new SimpleMutableAttributed<>(this, attributes);
        setName(name);
    }

    public MutableNode copy() {
        return new MutableNode(name, new ArrayList<>(links), attributes.applyTo(Attributes.attrs()));
    }

    public final MutableNode setName(Label name) {
        this.name = name;
        if (name != null) {
            if (name.isExternal()) {
                this.name = Label.of("");
                attributes.add(name);
            } else if (name.isHtml()) {
                attributes.add(name);
            }
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

    public MutablePortNode withRecord(String record) {
        return new MutablePortNode().setNode(this).setRecord(record);
    }

    public MutablePortNode withCompass(Compass compass) {
        return new MutablePortNode().setNode(this).setCompass(compass);
    }

    public MutableNode addLink(LinkTarget target) {
        final Link link = target.linkTo();
        links.add(Link.between(from(link), link.to).with(link.attributes));
        return this;
    }

    public MutableNode addLink(LinkTarget... targets) {
        for (final LinkTarget target : targets) {
            addLink(target);
        }
        return this;
    }

    public MutableNode addLink(String node) {
        return addLink(new MutableNode().setName(node));
    }

    public MutableNode addLink(String... nodes) {
        for (final String node : nodes) {
            addLink(node);
        }
        return this;
    }

    public MutableNode add(Attributes attrs) {
        attributes.add(attrs);
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

    private MutablePortNode from(Link link) {
        if (link.from instanceof MutablePortNode) {
            final MutablePortNode f = (MutablePortNode) link.from;
            return new MutablePortNode().setNode(this).setRecord(f.record()).setCompass(f.compass());
        }
        return new MutablePortNode().setNode(this);
    }

    public Label name() {
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
