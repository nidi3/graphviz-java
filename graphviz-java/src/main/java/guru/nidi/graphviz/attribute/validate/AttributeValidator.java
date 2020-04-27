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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.StreamSupport;

import static guru.nidi.graphviz.attribute.validate.Datatype.*;
import static guru.nidi.graphviz.attribute.validate.ValidatorEngine.*;
import static guru.nidi.graphviz.attribute.validate.ValidatorFormat.OTHER;
import static guru.nidi.graphviz.attribute.validate.ValidatorFormat.UNKNOWN_FORMAT;
import static guru.nidi.graphviz.attribute.validate.ValidatorMessage.Severity.*;
import static java.util.Collections.singletonList;
import static java.util.Locale.ENGLISH;
import static java.util.stream.Collectors.toList;

public final class AttributeValidator {
    private static final Logger LOG = LoggerFactory.getLogger(AttributeValidator.class);

    public enum Scope {
        GRAPH, SUB_GRAPH, CLUSTER, NODE, EDGE;

        @Override
        public String toString() {
            return name().toLowerCase(ENGLISH).replace("_", "");
        }
    }

    private final ValidatorEngine engine;
    private final ValidatorFormat format;

    public AttributeValidator() {
        this(UNKNOWN_ENGINE, UNKNOWN_FORMAT);
    }

    private AttributeValidator(ValidatorEngine engine, ValidatorFormat format) {
        this.engine = engine;
        this.format = format;
    }

    public AttributeValidator forEngine(ValidatorEngine engine) {
        return new AttributeValidator(engine, format);
    }

    public AttributeValidator forFormat(ValidatorFormat format) {
        return new AttributeValidator(engine, format);
    }

    public List<ValidatorMessage> validate(Attributes<? extends For> attrs, Scope scope) {
        return StreamSupport.stream(attrs.spliterator(), false)
                .flatMap(entry -> validate(entry.getKey(), entry.getValue(), scope).stream())
                .collect(toList());
    }

    public List<ValidatorMessage> validate(String key, Object value, Scope scope) {
        final List<AttributeConfig> configs = AttributeConfigs.get(key);
        if (configs == null) {
            return singletonList(new ValidatorMessage(ERROR, key, "is unknown."));
        }

        final List<AttributeConfig> engineConfigs = findConfigsForEngine(configs);
        if (engineConfigs.isEmpty()) {
            return singletonList(new ValidatorMessage(
                    ERROR, key, "is not allowed for engine '" + engine + "'."));
        }

        final List<AttributeConfig> formatConfigs = findConfigsForFormat(configs);
        if (formatConfigs.isEmpty()) {
            return singletonList(new ValidatorMessage(
                    ERROR, key, "is not allowed for format '" + format + "'."));
        }

        List<AttributeConfig> matchConfigs = intersect(engineConfigs, formatConfigs);
        if (matchConfigs.isEmpty()) {
            return singletonList(new ValidatorMessage(
                    ERROR, key, "is not allowed for engine '" + engine + "' and format '" + format + "'."));
        }
        if (matchConfigs.size() > 1) {
            matchConfigs = matchConfigs.stream().filter(c -> c.scopes.contains(scope)).collect(toList());
        }
        if (matchConfigs.isEmpty()) {
            return singletonList(new ValidatorMessage(ERROR, key, "is not allowed for " + scope + "s."));
        }
        if (matchConfigs.size() > 1) {
            LOG.warn("Found multiple attribute configurations for " + engine + ", " + format + " and " + scope + "."
                    + " This should not happen.");
        }
        final List<ValidatorMessage> messages = new ArrayList<>();
        validateScope(messages, key, matchConfigs.get(0), scope);
        validateValue(messages, key, matchConfigs.get(0), value);
        validateType(messages, key, matchConfigs.get(0), value);
        return messages;
    }

    private static <T> List<T> intersect(List<T> as, List<T> bs) {
        return as.stream().filter(bs::contains).collect(toList());
    }

    private List<AttributeConfig> findConfigsForEngine(List<AttributeConfig> configs) {
        return configs.stream()
                .filter(c -> {
                    if (engine == UNKNOWN_ENGINE || c.engines.isEmpty()) {
                        return true;
                    }
                    if (c.engines.contains(NOT_DOT) && engine == DOT) {
                        return false;
                    }
                    return c.engines.contains(engine);
                })
                .collect(toList());
    }

    private List<AttributeConfig> findConfigsForFormat(List<AttributeConfig> configs) {
        return configs.stream()
                .filter(c -> {
                    if (format == OTHER && !c.formats.isEmpty()) {
                        return false;
                    }
                    if (format == UNKNOWN_FORMAT || c.formats.isEmpty()) {
                        return true;
                    }
                    return c.formats.contains(format);
                })
                .collect(toList());
    }

    private void validateScope(List<ValidatorMessage> messages, String key, AttributeConfig config, Scope scope) {
        if (!config.scopes.contains(scope)) {
            messages.add(new ValidatorMessage(ERROR, key, "is not allowed for " + scope + "s."));
        }
    }

    private void validateValue(List<ValidatorMessage> messages, String key, AttributeConfig config, Object value) {
        if (config.defVal != null && isValueEquals(config.defVal, value)) {
            messages.add(new ValidatorMessage(
                    INFO, key, "has its default value '" + config.defVal + "'."));
        }
        final Double val = tryParseDouble(value.toString());
        if (config.min != null && val != null && val < config.min) {
            messages.add(new ValidatorMessage(
                    WARN, key, "has the value '" + value + "' smaller than the minimum of '" + config.min + "'."));
        }
        if (config.max != null && val != null && val > config.max) {
            messages.add(new ValidatorMessage(
                    WARN, key, "has the value '" + value + "' greater than the maximum of '" + config.max + "'."));
        }
    }

    private void validateType(List<ValidatorMessage> messages, String key, AttributeConfig config, Object value) {
        final List<ValidatorMessage> typeMessages = config.types.stream().map(t -> t.validate(value)).collect(toList());
        if (typeMessages.size() == 1) {
            if (typeMessages.get(0) != null) {
                messages.add(typeMessages.get(0).attribute(key));
            }
        } else {
            if (typeMessages.stream().noneMatch(Objects::isNull)) {
                final List<String> lines = new ArrayList<>();
                for (int i = 0; i < config.types.size(); i++) {
                    lines.add("As " + config.types.get(i).name + " it " + typeMessages.get(i).message);
                }
                messages.add(new ValidatorMessage(ERROR, key, "has the value '" + value
                        + "' which is not valid for any of the possible types:\n" + String.join("\n", lines)));
            }
        }
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
