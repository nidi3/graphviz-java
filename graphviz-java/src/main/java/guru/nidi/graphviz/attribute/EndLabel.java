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

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Label placed near the end of an edge.
 */
public final class EndLabel extends SimpleLabel implements Attributes<ForLink> {
    private final String key;
    @Nullable
    private final Double angle;
    @Nullable
    private final Double distance;

    private EndLabel(String key, String value, boolean html, @Nullable Double angle, @Nullable Double distance) {
        super(value, html);
        this.key = key;
        this.angle = angle;
        this.distance = distance;
    }

    /**
     * Creates a label to be placed near the head of an edge.
     * 
     * @param label a label
     * @param angle angle of rotation
     * @param distance scaling factor for the distance from the head
     * @return a new head label
     * 
     * @see <a href="https://graphviz.org/docs/attrs/headlabel/">headlabel</a>
     * @see <a href="https://graphviz.org/docs/attrs/labelangle/">labelangle</a>
     * @see <a href="https://graphviz.org/docs/attrs/labeldistance/">labeldistance</a>
     */
    public static EndLabel head(SimpleLabel label, @Nullable Double angle, @Nullable Double distance) {
        return new EndLabel("headlabel", label.value, label.html, angle, distance);
    }

    /**
     * Creates a label to be placed near the tail of an edge.
     * 
     * @param label a label
     * @param angle angle of rotation
     * @param distance scaling factor for the distance from the tail
     * @return a new head label
     * 
     * @see <a href="https://graphviz.org/docs/attrs/taillabel/">taillabel</a>
     * @see <a href="https://graphviz.org/docs/attrs/labelangle/">labelangle</a>
     * @see <a href="https://graphviz.org/docs/attrs/labeldistance/">labeldistance</a>
     */
    public static EndLabel tail(SimpleLabel label, @Nullable Double angle, @Nullable Double distance) {
        return new EndLabel("taillabel", label.value, label.html, angle, distance);
    }

    @Override
    public Attributes<? super ForLink> applyTo(MapAttributes<? super ForLink> attributes) {
        attributes.add(key, this);
        if (angle != null) {
            attributes.add("labelangle", angle);
        }
        if (distance != null) {
            attributes.add("labeldistance", distance);
        }
        return attributes;
    }

    /**
     * Determines whether an object is "equal" to this end label.
     * 
     * @param o the reference object with which to compare.
     * @return {@code true} if this end label is the same as the {@code o} argument; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final EndLabel endLabel = (EndLabel) o;
        return Objects.equals(key, endLabel.key)
                && Objects.equals(angle, endLabel.angle)
                && Objects.equals(distance, endLabel.distance);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), key, angle, distance);
    }
}
