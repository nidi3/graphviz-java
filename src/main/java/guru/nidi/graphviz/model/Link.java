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
package guru.nidi.graphviz.model;

import guru.nidi.graphviz.attribute.Attributed;
import guru.nidi.graphviz.attribute.Attributes;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class Link implements Attributed<Link>,LinkSource {
    final LinkTarget from;
    final LinkTarget to;
    final Map<String, Object> attributes;

    public static Link to(Node to) {
        return to(NodePoint.of(to));
    }

    public static Link to(LinkTarget to) {
        return between(null, to);
    }

    public static Link between(Node from, Node to) {
        return between(NodePoint.of(from), NodePoint.of(to));
    }

    public static Link between(LinkTarget from, LinkTarget to) {
        final Link link = new Link(from, to, Collections.emptyMap());
        final CreationContext ctx = CreationContext.current();
        return ctx == null ? link : ctx.initLink(link);
    }

    private Link(LinkTarget from, LinkTarget to, Map<String, Object> attributes) {
        this.from = from;
        this.to = to;
        this.attributes = attributes;
    }

    public Link attr(String name, Object value) {
        final Map<String, Object> newAttrs = new HashMap<>(this.attributes);
        newAttrs.put(name, value);
        return new Link(from, to, newAttrs);
    }

    public Link attr(Map<String, Object> attrs) {
        final Map<String, Object> newAttrs = new HashMap<>(this.attributes);
        newAttrs.putAll(attrs);
        return new Link(from, to, newAttrs);
    }

    public Link attr(Object... keysAndValues) {
        return attr(Attributes.from(keysAndValues));
    }

    @Override
    public void apply(Map<String, Object> attrs) {
        attrs.putAll(attributes);
    }

    @Override
    public Link linkFrom() {
        return this;
    }
}
