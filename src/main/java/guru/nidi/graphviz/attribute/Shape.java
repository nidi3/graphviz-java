/*
 * Copyright (C) 2015 Stefan Niederhauser (nidin@gmx.ch)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package guru.nidi.graphviz.attribute;

/**
 *
 */
public class Shape extends SimpleAttribute<String> {
    private Shape(String value) {
        super("shape", value);
    }

    public static final Shape
            ELLIPSE = new Shape("ellipse"),
            CIRCLE = new Shape("circle"),
            POINT = new Shape("point"),
            EGG = new Shape("egg"),
            TRIANGLE = new Shape("triangle"),
            DIAMOND = new Shape("diamond"),
            TRAPEZIUM = new Shape("trapezium"),
            PARALLELOGRAM = new Shape("parallelogram"),
            HOUSE = new Shape("house"),
            PENTAGON = new Shape("pentagon"),
            HEXAGON = new Shape("hexagon"),
            SEPTAGON = new Shape("septagon"),
            OCTAGON = new Shape("octagon"),
            DOUBLE_CIRCLE = new Shape("doublecircle"),
            DOUBLE_OCTAGON = new Shape("doubleoctagon"),
            TRIPLE_OCTAGON = new Shape("tripleoctagon"),
            INV_TRIANGLE = new Shape("invtriangle"),
            INV_TRAPEZIUM = new Shape("invtrapezium"),
            INV_HOUSE = new Shape("invhouse"),
            RECTANGLE = new Shape("rectangle"),
            NONE = new Shape("none");

    public static Attribute mDiamond(String topLabel, String bottomLabel) {
        return new MapAttribute("shape", "Mdiamond", "toplabel", topLabel, "bottomlabel", bottomLabel);
    }

    public static Attribute mSquare(String topLabel, String bottomLabel) {
        return new MapAttribute("shape", "Msquare", "toplabel", topLabel, "bottomlabel", bottomLabel);
    }

    public static Attribute mCircle(String topLabel, String bottomLabel) {
        return new MapAttribute("shape", "Mcircle", "toplabel", topLabel, "bottomlabel", bottomLabel);
    }

    public static Attribute polygon(int sides, double skew, double distortion) {
        return new MapAttribute("shape", "polygon", "sides", sides, "skew", skew, "distortion", distortion);
    }
}

