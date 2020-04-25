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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Consumer;

public class ValidatorMessage {
    private static final Logger LOG = LoggerFactory.getLogger(ValidatorMessage.class);

    public static final Consumer<ValidatorMessage> LINE_LOGGING_CONSUMER = msg -> LOG.info(
            String.format("%-5s at %d:%d: '%1.20s' -> %s",
                    msg.severity, msg.line, msg.column, msg.attribute, msg.message));
    public static final Consumer<ValidatorMessage> POSITION_LOGGING_CONSUMER = msg -> LOG.info(
            String.format("%-5s %1.20s: '%1.20s' -> %s",
                    msg.severity, msg.position, msg.attribute, msg.message));
    public static final Consumer<ValidatorMessage> NOP_CONSUMER = msg -> {
    };

    public enum Severity {
        ERROR, WARN, INFO
    }

    public final Severity severity;
    public final String attribute;
    public final String message;
    public final int line;
    public final int column;
    public final String position;

    ValidatorMessage(Severity severity, String message) {
        this(severity, "", message);
    }

    public ValidatorMessage(Severity severity, String attribute, String message) {
        this(severity, attribute, message, 0, 0, "");
    }

    public ValidatorMessage(Severity severity, String attribute, String message,
                            int line, int column, String position) {
        this.severity = severity;
        this.attribute = attribute;
        this.message = message;
        this.line = line;
        this.column = column;
        this.position = position;
    }

    public ValidatorMessage at(int line, int column) {
        return new ValidatorMessage(severity, attribute, message, line, column, position);
    }

    public ValidatorMessage at(String position) {
        return new ValidatorMessage(severity, attribute, message, line, column, position);
    }

    ValidatorMessage atAttribute(String attribute) {
        return new ValidatorMessage(severity, attribute, message, line, column, position);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ValidatorMessage that = (ValidatorMessage) o;
        return line == that.line
                && column == that.column
                && severity == that.severity
                && attribute.equals(that.attribute)
                && message.equals(that.message)
                && position.equals(that.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(severity, attribute, message, line, column, position);
    }

    @Override
    public String toString() {
        return severity + " " + position + (line > 0 ? line + ":" + column : "")
                + ": '" + attribute + "' -> " + message;
    }
}
