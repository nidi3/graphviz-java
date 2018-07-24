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

import java.util.Iterator;
import java.util.Map.Entry;

public final class Edge implements Attributed<Edge>, EdgeSource {
    final Linkable from;
    final Linkable to;
    final Attributed<Edge> attributes;

//    public static Link to(Node node) {
//        return to(node);
//    }
//
//    public static Link to(Linkable to) {
//        return objBetween(null, to);
//    }
//
//    public Linkable to() {
//        return to;
//    }
//
//    public static Link between(Node from, Node to) {
//        return between(from.port(), to.port());
//    }
//
//    public static Link between(Linkable from, LinkTarget to) {
//        return objBetween(from, to);
//    }
//
//    private static Link objBetween(Linkable from, LinkTarget to) {
//        return new Link(from, to, Attributes.attrs());
//    }

    Edge(Linkable from, Linkable to) {
        this(from, to, Attributes.attrs());
    }

    Edge(Linkable from, Linkable to, Attributes attributes) {
        this.from = from;
        this.to = to;
        this.attributes = new SimpleAttributed<>(this, attributes);
    }

    @Override
    public Linkable linkable() {
        return to;
    }

    @Override
    public Iterator<Entry<String, Object>> iterator() {
        return attributes.iterator();
    }

    @Override
    public Edge with(Attributes attrs) {
        return new Edge(from, to, attrs.applyTo(attributes));
    }

    @Override
    public Object get(String key) {
        return attributes.get(key);
    }

    @Override
    public Attributes applyTo(MapAttributes attrs) {
        return attributes.applyTo(attrs);
    }

    public Edge linkTo() {
        return this;
    }

    public Linkable from() {
        return from;
    }

    public Attributed<Edge> attrs() {
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

        final Edge edge = (Edge) o;

        /*
        //including from could cause circular executions
//        if (from != null ? !from.equals(addLink.from) : addLink.from != null) {
//            return false;
//        }
//        if (to != null ? !to.equals(link.to) : link.to != null) {
//            return false;
//        }
        */
        return attributes.equals(edge.attributes);
    }

    @Override
    public int hashCode() {
        //including from could cause circular executions
        int result = 0;// from != null ? from.hashCode() : 0;
        //result = 31 * result + (to != null ? to.hashCode() : 0);
        result = 31 * result + attributes.hashCode();
        return result;
    }
}
