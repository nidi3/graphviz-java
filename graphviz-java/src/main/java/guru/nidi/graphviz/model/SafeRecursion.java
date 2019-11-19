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

import java.util.*;
import java.util.function.Supplier;

class SafeRecursion<T> {
    private final ThreadLocal<Set<T>> targetsHolder = new ThreadLocal<>();

    <R> R recurse(T target, R shortcutResult, Supplier<R> action) {
        Set<T> targets = targetsHolder.get();
        if (targets == null) {
            try {
                targets = Collections.newSetFromMap(new IdentityHashMap<>());
                targetsHolder.set(targets);
                targets.add(target);
                return action.get();
            } finally {
                targetsHolder.remove();
            }
        }
        if (targets.contains(target)) {
            return shortcutResult;
        }
        targets.add(target);
        return action.get();
    }
}
