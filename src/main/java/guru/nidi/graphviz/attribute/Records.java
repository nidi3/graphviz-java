/*
 * Copyright (C) 2015 Stefan Niederhauser (nidin@gmx.ch)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package guru.nidi.graphviz.attribute;

/**
 *
 */
public class Records {
    private Records() {
    }

    public static Attribute label(String label) {
        return new MapAttribute("shape", "record", "label", label);
    }

    public static Attribute mLabel(String label) {
        return new MapAttribute("shape", "Mrecord", "label", label);
    }

    public static Attribute of(String... recs) {
        return new MapAttribute("shape", "record", "label", join(recs, "|"));
    }

    public static Attribute mOf(String... recs) {
        return new MapAttribute("shape", "Mrecord", "label", join(recs, "|"));
    }

    public static String rec(String tag, String label) {
        return "<" + tag + ">" + rec(label);
    }

    public static String rec(String label) {
        return label.replace("{", "\\{").replace("}", "\\}").replace("<", "\\<").replace(">", "\\>").replace("|", "\\|").replace(" ", "\\ ");
    }

    public static String turn(String... records) {
        return "{" + join(records, "|") + "}";
    }

    private static String join(String[] ss, String delim) {
        String res = "";
        boolean first = true;
        for (final String s : ss) {
            if (first) {
                first = false;
            } else {
                res += delim;
            }
            res += s;
        }
        return res;
    }
}
