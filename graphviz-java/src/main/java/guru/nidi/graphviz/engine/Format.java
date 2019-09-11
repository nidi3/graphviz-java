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
        EngineResult postProcess(EngineResult result, double fontAdjust) {
            return result.mapString(s -> postProcessSvg(s, true, fontAdjust));
        }
    },

    SVG("svg", "svg", false, true) {
        @Override
        EngineResult postProcess(EngineResult result, double fontAdjust) {
            return result.mapString(s -> postProcessSvg(s, true, fontAdjust));
        }
    },

    SVG_STANDALONE("svg", "svg", false, true) {
        @Override
        EngineResult postProcess(EngineResult result, double fontAdjust) {
            return result.mapString(s -> postProcessSvg(s, false, fontAdjust));
        }
    },
    DOT("dot", "dot", false, false),
    XDOT("xdot", "xdot", false, false),
    PLAIN("plain", "txt", false, false),
    PLAIN_EXT("plain-ext", "txt", false, false),
    PS("ps", "ps", false, false),
    PS2("ps2", "ps", false, false),
    JSON("json", "json", false, false),
    JSON0("json0", "json", false, false);

    private static final Logger LOG = LoggerFactory.getLogger(Format.class);
    private static final Pattern FONT_PATTERN = Pattern.compile("font-size=\"(.*?)\"");
    private static final Pattern SVG_PATTERN = Pattern.compile(
            "<svg width=\"(\\d+)p[tx]\" height=\"(\\d+)p[tx]\"(.*?)>\\R"
                    + "<g(.*?)transform=\"(scale\\(.*?\\))?", Pattern.DOTALL);
    final String vizName;
    final String fileExtension;
    final boolean image;
    final boolean svg;

    Format(String vizName, String fileExtension, boolean image, boolean svg) {
        this.vizName = vizName;
        this.fileExtension = fileExtension;
        this.image = image;
        this.svg = svg;
    }

    EngineResult postProcess(EngineResult result, double fontAdjust) {
        return result;
    }

    private static String postProcessSvg(String result, boolean prefix, double fontAdjust) {
        final String unprefixed = prefix ? withoutPrefix(result) : result;
        final String scaled = adjustScale(unprefixed);
        return fontAdjust == 1 ? scaled : fontAdjusted(scaled, fontAdjust);
    }

    private static String adjustScale(String svg) {
        final Matcher m = SVG_PATTERN.matcher(svg);
        if (!m.find()) {
            LOG.warn("Generated SVG has not the expected format. There might be image size problems.");
            return svg;
        }
        return m.replaceFirst("<svg width=\"$1px\" height=\"$2px\"$3><g$4transform=\"");
    }

    private static String withoutPrefix(String svg) {
        final int pos = svg.indexOf("<svg ");
        return pos < 0 ? svg : svg.substring(pos);
    }

    private static String fontAdjusted(String svg, double fontAdjust) {
        final Matcher m = FONT_PATTERN.matcher(svg);
        final StringBuffer s = new StringBuffer();
        while (m.find()) {
            String rep;
            try {
                rep = "font-size=\"" + Double.parseDouble(m.group(1)) * fontAdjust + "\"";
            } catch (NumberFormatException e) {
                rep = m.group();
            }
            m.appendReplacement(s, rep);
        }
        m.appendTail(s);
        return s.toString();
    }
}
