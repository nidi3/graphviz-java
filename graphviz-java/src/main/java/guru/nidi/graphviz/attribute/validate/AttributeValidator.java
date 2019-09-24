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


import guru.nidi.graphviz.attribute.Attributes;
import guru.nidi.graphviz.attribute.For;
import guru.nidi.graphviz.attribute.validate.AttributeConfig.Engine;
import guru.nidi.graphviz.attribute.validate.AttributeConfig.Format;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.StreamSupport;

import static guru.nidi.graphviz.attribute.validate.AttributeConfig.Engine.DOT;
import static guru.nidi.graphviz.attribute.validate.AttributeConfig.Engine.NOT_DOT;
import static guru.nidi.graphviz.attribute.validate.Datatype.*;
import static guru.nidi.graphviz.attribute.validate.ValidatorMessage.Severity.ERROR;
import static guru.nidi.graphviz.attribute.validate.ValidatorMessage.Severity.WARNING;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public final class AttributeValidator {
    public enum Scope {
        GRAPH, SUB_GRAPH, CLUSTER, NODE, EDGE
    }

    public List<ValidatorMessage> validate(Attributes<? extends For> attrs, Scope scope,
                                           @Nullable String engine, @Nullable String format) {
        return StreamSupport.stream(attrs.spliterator(), false)
                .flatMap(entry -> validate(entry.getKey(), entry.getValue(), scope, engine, format).stream())
                .collect(toList());
    }

    private List<ValidatorMessage> validate(String key, Object value, Scope scope,
                                            @Nullable String engine, @Nullable String format) {
        final List<AttributeConfig> configs = AttributeConfigs.get(key);
        if (configs == null) {
            return singletonList(new ValidatorMessage(ERROR, key, "Attribute is unknown."));
        }
        final AttributeConfig engineConfig = findConfigForEngine(engine, configs);
        if (engineConfig == null) {
            return singletonList(new ValidatorMessage(
                    ERROR, key, "Attribute is not allowed for engine '" + engine + "'."));
        }
        final AttributeConfig formatConfig = findConfigForFormat(format, configs);
        if (formatConfig == null) {
            return singletonList(new ValidatorMessage(
                    ERROR, key, "Attribute is not allowed for format '" + format + "'."));
        }
        if (!engineConfig.equals(formatConfig)) {
            return singletonList(new ValidatorMessage(
                    ERROR, key, "Attribute is not allowed for engine '" + engine + "' and format '" + format + "'."));
        }
        final List<ValidatorMessage> messages = validateNonType(key, value, scope, engineConfig);
        messages.addAll(validateType(key, value, engineConfig));
        return messages;
    }

    private AttributeConfig findConfigForEngine(@Nullable String engine, List<AttributeConfig> configs) {
        final Engine e = engine == null ? null : Engine.valueOf(engine.toUpperCase(Locale.ENGLISH));
        return configs.stream()
                .filter(c -> {
                    if (e == null || c.engines.isEmpty()) {
                        return true;
                    }
                    if (c.engines.contains(NOT_DOT) && e == DOT) {
                        return false;
                    }
                    return c.engines.contains(e);
                })
                .findFirst()
                .orElse(null);
    }

    private AttributeConfig findConfigForFormat(@Nullable String format, List<AttributeConfig> configs) {
        final Format f = format == null ? null : Format.valueOf(format.toUpperCase(Locale.ENGLISH));
        return configs.stream()
                .filter(c -> f == null || c.formats.isEmpty() || c.formats.contains(f))
                .findFirst()
                .orElse(null);
    }

    private List<ValidatorMessage> validateNonType(String key, Object value, Scope scope, AttributeConfig config) {
        final List<ValidatorMessage> messages = new ArrayList<>();
        if (!config.scopes.contains(scope)) {
            messages.add(new ValidatorMessage(ERROR, key, "Attribute is not allowed for scope '" + scope + "'."));
        }
        if (config.defVal != null && isValueEquals(config.defVal, value)) {
            messages.add(new ValidatorMessage(
                    WARNING, key, "Attribute is set to its default value '" + config.defVal + "'."));
        }
        final Double val = tryParseDouble(value.toString());
        if (config.min != null && val != null && val < config.min) {
            messages.add(new ValidatorMessage(
                    WARNING, key, "Attribute has a minimum of '" + config.min + "' but is set to '" + value + "'."));
        }
        return messages;
    }

    private List<ValidatorMessage> validateType(String key, Object value, AttributeConfig config) {
        final List<ValidatorMessage> typeMessages = config.types.stream().map(t -> t.validate(value)).collect(toList());
        if (typeMessages.size() == 1) {
            if (typeMessages.get(0) != null) {
                return singletonList(typeMessages.get(0).at(key));
            }
        } else {
            if (typeMessages.stream().noneMatch(Objects::isNull)) {
                return singletonList(new ValidatorMessage(
                        ERROR, key, "'" + value + "' is not valid for any of the types '"
                        + config.types.stream().map(t -> t.name).collect(joining(", ")) + "'."));
            }
        }
        return emptyList();
    }

    private boolean isValueEquals(Object config, Object value) {
        if (config instanceof Double) {
            final Double val = doubleValue(value);
            return val != null && Math.abs((Double) config - val) < .0001;
        }
        if (config instanceof Integer) {
            final Integer val = intValue(value);
            return val != null && val.equals(config);
        }
        if (config instanceof Boolean) {
            final Boolean val = boolValue(value);
            return val != null && val.equals(config);
        }
        return config.toString().equals(value.toString());
    }
}
