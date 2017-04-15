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

public enum Format {
    PNG("svg", true, true) {
        String postProcess(String result) {
            return postProcessSvg(result);
        }
    },

    SVG("svg", false, true) {
        String postProcess(String result) {
            return postProcessSvg(result);
        }
    },

    SVG_STANDALONE("svg", false, true),
    XDOT("xdot", false, false),
    PLAIN("plain", false, false),
    PS("ps", false, false),
    JSON("json", false, false);

    final String vizName;
    final boolean image;
    final boolean svg;

    Format(String vizName, boolean image, boolean svg) {
        this.vizName = vizName;
        this.image = image;
        this.svg = svg;
    }

    String postProcess(String result) {
        return result;
    }

    private static String postProcessSvg(String result) {
        final int pos = result.indexOf("<svg ");
        return pos < 0 ? result : result.substring(pos);
    }
}
