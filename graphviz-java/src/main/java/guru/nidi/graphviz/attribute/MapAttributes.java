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

import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;

public class MapAttributes<F extends For> implements Attributes<F>, Iterable<Entry<String, Object>> {
    protected final Map<String, Object> attributes;

    MapAttributes(String key, @Nullable Object value) {
        this();
        attributes.put(key, value);
    }

    public MapAttributes() {
        attributes = new LinkedHashMap<>();
    }

    public Attributes<? super F> applyTo(MapAttributes<? super F> attrs) {
        attrs.attributes.putAll(attributes);
        return attrs;
    }

    public <G extends For> MapAttributes<G> add(String key, @Nullable Object value) {
        if (value == null) {
            attributes.remove(key);
        } else {
            attributes.put(key, value);
        }
        @SuppressWarnings("unchecked") final MapAttributes<G> map = (MapAttributes<G>) this;
        return map;
    }

    public MapAttributes<F> add(MapAttributes<? extends F> attributes) {
        this.attributes.putAll(attributes.attributes);
        return this;
    }

    public boolean isEmpty() {
        return attributes.isEmpty();
    }

    @Override
    public Object get(String key) {
        return attributes.get(key);
    }

    @Override
    public Iterator<Entry<String, Object>> iterator() {
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
        return Objects.equals(attributes, entries.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attributes);
    }

    @Override
    public String toString() {
        return attributes.toString();
    }
}
