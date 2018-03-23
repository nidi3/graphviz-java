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

import guru.nidi.graphviz.attribute.MutableAttributed;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.util.MathArrays;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

import static java.awt.RenderingHints.*;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static java.lang.Math.round;

public class XdotRasterizer implements Rasterizer {
    @Override
    public Format format() {
        return Format.XDOT;
    }

    @Override
    public BufferedImage rasterize(Graphviz graphviz, Consumer<Graphics2D> graphicsConfigurer, String input) {
        try {
            final MutableGraph graph = Parser.read(input);
            final double[] bb = boundingBox(graph);
            final BufferedImage img = new BufferedImage((int) Math.ceil(bb[2] - bb[0]), (int) Math.ceil(bb[3] - bb[1]), TYPE_INT_ARGB);
            final Graphics2D g = img.createGraphics();
            configGraphics(g);
            draw(g, graph.graphAttrs());
            graph.nodes().forEach(n -> {
                draw(g, n.attrs());
                n.links().forEach(l -> draw(g, l.attrs()));
            });
            g.dispose();
            return img;
        } catch (Exception e) {
            throw new GraphvizException("Error rasterizing image", e);
        }
    }

    private void draw(Graphics2D g, MutableAttributed<?> attrs) {
        draw(g, (String) attrs.get("_draw_"));
        draw(g, (String) attrs.get("_ldraw_"));
        draw(g, (String) attrs.get("_hdraw_"));
        draw(g, (String) attrs.get("_tdraw_"));
        draw(g, (String) attrs.get("_hldraw_"));
        draw(g, (String) attrs.get("_tldraw_"));
    }

    private double[] boundingBox(MutableGraph graph) {
        final String bb = (String) graph.graphAttrs().get("bb");
        final String[] parts = bb.split(",");
        return new double[]{Double.parseDouble(parts[0]), Double.parseDouble(parts[1]),
                Double.parseDouble(parts[2]), Double.parseDouble(parts[3])};
    }

    private void draw(Graphics2D g, String draw) {
        if (draw != null) {
            final XdotTokenizer t = new XdotTokenizer(draw);
            while (t.hasMore()) {
                char c = t.readChar();
                switch (c) {
                    case 'E':
                    case 'e':
                        ellipse(g, t, c == 'E');
                        break;
                    case 'P':
                    case 'p':
                        polygon(g, t, c == 'P');
                        break;
                    case 'L':
                        polyline(g, t);
                        break;
                    case 'B':
                    case 'b':
                        bspline2(g, t);
                        break;
                    case 'T':
                        text(g, t);
                        break;
                    case 't':
                        t.readInt();
                        break;
                    case 'C':
                        g.setColor(color(t.readString()));
                        break;
                    case 'c':
                        g.setPaint(color(t.readString()));
                        break;
                    case 'F':
                        int s = t.readInt();
                        g.setFont(Font.getFont(t.readString(), g.getFont()).deriveFont((float) s));
                        break;
                    case 'S':
                        t.readString();
                        break;
                    case 'I':
                        image(g, t);
                        break;
                }
            }
        }
    }

    private void image(Graphics2D g, XdotTokenizer t) {
        int x = t.readIntDouble();
        int y = t.readIntDouble();
        int w = t.readIntDouble();
        int h = t.readIntDouble();
        t.readString();
    }

    private void text(Graphics2D g, XdotTokenizer t) {
        int x = t.readIntDouble();
        int y = t.readIntDouble();
        int j = t.readInt();
        int w = t.readIntDouble();
        g.drawString(t.readString(), x + (j - 1) * w / 2, y + g.getFontMetrics().getAscent() / 2);
    }

    private void polyline(Graphics2D g, XdotTokenizer t) {
        int len = t.readInt();
        int[][] ixy = t.readIntCoords(len);
        g.drawPolyline(ixy[0], ixy[1], len);
    }

    private void polygon(Graphics2D g, XdotTokenizer t, boolean fill) {
        int len = t.readInt();
        int[][] ixy = t.readIntCoords(len);
        if (fill) {
            g.fillPolygon(ixy[0], ixy[1], len);
        } else {
            g.drawPolygon(ixy[0], ixy[1], len);
        }
    }

