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

import static java.util.Locale.ENGLISH;

public final class GraphAttr {
    private static final String SIZE = "size";

    public static final Attributes<ForGraph>
            CENTER = new SingleAttributes<>("center", true),
            COMPOUND = new SingleAttributes<>("compound", true),
            CONCENTRATE = new SingleAttributes<>("concentrate", true),
            FORCE_LABELS_NOT = new SingleAttributes<>("forcelabels", false),
            LANDSCAPE = new SingleAttributes<>("orientation", "L");

    private GraphAttr() {
    }

    public static Attributes<ForGraph> dpi(int dpi) {
        return new SingleAttributes<>("dpi", dpi);
    }

    public static Attributes<ForGraph> sizeMax(double size) {
        return new SingleAttributes<>(SIZE, size);
    }

    public static Attributes<ForGraph> sizeMax(double sizeX, double sizeY) {
        return new SingleAttributes<>(SIZE, sizeX + "," + sizeY);
    }

    public static Attributes<ForGraph> sizePreferred(double size) {
        return new SingleAttributes<>(SIZE, size + "!");
    }

    public static Attributes<ForGraph> sizePreferred(double sizeX, double sizeY) {
        return new SingleAttributes<>(SIZE, sizeX + "," + sizeY + "!");
    }

    public enum SplineMode {
        LINE, SPLINE, POLYLINE, ORTHO, CURVED, NONE
    }

    public static Attributes<ForGraph> splines(SplineMode mode) {
        return new SingleAttributes<>("splines", mode.toString().toLowerCase(ENGLISH));
    }

    public static Attributes<ForGraph> pad(double pad) {
        return new SingleAttributes<>("pad", pad);
    }

    public static Attributes<ForGraph> pad(double padX, double padY) {
        return new SingleAttributes<>("pad", padX + "," + padY);
    }

    public static Attributes<ForGraph> margin(double margin) {
        return new SingleAttributes<>("margin", margin);
    }

    public static Attributes<ForGraph> margin(double marginX, double marginY) {
        return new SingleAttributes<>("margin", marginX + "," + marginY);
    }
}
