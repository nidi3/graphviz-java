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

import static guru.nidi.graphviz.attribute.Attributes.attr;
import static guru.nidi.graphviz.attribute.Attributes.attrs;
import static guru.nidi.graphviz.attribute.NodeAttr.nodeAttr;

public final class Shape extends SingleAttributes<String, ForNode> {
    private static final String SHAPE = "shape";

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

    private Shape(String value) {
        super(SHAPE, value);
    }

    public static Attributes<ForNode> mDiamond(String topLabel, String bottomLabel) {
        return attrs(attr(SHAPE, "Mdiamond"), attr("toplabel", topLabel), attr("bottomlabel", bottomLabel));
    }

    public static Attributes<ForNode> mSquare(String topLabel, String bottomLabel) {
        return attrs(attr(SHAPE, "Msquare"), attr("toplabel", topLabel), attr("bottomlabel", bottomLabel));
    }

    public static Attributes<ForNode> mCircle(String topLabel, String bottomLabel) {
        return attrs(attr(SHAPE, "Mcircle"), attr("toplabel", topLabel), attr("bottomlabel", bottomLabel));
    }

    public static Polygon polygon(int sides) {
        return new Polygon(sides, null, null, null);
    }

    public static class Polygon implements Attributes<ForNode> {
        private final int sides;
        @Nullable
        private final Double skew;
        @Nullable
        private final Double distortion;
        @Nullable
        private final Double rotation;

        Polygon(int sides, @Nullable Double skew, @Nullable Double distortion, @Nullable Double rotation) {
            this.sides = sides;
            this.skew = skew;
            this.distortion = distortion;
            this.rotation = rotation;
        }

        public Polygon skew(double skew) {
            return new Polygon(sides, skew, distortion, rotation);
        }

        public Polygon distortion(double distortion) {
            return new Polygon(sides, skew, distortion, rotation);
        }

        public Polygon rotation(double rotation) {
            return new Polygon(sides, skew, distortion, rotation);
        }

        @Override
        public Attributes<? super ForNode> applyTo(MapAttributes<? super ForNode> attrs) {
            nodeAttr("shape", "polygon").applyTo(attrs);
            nodeAttr("sides", sides).applyTo(attrs);
            if (skew != null) {
                nodeAttr("skew", skew).applyTo(attrs);
            }
            if (distortion != null) {
                nodeAttr("distortion", distortion).applyTo(attrs);
            }
            if (rotation != null) {
                nodeAttr("orientation", rotation).applyTo(attrs);
            }
            return attrs;
        }
    }
}

