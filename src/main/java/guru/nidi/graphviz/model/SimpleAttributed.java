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

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class SimpleAttributed<E> implements Attributed<E> {
    private final E target;
    final Map<String, Object> attributes = new HashMap<>();

    public SimpleAttributed() {
        target = (E) this;
    }

    public SimpleAttributed(E target) {
        this.target = target;
    }

    public E attr(String name, Object value) {
        attributes.put(name, value);
        return target;
    }

    public E attr(Map<String, Object> attrs) {
        attributes.putAll(attrs);
        return target;
    }

    public E attr(Object... keysAndValues) {
        return attr(Attributes.from(keysAndValues));
    }

    @Override
    public void applyTo(Map<String, Object> attrs) {
        attrs.putAll(attributes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SimpleAttributed<?> that = (SimpleAttributed<?>) o;

        return attributes.equals(that.attributes);

    }

    @Override
    public int hashCode() {
        int result = attributes.hashCode();
        return result;
    }
}
