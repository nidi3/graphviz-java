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

import java.util.List;

import static java.util.Arrays.asList;

public interface Attributes<F extends For> {
    Attributes<? super F> applyTo(MapAttributes<? super F> attrs);

    default Attributes<? super F> applyTo(Attributes<? super F> attrs) {
        if (!(attrs instanceof MapAttributes)) {
            throw new UnsupportedOperationException("attributes must be a MapAttributes");
        }
        @SuppressWarnings("unchecked") final MapAttributes<? super F> as = (MapAttributes<? super F>) attrs;
        return applyTo(as);
    }

    default Attributes<F> copy() {
        @SuppressWarnings("unchecked") final Attributes<F> copy = (Attributes<F>) applyTo(attrs());
        return copy;
    }

    static <F extends For> Attributes<F> attr(String key, Object value) {
        return new MapAttributes<F>().add(key, value);
    }

    static <F extends For> Attributes<F> attrs() {
        return new MapAttributes<>();
    }

    @SafeVarargs
    static <F extends For> Attributes<F> attrs(Attributes<? extends F>... attributes) {
        return attrs(asList(attributes));
    }

    static <F extends For> Attributes<F> attrs(List<Attributes<? extends F>> attributes) {
        final MapAttributes<F> res = new MapAttributes<>();
        for (Attributes<? extends F> attribute : attributes) {
            attribute.applyTo(res);
        }
        return res;
    }

    default Object get(String key) {
        return applyTo(new MapAttributes<>()).get(key);
    }

    default boolean isEmpty() {
        return applyTo(new MapAttributes<>()).isEmpty();
    }
}
