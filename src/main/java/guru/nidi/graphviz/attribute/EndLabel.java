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

public final class EndLabel extends SimpleLabel implements Attributes {
    private final String key;
    private final Double angle;
    private final Double distance;

    private EndLabel(String key, String value, boolean html, Double angle, Double distance) {
        super(value, html);
        this.key = key;
        this.angle = angle;
        this.distance = distance;
    }

    public static EndLabel head(SimpleLabel label, Double angle, Double distance) {
        return new EndLabel("headlabel", label.value, label.html, angle, distance);
    }

    public static EndLabel tail(SimpleLabel label, Double angle, Double distance) {
        return new EndLabel("taillabel", label.value, label.html, angle, distance);
    }

    @Override
    public Attributes applyTo(MapAttributes attributes) {
        attributes.add(key, this);
        if (angle != null) {
            attributes.add("labelangle", angle);
        }
        if (distance != null) {
            attributes.add("labeldistance", distance);
        }
        return attributes;
    }

}
