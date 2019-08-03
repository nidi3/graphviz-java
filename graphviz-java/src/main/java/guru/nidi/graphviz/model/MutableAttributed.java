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

import guru.nidi.graphviz.attribute.Attributes;
import guru.nidi.graphviz.attribute.For;

import javax.annotation.Nullable;

import static guru.nidi.graphviz.attribute.Attributes.attr;
import static guru.nidi.graphviz.attribute.Attributes.attrs;

public interface MutableAttributed<T, F extends For> extends Attributes<F> {
    default T add(String name, @Nullable Object value) {
        return add(attr(name, value));
    }

    default T add(Attributes<? extends F> attr1, Attributes<? extends F> attr2) {
        return add(attrs(attr1, attr2));
    }

    default T add(Attributes<? extends F> attr1, Attributes<? extends F> attr2, Attributes<? extends F> attr3) {
        return add(attrs(attr1, attr2, attr3));
    }

    default T add(Attributes<? extends F> attr1, Attributes<? extends F> attr2,
                  Attributes<? extends F> attr3, Attributes<? extends F> attr4) {
        return add(attrs(attr1, attr2, attr3, attr4));
    }

    //cannot use @SafeVarargs here, that's why we have the specializations for 2..4 attrs
    default T add(Attributes<? extends F>... attributes) {
        return add(attrs(attributes));
    }

    T add(Attributes<? extends F> attributes);
}
