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

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

final class FontMeasurer {
    private static final FontRenderContext FONT_RENDER_CONTEXT =
            new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR).createGraphics().getFontRenderContext();
    private static final double COURIER_WIDTH = .5999;
    private static final Font COURIER = new Font("Courier", Font.PLAIN, 10);
    private static final double COURIER_SPACE_WIDTH = charWidth(COURIER, ' ');
    private static final double COURIER_BORDER_WIDTH = borderWidth(COURIER);
    private static final double[] COURIER_WIDTHS = courierWidths();
    private static final Set<String> FONTS = new HashSet<>();

    private FontMeasurer() {
    }

    private static double[] courierWidths() {
        double[] w = new double[256];
        for (int i = 32; i < 256; i++) {
            w[i] = charWidth(COURIER, (char) i);
        }
        return w;
    }

    private static double charWidth(Font font, char c) {
        return font.createGlyphVector(FONT_RENDER_CONTEXT, new char[]{56, c, 56}).getVisualBounds().getWidth();
    }

    private static double borderWidth(Font font) {
        return font.createGlyphVector(FONT_RENDER_CONTEXT, new char[]{56, 56}).getVisualBounds().getWidth();
    }

    static double[] measureFont(String name) {
        if (FONTS.contains(name)) {
            return new double[0];
        }
        FONTS.add(name);
        final Font font = new Font(name, Font.PLAIN, 10);
        final double spaceWidth = charWidth(font, ' ');
        final double borderWidth = borderWidth(font);
        double[] w = new double[256];
        for (int i = 0; i < 256; i++) {
            w[i] = COURIER_WIDTH * (i <= 32
                    ? (spaceWidth - borderWidth) / (COURIER_SPACE_WIDTH - COURIER_BORDER_WIDTH)
                    : (charWidth(font, (char) i) - borderWidth) / (COURIER_WIDTHS[i] - COURIER_BORDER_WIDTH));
        }
        return w;
    }
}
