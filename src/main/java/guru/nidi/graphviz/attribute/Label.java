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

import static guru.nidi.graphviz.attribute.Label.Justification.LEFT;
import static guru.nidi.graphviz.attribute.Label.Justification.RIGHT;
import static guru.nidi.graphviz.attribute.Label.Location.BOTTOM;
import static guru.nidi.graphviz.attribute.Label.Location.TOP;

public final class Label extends SimpleLabel implements Attributes {
    public enum Justification {
        LEFT, MIDDLE, RIGHT
    }

    public enum Location {
        TOP, CENTER, BOTTOM
    }

    private final boolean external;
    private final boolean floating;
    private final boolean decorated;
    private final Justification just;
    private final Location loc;

    private Label(String value, boolean html, boolean external, boolean floating, boolean decorated,
                  Justification just, Location loc) {
        super(value, html);
        this.external = external;
        this.floating = floating;
        this.decorated = decorated;
        this.just = just;
        this.loc = loc;
    }

    public static Label of(String value) {
        return new Label(value, false, false, false, false, null, null);
    }

    public static Label html(String value) {
        return new Label(value, true, false, false, false, null, null);
    }

    public EndLabel head() {
        return EndLabel.head(this, null, null);
    }

    public EndLabel head(double angle, double distance) {
        return EndLabel.head(this, angle, distance);
    }

    public EndLabel tail() {
        return EndLabel.tail(this, null, null);
    }

    public EndLabel tail(double angle, double distance) {
        return EndLabel.tail(this, angle, distance);
    }

    public Label external() {
        return new Label(value, html, true, floating, decorated, just, loc);
    }

    public Label floating() {
        return new Label(value, html, external, true, decorated, just, loc);
    }

    public Label decorated() {
        return new Label(value, html, external, floating, true, just, loc);
    }

    public Label justify(Justification just) {
        return new Label(value, html, external, floating, decorated, just, loc);
    }

    public Label locate(Location loc) {
        return new Label(value, html, external, floating, decorated, just, loc);
    }

    public boolean isExternal() {
        return external;
    }

    @Override
    public Attributes applyTo(MapAttributes attributes) {
        attributes.add(external ? "xlabel" : "label", this);
        if (floating) {
            attributes.add("labelfloat", true);
        }
        if (decorated) {
            attributes.add("decorate", true);
        }
        if (just == LEFT) {
            attributes.add("labeljust", "l");
        }
        if (just == RIGHT) {
            attributes.add("labeljust", "r");
        }
        if (loc == TOP) {
            attributes.add("labelloc", "t");
        }
        if (loc == BOTTOM) {
            attributes.add("labelloc", "b");
        }
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
