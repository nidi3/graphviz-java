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
import java.util.Objects;

public final class Link implements Attributed<Link, ForLink>, LinkTarget {
    @Nullable
    final LinkSource from;
    final LinkTarget to;
    final MutableAttributed<Link, ForLink> attributes;

    public static Link to(MutableNode node) {
        return to(node.port((String) null));
    }

    public static Link to(Node node) {
        return to(node.port((String) null));
    }

    public static Link to(LinkTarget to) {
        return between((LinkSource) null, to);
    }

    public LinkTarget to() {
        return to;
    }

    public static Link between(Port port, LinkTarget to) {
        return between(new PortSource(port), to);
    }

    static Link between(@Nullable LinkSource from, LinkTarget to) {
        return CreationContext.createLink(from, to);
    }

    Link(@Nullable LinkSource from, LinkTarget to, Attributes<? extends ForLink> attributes) {
        this.from = from;
        this.to = to;
        this.attributes = new SimpleMutableAttributed<>(this, attributes);
    }

    public Link add(Attributes<? extends ForLink> attrs) {
        attributes.add(attrs);
        return this;
    }

    public Link with(Attributes<? extends ForLink> attrs) {
        @SuppressWarnings("unchecked") //
        final Attributes<? extends ForLink> as = (Attributes) attrs.applyTo(attributes.copy());
        return new Link(from, to, as);
    }

    @Override
    public Label name() {
        return Label.of((from == null ? "" : from.name()) + "--" + to.name());
    }

    @Override
    public Attributes<? super ForLink> applyTo(MapAttributes<? super ForLink> attrs) {
        return attributes.applyTo(attrs);
    }

    @Override
    public Link linkTo() {
        return this;
    }

    public LinkTarget asLinkTarget() {
        return to(from.asLinkTarget());
    }

    @Override
    public LinkSource asLinkSource() {
        return to.asLinkSource();
    }

    @Nullable
    public LinkSource from() {
        return from;
    }

    //TODO differentiate between mutable and immutable
    public MutableAttributed<Link, ForLink> attrs() {
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
        final Link entries = (Link) o;
        return Objects.equals(from, entries.from)
                && to.equals(entries.to)
                && attributes.equals(entries.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, attributes);
    }
}
