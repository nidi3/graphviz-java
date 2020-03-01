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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Format {
    PNG("svg", "png", true, true) {
        @Override
        String preProcess(String src) {
            return encodeXml(super.preProcess(src));
        }

        @Override
        EngineResult postProcess(Graphviz graphviz, EngineResult result) {
            return result.mapString(s -> postProcessSvg(graphviz.processOptions, s, true));
        }
    },

    SVG("svg", "svg", false, true) {
        @Override
        String preProcess(String src) {
            return encodeXml(super.preProcess(src));
        }

        @Override
        EngineResult postProcess(Graphviz graphviz, EngineResult result) {
            return result.mapString(s -> postProcessSvg(graphviz.processOptions, s, true));
        }
    },

    SVG_STANDALONE("svg", "svg", false, true) {
        @Override
        String preProcess(String src) {
            return encodeXml(super.preProcess(src));
        }

        @Override
        EngineResult postProcess(Graphviz graphviz, EngineResult result) {
            return result.mapString(s -> postProcessSvg(graphviz.processOptions, s, false));
        }
    },
    DOT("dot", "dot"),
    XDOT("xdot", "xdot"),
    PLAIN("plain", "txt"),
    PLAIN_EXT("plain-ext", "txt"),
    PS("ps", "ps"),
    PS2("ps2", "ps"),
    JSON("json", "json"),
    IMAP("imap", "imap"),
    CMAPX("cmapx", "cmapx"),
    JSON0("json0", "json");

    private static final Logger LOG = LoggerFactory.getLogger(Format.class);
    private static final Pattern SVG_PATTERN = Pattern.compile(
            "<svg width=\"(?<width>\\d+)(?<unit>p[tx])\" height=\"(?<height>\\d+)p[tx]\""
                    + "(?<between>.*?>\\R<g.*?)transform=\"scale\\((?<scaleX>[0-9.]+) (?<scaleY>[0-9.]+)\\)",
            Pattern.DOTALL);

    final String vizName;
    public final String fileExtension;
    final boolean image;
    final boolean svg;

    Format(String vizName, String fileExtension) {
        this(vizName, fileExtension, false, false);
    }

    Format(String vizName, String fileExtension, boolean image, boolean svg) {
        this.vizName = vizName;
        this.fileExtension = fileExtension;
        this.image = image;
        this.svg = svg;
    }

    String preProcess(String src) {
        return replaceSubSpaces(src);
    }

    EngineResult postProcess(Graphviz graphviz, EngineResult result) {
        return result;
    }

    private static String replaceSubSpaces(String src) {
        final char[] chars = src.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] < ' ' && chars[i] != '\t' && chars[i] != '\r' && chars[i] != '\n') {
                chars[i] = ' ';
            }
        }
        return new String(chars);
    }

    private static String encodeXml(String src) {
        return src.replace("&", "&amp;");
    }

    private static String postProcessSvg(ProcessOptions options, String result, boolean prefix) {
        final String unprefixed = prefix ? withoutPrefix(result) : result;
        return pointsToPixels(unprefixed, options.dpi, options.width, options.height, options.scale);
    }

    private static String withoutPrefix(String svg) {
        final int pos = svg.indexOf("<svg ");
        return pos < 0 ? svg : svg.substring(pos);
    }

    private static String pointsToPixels(String svg, double dpi, int width, int height, double scale) {
        final Matcher m = SVG_PATTERN.matcher(svg);
        if (!m.find()) {
            LOG.warn("Generated SVG has not the expected format. There might be image size problems.");
            return svg;
        }
        return m.replaceFirst("<svg " + svgSize(m, width, height, scale) + m.group("between") + svgScale(m, dpi));
    }

    private static String svgSize(Matcher m, int width, int height, double scale) {
        double w = Integer.parseInt(m.group("width"));
        double h = Integer.parseInt(m.group("height"));
        if (width > 0 && height > 0) {
            w = width;
            h = height;
        } else if (width > 0) {
            h *= width / w;
            w = width;
        } else if (height > 0) {
            w *= height / h;
            h = height;
        }
        return "width=\"" + Math.round(w * scale) + "px\" height=\"" + Math.round(h * scale) + "px\"";
    }

    private static String svgScale(Matcher m, double dpi) {
        final double pixelScale = m.group("unit").equals("px") ? 1 : Math.round(10000 * dpi / 72) / 10000d;
        final double scaleX = Double.parseDouble(m.group("scaleX")) / pixelScale;
        final double scaleY = Double.parseDouble(m.group("scaleY")) / pixelScale;
        return "transform=\"scale(" + scaleX + " " + scaleY + ")";
    }
}
