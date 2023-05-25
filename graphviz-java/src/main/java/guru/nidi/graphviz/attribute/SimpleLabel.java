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
package guru.nidi.graphviz.attribute;

import java.util.Objects;

public class SimpleLabel {
    final String value;
    final boolean html;

    SimpleLabel(String value, boolean html) {
        this.value = value;
        this.html = html;
    }

    public static SimpleLabel of(String value) {
        return new SimpleLabel(value, false);
    }

    public static SimpleLabel of(Object value) {
        return value instanceof SimpleLabel ? (SimpleLabel) value : of(value.toString());
    }

    /**
     * Returns the value stored in this label after serializing.
     * 
     * @return the value stored in this label, after serialization
     */
    public String serialized() {
        return html ? ("<" + value + ">") : ("\"" + quoted() + "\"");
    }

    /**
     * Returns the value stored in this label after serializing, but without delimiters.
     * 
     * @return the value stored in this label, after serialization
     */
    public String simpleSerialized() {
        return html ? value : quoted();
    }

    private String quoted() {
        //the ending " must not accidentally be escaped by an odd number of \
        int endSlashes = 0;
        while (endSlashes < value.length() && value.charAt(value.length() - 1 - endSlashes) == '\\') {
            endSlashes++;
        }
        final String end = endSlashes % 2 == 1 ? "\\" : "";
        //TODO check if works for cmdline engine too
        return value.replace("\"", "\\\"").replaceAll("\\R", "\\\\n") + end;
    }

    public String value() {
        return value;
    }

    /**
     * Determines whether a string is equal to the value stored in this label
     * 
     * @param s a string
     * @return {@code true} if the value stored in this label is equal to the argument
     */
    public boolean contentEquals(String s) {
        return value.equals(s);
    }

    /**
     * Determines whether the content of this label is empty.
     * 
     * @return {@code true} if the value stored in this label has length of 0.
     */
    public boolean isContentEmpty() {
        return value.isEmpty();
    }

    /**
     * Determines whether this label represents an HTML string.
     * 
     * @return {@code true} if this label represents an HTML string
     */
    public boolean isHtml() {
        return html;
    }

    /**
     * Determines whether an object is "equal" to this label.
     * 
     * @param o the reference object with which to compare.
     * @return {@code true} if this label is the same as the {@code o} argument; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SimpleLabel that = (SimpleLabel) o;
        return html == that.html
                && Objects.equals(value, that.value);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(value, html);
    }

    /**
     * Returns the string representation of this label.
     * @return the string representation of this label
     */
    @Override
    public String toString() {
        return value;
    }
}
