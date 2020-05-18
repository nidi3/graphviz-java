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

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class StringFunctions {
    private StringFunctions() {
    }

    static String replaceRegex(String src, Pattern pattern, Function<String, String> replacer) {
        final Matcher matcher = pattern.matcher(src);
        final StringBuilder s = new StringBuilder();
        int last = 0;
        while (matcher.find()) {
            final String attr = matcher.group(1);
            s.append(src, last, matcher.start(1));
            s.append(replacer.apply(attr));
            last = matcher.end(1);
        }
        return s.append(src.substring(last)).toString();
    }

    static String escapeJs(String js) {
        return js.replace("\\", "\\\\").replace("'", "\\'").replaceAll("\\R", "\\\\n");
    }

    static String replaceSubSpaces(String s) {
        final char[] cs = s.toCharArray();
        for (int i = 0; i < cs.length; i++) {
            if (cs[i] < ' ' && cs[i] != '\t' && cs[i] != '\r' && cs[i] != '\n') {
                cs[i] = ' ';
            }
        }
        return new String(cs);
    }

    static String replaceNonWordChars(String s) {
        final char[] cs = s.toCharArray();
        for (int i = 0; i < cs.length; i++) {
            if (!(cs[i] == '.' || (cs[i] >= '0' && cs[i] <= '9')
                    || (cs[i] >= 'A' && cs[i] <= 'Z') || (cs[i] >= 'a' && cs[i] <= 'z'))) {
                cs[i] = '-';
            }
        }
        return new String(cs);
    }

}
