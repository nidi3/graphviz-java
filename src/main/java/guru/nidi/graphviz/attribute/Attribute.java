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

import java.util.HashMap;
import java.util.Map;

public interface Attribute {
    Map<String, Object> applyTo(Map<String, Object> attrs);

    static Map<String, Object> from(Object... keysAndValues) {
        final Map<String, Object> res = new HashMap<>();
        for (int i = 0; i < keysAndValues.length; i++) {
            if (keysAndValues[i] instanceof Attribute) {
                ((Attribute) keysAndValues[i]).applyTo(res);
            } else if (keysAndValues[i] instanceof Map) {
                res.putAll((Map<String, Object>) keysAndValues[i]);
            } else if (!(keysAndValues[i] instanceof String)) {
                throw new IllegalArgumentException(i + "th argument '" + keysAndValues[i] + "' is a key, but not a string");
            } else {
                if (i == keysAndValues.length - 1) {
                    throw new IllegalArgumentException("Last key '" + keysAndValues[i] + "' has no value");
                }
                res.put((String) keysAndValues[i], keysAndValues[i + 1]);
                i++;
            }
        }
        return res;
    }
}
