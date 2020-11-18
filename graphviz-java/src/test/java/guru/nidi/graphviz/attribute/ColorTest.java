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

import org.junit.jupiter.api.Test;

import static guru.nidi.graphviz.attribute.Attributes.attr;
import static guru.nidi.graphviz.attribute.Attributes.attrs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ColorTest {
    @Test
    void simple() {
        assertColor(attr("color", "azure"), Color.AZURE);
    }

    @Test
    void fill() {
        assertColor(attr("fillcolor", "azure"), Color.AZURE.fill());
    }

    @Test
    void background() {
        assertColor(attr("bgcolor", "azure"), Color.AZURE.background());
    }

    @Test
    void font() {
        assertColor(attr("fontcolor", "azure"), Color.AZURE.font());
    }

    @Test
    void labelFont() {
        assertColor(attr("labelfontcolor", "azure"), Color.AZURE.labelFont());
    }

    @Test
    void rgbNok() {
        assertThrows(IllegalArgumentException.class, () -> Color.rgb("123"));
    }

    @Test
    void rgbOk() {
        assertColor(attr("color", "#123456"), Color.rgb("123456"));
    }

    @Test
    void rgbOkWithHash() {
        assertColor(attr("color", "#123456"), Color.rgb("#123456"));
    }

    @Test
    void rgbaNok() {
        assertThrows(IllegalArgumentException.class, () -> Color.rgba("123456"));
    }

    @Test
    void rgbaOk() {
        assertColor(attr("color", "#12345678"), Color.rgba("12345678"));
    }

    @Test
    void rgbaOkWithHash() {
        assertColor(attr("color", "#12345678"), Color.rgba("#12345678"));
    }

    @Test
    void rgbInt() {
        assertColor(attr("color", "#f008ff"), Color.rgb(0xf008ff));
    }

    @Test
    void rgbaInt() {
        assertColor(attr("color", "#f008fff8"), Color.rgba(0xf8f008ff));
    }

    @Test
    void hsv() {
        assertColor(attr("color", "0.12 0.34 0.56"), Color.hsv(.12, .34, .56));
    }

    @Test
    void and() {
        assertColor(attr("color", "red:blue"), Color.RED.and(Color.BLUE));
    }

    @Test
    void andAt() {
        assertColor(attr("color", "red:blue;0.3"), Color.RED.and(Color.BLUE, .3));
    }

    @Test
    void angle() {
        assertEquals(attrs(attr("color", "red"), attr("gradientangle", 45)), Color.RED.angle(45));
    }

    @Test
    void radial() {
        assertEquals(attrs(attr("color", "red"), attr("style", "radial"), attr("gradientangle", 45)),
                Color.RED.radial(45));
    }

    @Test
    void striped() {
        assertEquals(attrs(attr("color", "red:green"), attr("style", "striped")),
                Color.RED.and(Color.GREEN).striped());
    }

    @Test
    void wedged() {
        assertEquals(attrs(attr("color", "red:blue:green"), attr("style", "wedged")),
                Color.RED.and(Color.BLUE, Color.GREEN).wedged());
    }

    private void assertColor(Attributes value, Color color) {
        assertEquals(value, attrs(color));
    }

}