    private void ellipse(Graphics2D g, XdotTokenizer t, boolean fill) {
        int x = t.readIntDouble();
        int y = t.readIntDouble();
        int w = t.readIntDouble();
        int h = t.readIntDouble();
        if (fill) {
            g.fillOval(x - w, y - h, w * 2, h * 2);
        } else {
            g.drawOval(x - w, y - h, w * 2, h * 2);
        }
    }

    private Color color(String col) {
        if (col.charAt(0) == '#') {
            if (col.length() == 9) {
                return new Color(Integer.parseInt(col.substring(1, 3), 16), Integer.parseInt(col.substring(3, 5), 16),
                        Integer.parseInt(col.substring(5, 7), 16), Integer.parseInt(col.substring(7, 9), 16));
            }
            if (col.length() == 7) {
                return new Color(Integer.parseInt(col.substring(1, 3), 16), Integer.parseInt(col.substring(3, 5), 16),
                        Integer.parseInt(col.substring(5, 7), 16));
            }
        }
        throw new RuntimeException("Unsupported color " + col);
    }

    private void configGraphics(Graphics2D graphics) {
        graphics.setRenderingHint(KEY_ALPHA_INTERPOLATION, VALUE_ALPHA_INTERPOLATION_QUALITY);
        graphics.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(KEY_COLOR_RENDERING, VALUE_COLOR_RENDER_QUALITY);
        graphics.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BICUBIC);
        graphics.setRenderingHint(KEY_RENDERING, VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON);
    }

    private void bspline(Graphics2D g, XdotTokenizer t) {
        int len = t.readInt();
        double[][] xy = t.readDoubleCoords(len);
        MathArrays.sortInPlace(xy[0], xy[1]);
        for (int i = 0; i < xy[0].length; i++) {
            g.drawOval((int) round(xy[0][i]), (int) round(xy[1][i]), 3, 3);
        }
        try {
            final PolynomialSplineFunction psf = new SplineInterpolator().interpolate(xy[0], xy[1]);
            double s = (xy[0][len - 1] - xy[0][0]) / 20;
            for (int i = 0; i < 20; i++) {
                double x0 = xy[0][0] + i * s;
                double x1 = xy[0][0] + (i + 1) * s;
                g.drawLine((int) round(x0), (int) round(psf.value(x0)), (int) round(x1), (int) round(psf.value(x1)));
            }
        } catch (NonMonotonicSequenceException e) {
            e.printStackTrace();
        }
    }

    private void bspline2(Graphics2D g, XdotTokenizer t) {
        int len = t.readInt();
        double[][] xy = t.readDoubleCoords(len);
//        for (int i = 0; i < xy[0].length; i++) {
//            g.drawOval((int) round(xy[0][i]) - 2, (int) round(xy[1][i]) - 2, 4, 4);
//        }

        int m = 50;
        double xa, ya, xb, yb, xc, yc, xd, yd, a0, a1, a2, a3, b0, b1, b2, b3, x, y, x0 = 0, y0 = 0;
        for (int i = 1; i < xy[0].length - 2; i++) {
            xa = xy[0][i - 1];
            xb = xy[0][i];
            xc = xy[0][i + 1];
            xd = xy[0][i + 2];
            ya = xy[1][i - 1];
            yb = xy[1][i];
            yc = xy[1][i + 1];
            yd = xy[1][i + 2];
            a3 = (-xa + 3 * (xb - xc) + xd) / 6;
            b3 = (-ya + 3 * (yb - yc) + yd) / 6;
            a2 = (xa - 2 * xb + xc) / 2;
            b2 = (ya - 2 * yb + yc) / 2;
            a1 = (xc - xa) / 2;
            b1 = (yc - ya) / 2;
            a0 = (xa + 4 * xb + xc) / 6;
            b0 = (ya + 4 * yb + yc) / 6;
            for (int j = 0; j <= m; j++) {
                double f = (double) j / m;
                x = ((a3 * f + a2) * f + a1) * f + a0;
                y = ((b3 * f + b2) * f + b1) * f + b0;
                if ((i > 1 || j > 0) && (Math.abs(x - x0) > 2 || Math.abs(y - y0) > 2)) {
                    g.drawLine((int) round(x0), (int) round(y0), (int) round(x), (int) round(y));
                    x0 = x;
                    y0 = y;
                }
                if (i == 1 && j == 0) {
                    x0 = x;
                    y0 = y;
                }
            }
        }
    }
}

