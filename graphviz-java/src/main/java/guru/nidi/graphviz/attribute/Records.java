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
package guru.nidi.graphviz.attribute;

import static guru.nidi.graphviz.attribute.Attributes.attr;
import static guru.nidi.graphviz.attribute.Attributes.attrs;

public final class Records {
    private static final String
            SHAPE = "shape",
            RECORD = "record",
            M_RECORD = "Mrecord",
            LABEL = "label";

    private Records() {
    }

    public static Attributes<ForNode> label(String label) {
        return attrs(attr(SHAPE, RECORD), attr(LABEL, label));
    }

    public static Attributes<ForNode> mLabel(String label) {
        return attrs(attr(SHAPE, M_RECORD), attr(LABEL, label));
    }

    public static Attributes<ForNode> of(String... recs) {
        return attrs(attr(SHAPE, RECORD), attr(LABEL, String.join("|", recs)));
    }

    public static Attributes<ForNode> mOf(String... recs) {
        return attrs(attr(SHAPE, M_RECORD), attr(LABEL, String.join("|", recs)));
    }

    public static String rec(String tag, String label) {
        return "<" + tag + ">" + rec(label);
    }

    public static String rec(String label) {
        return label.replace("{", "\\{").replace("}", "\\}")
                .replace("<", "\\<").replace(">", "\\>")
                .replace("|", "\\|").replace(" ", "\\ ");
    }

    public static String turn(String... records) {
        return "{" + String.join("|", records) + "}";
    }

}
