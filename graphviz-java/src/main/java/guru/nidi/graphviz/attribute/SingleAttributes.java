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

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;

public class SingleAttributes<T, F extends For> implements Attributes<F> {
    protected final String key;
    public final T value;

    protected SingleAttributes(String key, T value) {
        this.key = key;
        this.value = value;
    }

    protected <E> E key(String key) {
        return newInstance(key, value);
    }

    protected <E> E value(T value) {
        return newInstance(key, value);
    }

    private <E> E newInstance(String key, T value) {
        try {
            final ParameterizedType superclass = (ParameterizedType) getClass().getGenericSuperclass();
            final Class<?> type = (Class<?>) superclass.getActualTypeArguments()[0];
            final Constructor<? extends SingleAttributes> cons = getClass().getDeclaredConstructor(String.class, type);
            cons.setAccessible(true);
            return (E) cons.newInstance(key, value);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public Attributes<? super F> applyTo(MapAttributes<? super F> attrs) {
        return attrs.add(key, value);
    }
}
