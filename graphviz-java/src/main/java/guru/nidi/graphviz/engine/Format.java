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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Format {
    PNG("svg", "png", true, true) {
        @Override
        String postProcess(String result, double fontAdjust) {
            return postProcessSvg(result, true, fontAdjust);
        }
    },

    SVG("svg", "svg", false, true) {
        @Override
        String postProcess(String result, double fontAdjust) {
            return postProcessSvg(result, true, fontAdjust);
        }
    },

    SVG_STANDALONE("svg", "svg", false, true) {
        @Override
        String postProcess(String result, double fontAdjust) {
            return postProcessSvg(result, false, fontAdjust);
        }
    },
    XDOT("xdot", "xdot", false, false),
    PLAIN("plain", "txt", false, false),
    PLAIN_EXT("plain-ext", "txt", false, false),
    PS("ps", "ps", false, false),
    PS2("ps2", "ps", false, false),
    JSON("json", "json", false, false),
    JSON0("json0", "json", false, false);

    private static final Pattern FONT_PATTERN = Pattern.compile("font-size=\"(.*?)\"");
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

    String postProcess(String result, double fontAdjust) {
        return result;
    }

    private static String postProcessSvg(String result, boolean prefix, double fontAdjust) {
        final String prefixed = prefix ? withoutPrefix(result) : result;
        return fontAdjust == 1 ? prefixed : fontAdjusted(prefixed, fontAdjust);
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
