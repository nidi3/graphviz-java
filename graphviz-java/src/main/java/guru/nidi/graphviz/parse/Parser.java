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
package guru.nidi.graphviz.parse;

import guru.nidi.graphviz.attribute.validate.*;
import guru.nidi.graphviz.model.MutableGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import static guru.nidi.graphviz.attribute.validate.ValidatorEngine.UNKNOWN_ENGINE;
import static guru.nidi.graphviz.attribute.validate.ValidatorFormat.UNKNOWN_FORMAT;
import static guru.nidi.graphviz.attribute.validate.ValidatorMessage.loggingConsumer;

public final class Parser {
    private static final Logger LOG = LoggerFactory.getLogger(Parser.class);

    private final ValidatorEngine engine;
    private final ValidatorFormat format;
    @Nullable
    private final Consumer<ValidatorMessage> messageConsumer;

    public Parser() {
        this(UNKNOWN_ENGINE, UNKNOWN_FORMAT, loggingConsumer(LOG));
    }

    private Parser(ValidatorEngine engine, ValidatorFormat format,
                   @Nullable Consumer<ValidatorMessage> messageConsumer) {
        this.engine = engine;
        this.format = format;
        this.messageConsumer = messageConsumer;
    }

    public Parser forEngine(ValidatorEngine engine) {
        return new Parser(engine, format, messageConsumer);
    }

    public Parser forFormat(ValidatorFormat format) {
        return new Parser(engine, format, messageConsumer);
    }

    public Parser notValidating() {
        return new Parser(engine, format, null);
    }

    public Parser validating(Consumer<ValidatorMessage> messageConsumer) {
        return new Parser(engine, format, messageConsumer);
    }

    public MutableGraph read(File file) throws IOException {
        return read(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8), file.getName());
    }

    public MutableGraph read(InputStream is) throws IOException {
        return read(new InputStreamReader(is, StandardCharsets.UTF_8), "<input stream>");
    }

    public MutableGraph read(String dot) throws IOException {
        return read(new StringReader(dot), "<string>");
    }

    public MutableGraph read(Reader dot, String name) throws IOException {
        final AttributeValidator validator = new AttributeValidator().forEngine(engine).forFormat(format);
        return new ParserImpl(new Lexer(dot, name), validator, messageConsumer).parse();
    }
}
