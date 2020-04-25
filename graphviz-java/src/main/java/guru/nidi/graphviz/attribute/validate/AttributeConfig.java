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
package guru.nidi.graphviz.attribute.validate;

import guru.nidi.graphviz.attribute.validate.AttributeValidator.Scope;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

import static guru.nidi.graphviz.attribute.validate.AttributeValidator.Scope.*;
import static java.util.Collections.singletonList;

final class AttributeConfig {
    final EnumSet<Scope> scopes;
    final List<Datatype> types;
    @Nullable
    final Object defVal;
    @Nullable
    final Double min;
    @Nullable
    final Double max;
    final EnumSet<ValidatorEngine> engines;
    final EnumSet<ValidatorFormat> formats;

    private AttributeConfig(EnumSet<Scope> scopes, List<Datatype> types, @Nullable Object defVal, @Nullable Double min,
                            @Nullable Double max, EnumSet<ValidatorEngine> engines, EnumSet<ValidatorFormat> formats) {
        this.scopes = scopes;
        this.types = types;
        this.defVal = defVal;
        this.min = min;
        this.max = max;
        this.engines = engines;
        this.formats = formats;
    }

    static AttributeConfig entry(String scopes, Datatype type) {
        return entry(scopes, type, null);
    }

    static AttributeConfig entry(String scopes, List<Datatype> types) {
        return entry(scopes, types, null);
    }

    static AttributeConfig entry(String scopes, Datatype type, @Nullable Object defVal) {
        return entry(scopes, type, defVal, null);
    }

    static AttributeConfig entry(String scopes, List<Datatype> types, @Nullable Object defVal) {
        return entry(scopes, types, defVal, null);
    }

    static AttributeConfig entry(String scopes, Datatype type, @Nullable Object defVal, @Nullable Double min) {
        return entry(scopes, singletonList(type), defVal, min);
    }

    static AttributeConfig entry(String scopes, Datatype type, @Nullable Object defVal,
                                 @Nullable Double min, @Nullable Double max) {
        return entry(scopes, singletonList(type), defVal, min, max);
    }

    static AttributeConfig entry(String scopes, List<Datatype> types, @Nullable Object defVal, @Nullable Double min) {
        return entry(scopes, types, defVal, min, null);
    }

    static AttributeConfig entry(String scopes, List<Datatype> types, @Nullable Object defVal,
                                 @Nullable Double min, @Nullable Double max) {
        return new AttributeConfig(scopesOf(scopes), types, defVal, min,
                max, EnumSet.noneOf(ValidatorEngine.class), EnumSet.noneOf(ValidatorFormat.class));
    }

    AttributeConfig engines(ValidatorEngine... engines) {
        return new AttributeConfig(scopes, types, defVal, min, max, EnumSet.of(engines[0], engines), formats);
    }

    AttributeConfig formats(ValidatorFormat... formats) {
        return new AttributeConfig(scopes, types, defVal, min, max, engines, EnumSet.of(formats[0], formats));
    }

    private static EnumSet<Scope> scopesOf(String scopes) {
        final EnumSet<Scope> res = EnumSet.noneOf(Scope.class);
        for (int i = 0; i < scopes.length(); i++) {
            switch (scopes.charAt(i)) {
                case 'G':
                    res.add(GRAPH);
                    break;
                case 'S':
                    res.add(SUB_GRAPH);
                    break;
                case 'C':
                    res.add(CLUSTER);
                    break;
                case 'N':
                    res.add(NODE);
                    break;
                case 'E':
                    res.add(EDGE);
                    break;
                default:
                    throw new IllegalArgumentException("unknown scope '" + scopes.charAt(i) + "'.");
            }
        }
        return res;
    }

}
