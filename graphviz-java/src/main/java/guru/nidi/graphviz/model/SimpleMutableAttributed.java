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

class SimpleMutableAttributed<T, F extends For> implements MutableAttributed<T, F> {
    private final T target;
    private final MapAttributes<F> attributes = new MapAttributes<>();

    public SimpleMutableAttributed(T target) {
        this.target = target;
    }

    public SimpleMutableAttributed(T target, @Nullable Attributes<? extends F> attributes) {
        this.target = target;
        if (attributes != null) {
            attributes.applyTo(this.attributes);
        }
    }

    @Override
    public Attributes<? super F> applyTo(MapAttributes<? super F> attrs) {
        return attrs.add(attributes);
    }

    @Override
    public Object get(String key) {
        return attributes.get(key);
    }

    @Override
    public T add(Attributes<? extends F> attributes) {
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
        final SimpleMutableAttributed<?, ?> that = (SimpleMutableAttributed<?, ?>) o;
        return Objects.equals(attributes, that.attributes);
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
