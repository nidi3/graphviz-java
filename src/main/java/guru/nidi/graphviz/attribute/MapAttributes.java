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
package guru.nidi.graphviz.attribute;

import java.util.*;

public class MapAttributes implements Attributes, Iterable<Map.Entry<String, Object>> {
    protected final Map<String, Object> attributes;

    MapAttributes() {
        attributes = new HashMap<>();
    }

    public Attributes applyTo(MapAttributes attrs) {
        attrs.attributes.putAll(attributes);
        return attrs;
    }

    public void add(String key, Object value) {
        attributes.put(key, value);
    }

    public MapAttributes add(MapAttributes attributes) {
        this.attributes.putAll(attributes.attributes);
        return this;
    }

    public boolean isEmpty() {
        return attributes.isEmpty();
    }

    @Override
    public Iterator<Map.Entry<String, Object>> iterator() {
        return attributes.entrySet().iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final MapAttributes entries = (MapAttributes) o;

        return attributes.equals(entries.attributes);
    }

    @Override
    public int hashCode() {
        return attributes.hashCode();
    }

    @Override
    public String toString() {
        return attributes.toString();
    }
}
