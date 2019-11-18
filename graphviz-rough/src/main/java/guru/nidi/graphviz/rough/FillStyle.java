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
package guru.nidi.graphviz.rough;

import java.util.HashMap;
import java.util.Map;

public class FillStyle {
    public static class Hachure extends FillStyle {
        private Hachure() {
            super("hachure");
        }

        public Hachure width(double width) {
            values.put("fillWeight", width);
            return this;
        }

        public Hachure angle(double angle) {
            values.put("hachureAngle", angle);
            return this;
        }

        public Hachure gap(double gap) {
            values.put("hachureGap", gap);
            return this;
        }
    }

    public static class CrossHatch extends FillStyle {
        private CrossHatch() {
            super("cross-hatch");
        }

        public CrossHatch width(double width) {
            values.put("fillWeight", width);
            return this;
        }

        public CrossHatch angle(double angle) {
            values.put("hachureAngle", angle);
            return this;
        }

        public CrossHatch gap(double gap) {
            values.put("hachureGap", gap);
            return this;
        }
    }

    public static class Zigzag extends FillStyle {
        private Zigzag() {
            super("zigzag");
        }

        public Zigzag width(double width) {
            values.put("fillWeight", width);
            return this;
        }

        public Zigzag angle(double angle) {
            values.put("hachureAngle", angle);
            return this;
        }

        public Zigzag gap(double gap) {
            values.put("hachureGap", gap);
            return this;
        }
    }

    public static class ZigzagLine extends FillStyle {
        private ZigzagLine() {
            super("zigzag-line");
        }

        public ZigzagLine width(double width) {
            values.put("fillWeight", width);
            return this;
        }

        public ZigzagLine angle(double angle) {
            values.put("hachureAngle", angle);
            return this;
        }

        public ZigzagLine gap(double gap) {
            values.put("hachureGap", gap);
            return this;
        }

        public ZigzagLine size(double size) {
            values.put("zigzagOffset", size);
            return this;
        }
    }

    public static class Starburst extends FillStyle {
        private Starburst() {
            super("starburst");
        }

        public Starburst width(double width) {
            values.put("fillWeight", width);
            return this;
        }

        public Starburst gap(double gap) {
            values.put("hachureGap", gap);
            return this;
        }
    }

    public static class Dots extends FillStyle {
        private Dots() {
            super("dots");
        }

        public Dots size(double size) {
            values.put("fillWeight", size);
            return this;
        }
    }

    public static class Dashed extends FillStyle {
        private Dashed() {
            super("dashed");
        }

        public Dashed width(double size) {
            values.put("fillWeight", size);
            return this;
        }

        public Dashed angle(double angle) {
            values.put("hachureAngle", angle);
            return this;
        }

        public Dashed gap(double gap) {
            values.put("hachureGap", gap);
            return this;
        }

        public Dashed length(double length) {
            values.put("dashOffset", length);
            return this;
        }
    }

    Map<String, Object> values;

    private FillStyle(String style) {
        values = new HashMap<>();
        values.put("fillStyle", style);
    }

    public static Hachure hachure() {
        return new Hachure();
    }

    public static CrossHatch crossHatch() {
        return new CrossHatch();
    }

    public static FillStyle solid() {
        return new FillStyle("solid");
    }

    public static FillStyle zigzag() {
        return new Zigzag();
    }

    public static ZigzagLine zigzagLine() {
        return new ZigzagLine();
    }

    public static Dots dots() {
        return new Dots();
    }

    public static Starburst starburst() {
        return new Starburst();
    }

    public static Dashed dashed() {
        return new Dashed();
    }
}
