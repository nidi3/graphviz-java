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
    enum Engine {
        CIRCO, NOT_DOT, DOT, NEATO, OSAGE, TWOPI, FDP, SFDP, PATCHWORK
    }

    enum Format {
        WRITE, SVG, BITMAP, MAP, CMAP, POSTSCRIPT, XDOT
    }

    final List<Datatype> types;
    @Nullable
    final Object defVal;
    @Nullable
    final Double min;
    final EnumSet<Engine> engines;
    final EnumSet<Format> formats;
    final EnumSet<Scope> scopes;

    private AttributeConfig(@Nullable EnumSet<Scope> scopes, List<Datatype> types, @Nullable Object defVal,
                            @Nullable Double min, @Nullable EnumSet<Engine> engines, 
                            @Nullable EnumSet<Format> formats) {
        this.scopes = scopes == null ? EnumSet.noneOf(Scope.class) : scopes;
        this.types = types;
        this.defVal = defVal;
        this.min = min;
        this.engines = engines == null ? EnumSet.noneOf(Engine.class) : engines;
        this.formats = formats == null ? EnumSet.noneOf(Format.class) : formats;
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

    static AttributeConfig entry(String scopes, List<Datatype> types, @Nullable Object defVal, @Nullable Double min) {
        return new AttributeConfig(scopesOf(scopes), types, defVal, min, null, null);
    }

    AttributeConfig engines(Engine... engines) {
        return new AttributeConfig(scopes, types, defVal, min, EnumSet.of(engines[0], engines), formats);
    }

    AttributeConfig formats(Format... formats) {
        return new AttributeConfig(scopes, types, defVal, min, engines, EnumSet.of(formats[0], formats));
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
