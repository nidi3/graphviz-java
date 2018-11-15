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

public class Style extends SingleAttributes<String, ForAll> {
    public static final Style
            DASHED = new Style("dashed"),
            DOTTED = new Style("dotted"),
            SOLID = new Style("solid"),
            INVIS = new Style("invis"),
            BOLD = new Style("bold"),
            FILLED = new Style("filled"),
            RADIAL = new Style("radial"),
            DIAGONALS = new Style("diagonals"),
            ROUNDED = new Style("rounded");

    public Style(String value) {
        super("style", value);
    }

    public static Style lineWidth(int width) {
        return new Style("setlinewidth(" + width + ")");
    }

    public Style and(Style style) {
        return new Style(value + "," + style.value);
    }
}
