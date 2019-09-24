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

import java.util.HashSet;
import java.util.Set;

import static guru.nidi.graphviz.attribute.validate.ValidatorMessage.Severity.ERROR;
import static java.util.Arrays.asList;

class ArrowTypeValidator {
    private static final Set<Shape> SHAPES = new HashSet<>(asList(
            new Shape("box", true, true), new Shape("crow", true, false), new Shape("curve", true, false),
            new Shape("icurve", true, false), new Shape("diamond", true, true), new Shape("dot", false, true),
            new Shape("inv", true, true), new Shape("none", false, false), new Shape("normal", true, true),
            new Shape("tee", true, false), new Shape("vee", true, false)));

    private final String s;
    private int pos;

    ArrowTypeValidator(Object value) {
        s = value.toString();
        pos = 0;
    }

    ValidatorMessage validate() {
        int shapes = 0;
        do {
            final ValidatorMessage message = validateShape();
            if (message != null) {
                return message;
            }
            shapes++;
        } while (pos < s.length() && shapes < 4);
        if (pos < s.length() - 1) {
            return new ValidatorMessage(ERROR, "More than 4 shapes in '" + s + "'.");
        }
        if (shapes > 1 && s.endsWith("none")) {
            return new ValidatorMessage(ERROR, "Last shape cannot be 'none' in '" + s + "'.");
        }
        return null;
    }

    private ValidatorMessage validateShape() {
        boolean o = false;
        if (pos < s.length() && s.charAt(pos) == 'o') {
            o = true;
            pos++;
        }
        boolean lr = false;
        if (pos < s.length() && (s.charAt(pos) == 'l' || s.charAt(pos) == 'r')) {
            lr = true;
            pos++;
        }
        final Shape shape = findShape();
        if (shape == null) {
            return new ValidatorMessage(ERROR, "Unknown shape '" + s.substring(pos) + "'.");
        }
        if (o && !shape.o) {
            return new ValidatorMessage(ERROR, "Shape '" + shape.name + "' is not allowed a 'o' prefix.");
        }
        if (lr && !shape.lr) {
            return new ValidatorMessage(ERROR, "Shape '" + shape.name + "' is not allowed a 'l'/'r' prefix.");
        }
        return null;
    }

    private Shape findShape() {
        for (final Shape shape : SHAPES) {
            if (s.substring(pos, Math.min(s.length(), pos + shape.name.length())).equals(shape.name)) {
                pos += shape.name.length();
                return shape;
            }
        }
        return null;
    }

    static class Shape {
        final String name;
        final boolean lr;
        final boolean o;

        Shape(String name, boolean lr, boolean o) {
            this.name = name;
            this.lr = lr;
            this.o = o;
        }
    }
}
