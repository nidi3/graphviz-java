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

import guru.nidi.graphviz.attribute.Attribute;
import guru.nidi.graphviz.attribute.MutableAttributed;

import java.util.HashMap;
import java.util.Map;

public class SimpleMutableAttributed<E> implements MutableAttributed<E> {
    private final E target;
    final Map<String, Object> attributes = new HashMap<>();

    public SimpleMutableAttributed(E target) {
        this.target = target;
    }

    public SimpleMutableAttributed(E target, Attribute attribute) {
        this.target = target;
        if (attribute != null) {
            attribute.applyTo(attributes);
        }
    }

    @Override
    public E add(Map<String, Object> attrs) {
        attributes.putAll(attrs);
        return target;
    }

    @Override
    public Map<String, Object> applyTo(Map<String, Object> attrs) {
        attrs.putAll(attributes);
        return attrs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SimpleMutableAttributed<?> that = (SimpleMutableAttributed<?>) o;

        return attributes.equals(that.attributes);

    }

    @Override
    public int hashCode() {
        int result = attributes.hashCode();
        return result;
    }
}
