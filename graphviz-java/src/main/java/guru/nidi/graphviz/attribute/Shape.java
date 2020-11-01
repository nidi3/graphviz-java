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

import static guru.nidi.graphviz.attribute.NodeAttr.nodeAttr;

public final class Shape extends SingleAttributes<String, ForNode> {
    private static final String SHAPE = "shape";

    /**
     * Records are better supported by the {@link Records} class.
     */
    public static final Shape
            RECORD = new Shape("record"),
            M_RECORD = new Shape("Mrecord");

    public static final Shape
            BOX = new Shape("box"),
            ELLIPSE = new Shape("ellipse"),
            OVAL = new Shape("oval"),
            CIRCLE = new Shape("circle"),
            POINT = new Shape("point"),
            EGG = new Shape("egg"),
            TRIANGLE = new Shape("triangle"),
            PLAIN_TEXT = new Shape("plaintext"),
            PLAIN = new Shape("plain"),
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
            M_DIAMOND = new Shape("Mdiamond"),
            M_SQUARE = new Shape("Msquare"),
            M_CIRCLE = new Shape("Mcircle"),
            RECT = new Shape("rect"),
            RECTANGLE = new Shape("rectangle"),
            SQUARE = new Shape("square"),
            STAR = new Shape("star"),
            NONE = new Shape("none"),
            UNDERLINE = new Shape("underline"),
            CYLINDER = new Shape("cylinder"),
            NOTE = new Shape("note"),
            TAB = new Shape("tab"),
            FOLDER = new Shape("folder"),
            BOX_3D = new Shape("box3d"),
            COMPONENT = new Shape("component"),
            PROMOTER = new Shape("promoter"),
            CDS = new Shape("cds"),
            TERMINATOR = new Shape("terminator"),
            UTR = new Shape("utr"),
            PRIMER_SITE = new Shape("primersite"),
            RESTRICTION_SITE = new Shape("restrictionsite"),
            FIVE_P_OVERHANG = new Shape("fivepoverhang"),
            THREE_P_OVERHANG = new Shape("threepoverhang"),
            N_OVERHANG = new Shape("noverhang"),
            ASSEMBLY = new Shape("assembly"),
            SIGNATURE = new Shape("signature"),
            INSULATOR = new Shape("insulator"),
            RIBO_SITE = new Shape("ribosite"),
            RNA_STAB = new Shape("rnastab"),
            PROTEASE_SITE = new Shape("proteasesite"),
            PROTEIN_STAB = new Shape("proteinstab"),
            R_PROMOTER = new Shape("rpromoter"),
            R_ARROW = new Shape("rarrow"),
            L_ARROW = new Shape("larrow"),
            L_PROMOTER = new Shape("lpromoter");

    private Shape(String value) {
        super(SHAPE, value);
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

