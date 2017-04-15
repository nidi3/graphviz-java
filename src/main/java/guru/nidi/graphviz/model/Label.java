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

import guru.nidi.graphviz.attribute.Attributes;
import guru.nidi.graphviz.attribute.MapAttributes;

public final class Label implements Attributes {
    final String value;
    final boolean html;

    private Label(String value, boolean html) {
        this.value = value;
        this.html = html;
    }

    public static Label of(String value) {
        return new Label(value, false);
    }

    public static Label html(String value) {
        return new Label(value, true);
    }

    public boolean isEmptyLabel() {
        return value.length() == 0;
    }

    public String serialized() {
        return html
                ? ("<" + value + ">")
                : ("\"" + value.replace("\"", "\\\"").replace("\n", "\\n") + "\"");
    }

    @Override
    public Attributes applyTo(MapAttributes attributes) {
        attributes.add("label", this);
        return attributes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Label label = (Label) o;

        return !(value != null ? !value.equals(label.value) : label.value != null);
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public String toString() {
        return value;
    }


}
