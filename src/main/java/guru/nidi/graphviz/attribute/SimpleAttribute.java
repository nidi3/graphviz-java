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
package guru.nidi.graphviz.attribute;

import java.util.Map;

/**
 *
 */
public class SimpleAttribute<T> implements Attribute {
    protected final String key;
    protected final T value;

    protected SimpleAttribute(String key, T value) {
        this.key = key;
        this.value = value;
    }

    public SimpleAttribute<T> key(String key) {
        return new SimpleAttribute<>(key, value);
    }

    @Override
    public void apply(Map<String, Object> attrs) {
        attrs.put(key, value);
    }
}
