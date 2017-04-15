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

import java.util.Iterator;
import java.util.Map;

public class SimpleMutableAttributed<E> implements MutableAttributed<E> {
    private final E target;
    final MapAttributes attributes = new MapAttributes();

    public SimpleMutableAttributed(E target) {
        this.target = target;
    }

    public SimpleMutableAttributed(E target, Attributes attributes) {
        this.target = target;
        if (attributes != null) {
            attributes.applyTo(this.attributes);
        }
    }

    @Override
    public Attributes applyTo(MapAttributes attrs) {
        return attrs.add(attributes);
    }

    @Override
    public Iterator<Map.Entry<String, Object>> iterator() {
        return attributes.iterator();
    }

    @Override
    public E add(Attributes attributes) {
        attributes.applyTo(this.attributes);
        return target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final SimpleMutableAttributed<?> that = (SimpleMutableAttributed<?>) o;

        return attributes.equals(that.attributes);

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
