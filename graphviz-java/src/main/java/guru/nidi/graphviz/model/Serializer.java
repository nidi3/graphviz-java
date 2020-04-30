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

import guru.nidi.graphviz.attribute.validate.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.function.Consumer;

import static guru.nidi.graphviz.attribute.validate.ValidatorEngine.UNKNOWN_ENGINE;
import static guru.nidi.graphviz.attribute.validate.ValidatorFormat.UNKNOWN_FORMAT;
import static guru.nidi.graphviz.attribute.validate.ValidatorMessage.loggingConsumer;

public final class Serializer {
    private static final Logger LOG = LoggerFactory.getLogger(Serializer.class);

    private final ValidatorEngine engine;
    private final ValidatorFormat format;
    @Nullable
    private final Consumer<ValidatorMessage> messageConsumer;

    public Serializer() {
        this(UNKNOWN_ENGINE, UNKNOWN_FORMAT, loggingConsumer(LOG));
    }

    private Serializer(ValidatorEngine engine, ValidatorFormat format,
                       @Nullable Consumer<ValidatorMessage> messageConsumer) {
        this.engine = engine;
        this.format = format;
        this.messageConsumer = messageConsumer;
    }

    public Serializer forEngine(ValidatorEngine engine) {
        return new Serializer(engine, format, messageConsumer);
    }

    public Serializer forFormat(ValidatorFormat format) {
        return new Serializer(engine, format, messageConsumer);
    }

    public Serializer notValidating() {
        return new Serializer(engine, format, null);
    }

    public Serializer validating(Consumer<ValidatorMessage> messageConsumer) {
        return new Serializer(engine, format, messageConsumer);
    }

    public String serialize(Graph graph) {
        return serialize((MutableGraph) graph);
    }

    public String serialize(MutableGraph graph) {
        final AttributeValidator validator = new AttributeValidator().forEngine(engine).forFormat(format);
        return new SerializerImpl(graph, validator, messageConsumer).serialize();
    }
}
