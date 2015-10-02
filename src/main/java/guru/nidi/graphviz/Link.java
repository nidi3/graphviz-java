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

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class Link {
    final NodePoint from;
    final NodePoint to;
    final Map<String, Object> attributes = new HashMap<>();

    private Link(NodePoint from, NodePoint to) {
        this.from = from;
        this.to = to;
    }

    public static Link to(NodePoint to) {
        return new Link(null, to);
    }

    public static Link to(Node to) {
        return to(NodePoint.of(to));
    }

    public static Link between(NodePoint from, NodePoint to) {
        return new Link(from, to);
    }

    public static Link between(Node from, Node to) {
        return between(NodePoint.of(from), NodePoint.of(to));
    }

    public Link attr(String name, Object value) {
        attributes.put(name, value);
        return this;
    }

    public Link attrs(Map<String, Object> attrs) {
        attributes.putAll(attrs);
        return this;
    }

}
