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
package guru.nidi.graphviz.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class SvgSizeAnalyzerTest {
    private static final String SVG =
            "<svg width=\"1000pt\" height=\"500pt\" viewBox=\"0.00 0.00 7272.00 3618.00\" "
                    + "xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n"
                    + "<g id=\"graph0\" class=\"graph\" transform=\"scale(1.0 2.5) rotate(30.0) translate(360.0 32.0)\">";

    @Test
    void getValues() {
        final SvgSizeAnalyzer a = SvgSizeAnalyzer.svg(SVG);
        assertEquals(1000, a.getWidth());
        assertEquals(500, a.getHeight());
        assertEquals("pt", a.getUnit());
        assertEquals(1.0, a.getScaleX());
        assertEquals(2.5, a.getScaleY());
        assertEquals(30, a.getRotate());
        assertEquals(360, a.getTranslateX());
        assertEquals(32, a.getTranslateY());
    }

    @Test
    void svgWithoutChanges() {
        final SvgSizeAnalyzer a = SvgSizeAnalyzer.svg(SVG);
        assertEquals(SVG, a.getSvg());
    }

    @Test
    void transformWithoutChanges() {
        final SvgSizeAnalyzer a = SvgSizeAnalyzer.svg(SVG);
        assertEquals("scale(1.0 2.5) rotate(30.0) translate(360.0 32.0)", a.getTransform());
    }

    @Test
    void svgWithChanges() {
        final SvgSizeAnalyzer a = SvgSizeAnalyzer.svg(SVG);
        a.setSize(1, 2);
        a.setScale(3.5, 4.0);
        assertEquals("<svg width=\"1px\" height=\"2px\" viewBox=\"0.00 0.00 7272.00 3618.00\" "
                        + "xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n"
                        + "<g id=\"graph0\" class=\"graph\" transform=\"scale(3.5 4.0) rotate(30.0) translate(360.0 32.0)\">",
                a.getSvg());
    }

    @Test
    void transformWithChanges() {
        final SvgSizeAnalyzer a = SvgSizeAnalyzer.svg(SVG);
        a.setScale(3.5, 4.0);
        a.setRotate(5.5);
        a.setTranslate(6.6,7.7);
        assertEquals("scale(3.5 4.0) rotate(5.5) translate(6.6 7.7)", a.getTransform());
    }
}
