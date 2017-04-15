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

import org.junit.Test;

import static guru.nidi.graphviz.attribute.Attributes.attr;
import static guru.nidi.graphviz.attribute.Attributes.attrs;
import static org.junit.Assert.assertEquals;

public class ColorTest {
    @Test
    public void simple() {
        assertColor(attr("color", "azure"), Color.AZURE);
    }

    @Test
    public void fill() {
        assertColor(attr("fillcolor", "azure"), Color.AZURE.fill());
    }

    @Test
    public void background() {
        assertColor(attr("bgcolor", "azure"), Color.AZURE.background());
    }

    @Test
    public void font() {
        assertColor(attr("fontcolor", "azure"), Color.AZURE.font());
    }

    @Test
    public void labelFont() {
        assertColor(attr("labelfontcolor", "azure"), Color.AZURE.labelFont());
    }

    @Test(expected = IllegalArgumentException.class)
    public void rgbNok() {
        Color.rgb("123");
    }

    @Test
    public void rgbOk() {
        assertColor(attr("color", "#123456"), Color.rgb("123456"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rgbaNok() {
        Color.rgba("123456");
    }

    @Test
    public void rgbaOk() {
        assertColor(attr("color", "#12345678"), Color.rgba("12345678"));
    }

    @Test
    public void hsv() {
        assertColor(attr("color", "0.12 0.34 0.56"), Color.hsv(.12, .34, .56));
    }

    @Test
    public void gradient() {
        assertColor(attr("color", "red:blue"), Color.RED.gradient(Color.BLUE));
    }

    @Test
    public void gradientAt() {
        assertColor(attr("color", "red:blue;0.3"), Color.RED.gradient(Color.BLUE, .3));
    }

    @Test
    public void angle() {
        assertEquals(attrs(attr("color", "red"), attr("gradientangle", 45)), Color.RED.angle(45));
    }

    @Test
    public void radial() {
        assertEquals(attrs(attr("color", "red"), attr("style", "radial"), attr("gradientangle", 45)),
                Color.RED.radial(45));
    }

    private void assertColor(Attributes value, Color color) {
        assertEquals(value, attrs(color));
    }

}
