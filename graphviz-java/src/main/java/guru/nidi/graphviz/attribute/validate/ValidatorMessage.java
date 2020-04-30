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

import guru.nidi.graphviz.attribute.Named;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

import static java.util.Locale.ENGLISH;

public class ValidatorMessage {
    public final Severity severity;
    public final String attribute;
    public final String message;
    @Nullable
    public final Position position;
    @Nullable
    public final Location location;

    public static Consumer<ValidatorMessage> loggingConsumer(Logger logger) {
        return msg -> {
            final Location loc = msg.location;
            final Position pos = msg.position;
            final String where = loc != null ? String.format("%s '%1.30s'", loc.type.desc(), loc.name.name())
                    : pos != null ? String.format("%s:%s:%s", pos.name, pos.line, pos.column)
                    : "";
            logger.info(String.format("%-5s %s -> '%1.20s' %s", msg.severity, where, msg.attribute, msg.message));
        };
    }

    ValidatorMessage(Severity severity, String message) {
        this(severity, "", message);
    }

    public ValidatorMessage(Severity severity, String attribute, String message) {
        this(severity, attribute, message, null, null);
    }

    public ValidatorMessage(Severity severity, String attribute, String message,
                            @Nullable Position position, @Nullable Location location) {
        this.severity = severity;
        this.attribute = attribute;
        this.message = message;
        this.position = position;
        this.location = location;
    }

    public ValidatorMessage at(Position position) {
        return new ValidatorMessage(severity, attribute, message, position, location);
    }

    public ValidatorMessage at(Location location) {
        return new ValidatorMessage(severity, attribute, message, position, location);
    }

    ValidatorMessage attribute(String attribute) {
        return new ValidatorMessage(severity, attribute, message, position, location);
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
        return severity == that.severity
                && attribute.equals(that.attribute)
                && message.equals(that.message)
                && Objects.equals(position, that.position)
                && Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(severity, attribute, message, position, location);
    }

    @Override
    public String toString() {
        return severity + " "
                + (location == null ? "" : location)
                + (position == null ? "" : position)
                + ": '" + attribute + "' " + message;
    }

    public enum Severity {
        ERROR, WARN, INFO
    }

    public static class Position {
        public final String name;
        public final int line;
        public final int column;

        public Position(String name, int line, int column) {
            this.name = name;
            this.line = line;
            this.column = column;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final Position position = (Position) o;
            return line == position.line
                    && column == position.column
                    && name.equals(position.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, line, column);
        }

        @Override
        public String toString() {
            return "Position{"
                    + "name='" + name + '\''
                    + ", line=" + line
                    + ", column=" + column
                    + '}';
        }
    }

    public static class Location {
        public enum Type {
            NODE, LINK, GRAPH_ATTRS, NODE_ATTRS, LINK_ATTRS;

            public String desc() {
                final String s = super.toString().toLowerCase(ENGLISH);
                return s.replace("_", " ") + (s.contains("_") ? " of" : "");
            }
        }

        public final Type type;
        public final Named name;

        public Location(Type type, Named name) {
            this.type = type;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final Location location = (Location) o;
            return type == location.type
                    && name.equals(location.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, name);
        }

        @Override
        public String toString() {
            return "Location{"
                    + "type=" + type
                    + ", name=" + name.name()
                    + '}';
        }
    }
}
