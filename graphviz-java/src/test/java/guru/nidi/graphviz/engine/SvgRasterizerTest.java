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
package guru.nidi.graphviz.engine;

import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SvgRasterizerTest {
    @Test
    void preProcess() {
        final DummyRasterizer rasterizer = new DummyRasterizer();
        rasterizer.rasterize(Graphviz.fromString(""), Graphics2D -> {
        }, quote(
                "<svg width='62px' height='116px' viewBox='0.00 0.00 62.00 116.00'>" +
                        "<g id='graph0' class='graph' transform=' rotate(0) translate(4 112)'>\n" +
                        "<text xlink:href='ref' stroke='transparent'>a</text>\n" +
                        "<text fill='transparent'>a</text>\n" +
                        "</g></svg>"));
        assertEquals(quote(
                "<svg width='62px' height='116px' viewBox='0.00 0.00 62.00 116.00'>" +
                        "<g id='graph0' class='graph' transform=' rotate(0) translate(4 112)'>\n" +
                        "<text xlink:href='file://ref' stroke='#fff' stroke-opacity='0.0'>a</text>\n" +
                        "<text fill='#fff' fill-opacity='0.0'>a</text>\n" +
                        "</g></svg>"),
                rasterizer.svg);
    }

    private static String quote(String s) {
        return s.replace("'", "\"");
    }

    private static class DummyRasterizer extends SvgRasterizer {
        String svg = "";

        @Override
        BufferedImage doRasterize(Graphviz graphviz, Consumer<Graphics2D> graphicsConfigurer, String svg) {
            this.svg = svg;
            return new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        }
    }
}
