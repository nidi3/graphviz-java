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

import java.util.Arrays;

import static java.util.stream.Collectors.joining;

public final class Records {
    private static final String SHAPE = "shape";
    private static final String RECORD = "record";
    private static final String LABEL = "label";

    private Records() {
    }

    public static Attribute label(String label) {
        return new MapAttribute(SHAPE, RECORD, LABEL, label);
    }

    public static Attribute mLabel(String label) {
        return new MapAttribute(SHAPE, "Mrecord", LABEL, label);
    }

    public static Attribute of(String... recs) {
        return new MapAttribute(SHAPE, RECORD, LABEL, Arrays.stream(recs).collect(joining("|")));
    }

    public static Attribute mOf(String... recs) {
        return new MapAttribute(SHAPE, "Mrecord", LABEL, Arrays.stream(recs).collect(joining("|")));
    }

    public static String rec(String tag, String label) {
        return "<" + tag + ">" + rec(label);
    }

    public static String rec(String label) {
        return label.replace("{", "\\{").replace("}", "\\}").replace("<", "\\<").replace(">", "\\>").replace("|", "\\|").replace(" ", "\\ ");
    }

    public static String turn(String... records) {
        return "{" + Arrays.stream(records).collect(joining("|")) + "}";
    }

}
