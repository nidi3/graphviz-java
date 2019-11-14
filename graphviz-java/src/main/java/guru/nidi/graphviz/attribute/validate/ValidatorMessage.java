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

import java.util.Objects;

public class ValidatorMessage {
    public enum Severity {
        ERROR, WARNING, INFO
    }

    public final Severity severity;
    public final String attribute;
    public final String message;
    public final int line;
    public final int column;

    ValidatorMessage(Severity severity, String message) {
        this(severity, "", message);
    }

    public ValidatorMessage(Severity severity, String attribute, String message) {
        this(severity, attribute, message, 0, 0);
    }

    public ValidatorMessage(Severity severity, String attribute, String message, int line, int column) {
        this.severity = severity;
        this.attribute = attribute;
        this.message = message;
        this.line = line;
        this.column = column;
    }

    public ValidatorMessage at(int line, int column) {
        return new ValidatorMessage(severity, attribute, message, line, column);
    }

    ValidatorMessage at(String attribute) {
        return new ValidatorMessage(severity, attribute, message, line, column);
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
                && message.equals(that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(severity, attribute, message, line, column);
    }

    @Override
    public String toString() {
        return "ValidatorMessage{"
                + line + ":" + column + " " + severity
                + ", attribute='" + attribute + '\''
                + ", message='" + message + '\''
                + '}';
    }
}
