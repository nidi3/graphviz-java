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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

import static java.awt.RenderingHints.*;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

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
            final Tokenizer t = new Tokenizer(draw);
            int len, x, y, w, h;
            int[][] xy;
            while (t.hasMore()) {
                char c = t.readChar();
                switch (c) {
                    case 'E':
                    case 'e':
                        x = t.readDouble();
                        y = t.readDouble();
                        w = t.readDouble();
                        h = t.readDouble();
                        if (c == 'e') {
                            g.drawOval(x - w, y - h, w * 2, h * 2);
                        } else {
                            g.fillOval(x - w, y - h, w * 2, h * 2);
                        }
                        break;
                    case 'P':
                    case 'p':
                        len = t.readInt();
                        xy = t.readXYarray(len);
                        if (c == 'p') {
                            g.drawPolygon(xy[0], xy[1], len);
                        } else {
                            g.fillPolygon(xy[0], xy[1], len);
                        }
                        break;
                    case 'L':
                        len = t.readInt();
                        xy = t.readXYarray(len);
                        g.drawPolyline(xy[0], xy[1], len);
                        break;
                    case 'B':
                    case 'b':
                        len = t.readInt();
                        xy = t.readXYarray(len);
                        break;
                    case 'T':
                        x = t.readDouble();
                        y = t.readDouble();
                        int j = t.readInt();
                        w = t.readDouble();
                        g.drawString(t.readString(), x + (j - 1) * w / 2, y + g.getFontMetrics().getAscent() / 2);
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
                        x = t.readDouble();
                        y = t.readDouble();
                        w = t.readDouble();
                        h = t.readDouble();
                        t.readString();
                        break;
                }
            }
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
}

class Tokenizer {
    private final String in;
    private int pos;

    public Tokenizer(String in) {
        this.in = in;
    }

    public String readString() {
        int len = readInt();
        String s = in.substring(pos + 1, pos + len + 1);
        pos += len + 2;
        return s;
    }

    private String readTillSpace() {
        int e = in.indexOf(' ', pos);
        String s = in.substring(pos, e);
        pos = e + 1;
        return s;
    }

    public char readChar() {
        final String s = readTillSpace();
        if (s.length() != 1) {
            throw new GraphvizException("Expected char, but found '" + s + "'");
        }
        return s.charAt(0);
    }

    public int readInt() {
        return Integer.parseInt(readTillSpace());
    }

    public int readDouble() {
        return (int) Math.round(Double.parseDouble(readTillSpace()));
    }

    public int[][] readXYarray(int len) {
        int[] x = new int[len];
        int[] y = new int[len];
        for (int i = 0; i < len; i++) {
            x[i] = readDouble();
            y[i] = readDouble();
        }
        return new int[][]{x, y};
    }

    public boolean hasMore() {
        return pos < in.length() - 1;
    }
}