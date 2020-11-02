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

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.stream.Stream;

import static guru.nidi.graphviz.attribute.Label.Justification.*;
import static guru.nidi.graphviz.attribute.Label.Location.BOTTOM;
import static guru.nidi.graphviz.attribute.Label.Location.TOP;
import static java.util.stream.Collectors.joining;

public final class Label extends SimpleLabel implements Attributes<ForAll> {
    public static final String NODE_NAME = "\\N";
    public static final String GRAPH_NAME = "\\G";
    public static final String HEAD_NAME = "\\H";
    public static final String TAIL_NAME = "\\T";

    public enum Justification {
        LEFT, MIDDLE, RIGHT
    }

    public enum Location {
        TOP, CENTER, BOTTOM
    }

    private final boolean external;
    private final boolean floating;
    private final boolean decorated;
    @Nullable
    private final Justification just;
    @Nullable
    private final Location loc;

    private Label(String value, boolean html, boolean external, boolean floating, boolean decorated,
                  @Nullable Justification just, @Nullable Location loc) {
        super(value, html);
        this.external = external;
        this.floating = floating;
        this.decorated = decorated;
        this.just = just;
        this.loc = loc;
    }

    /**
     * Create a simple label. Create newlines with \n.
     *
     * @param value the label text
     * @return the Label
     */
    public static Label of(String value) {
        return new Label(value, false, false, false, false, null, null);
    }

    /**
     * Create a label of the node name (works only as a node attribute).
     *
     * @return the Label
     */
    public static Label nodeName() {
        return of(NODE_NAME);
    }

    /**
     * Create a label of the graph name.
     *
     * @return the Label
     */
    public static Label graphName() {
        return of(GRAPH_NAME);
    }

    /**
     * Create a label of the link head's node name (works only as a link attribute).
     *
     * @return the Label
     */
    public static Label headName() {
        return of(HEAD_NAME);
    }

    /**
     * Create a label of the link tail's node name (works only as a link attribute).
     *
     * @return the Label
     */
    public static Label tailName() {
        return of(TAIL_NAME);
    }

    /**
     * Create a simple multiline label.
     *
     * @param lines the text lines
     * @return the Label
     */
    public static Label lines(String... lines) {
        return lines(Justification.MIDDLE, lines);
    }

    /**
     * Create a simple multiline label.
     *
     * @param just  the text justification
     * @param lines the text lines
     * @return the Label
     */
    public static Label lines(Justification just, String... lines) {
        final String sep = just == LEFT ? "\\l" : just == RIGHT ? "\\r" : "\n";
        final String value = Stream.of(lines).map(line -> line + sep).collect(joining());
        return of(value);
    }

    /**
     * Create a HTML label.
     *
     * @param value the HTML code
     * @return the Label
     * @see <a href="http://www.graphviz.org/doc/info/shapes.html#html">www.graphviz.org</a>
     */
    public static Label html(String value) {
        return new Label(value, true, false, false, false, null, null);
    }

    /**
     * Create a multiline HTML label.
     *
     * @param lines the lines in HTML format
     * @return the Label
     * @see <a href="http://www.graphviz.org/doc/info/shapes.html#html">www.graphviz.org</a>
     */
    public static Label htmlLines(String... lines) {
        return htmlLines(MIDDLE, lines);
    }

    /**
     * Create a multiline HTML label.
     *
     * @param just  the test justification
     * @param lines the lines in HTML format
     * @return the Label
     * @see <a href="http://www.graphviz.org/doc/info/shapes.html#html">www.graphviz.org</a>
     */
    public static Label htmlLines(Justification just, String... lines) {
        final String sep = just == LEFT ? "<br align=\"left\"/>" : just == RIGHT ? "<br align=\"right\"/>" : "<br/>";
        final String value = Stream.of(lines).map(line -> line + sep).collect(joining());
        return html(value);
    }

    /**
     * Create a HTML label from markdown. The following patterns are allowed:<br>
     * \n newline, **bold**, *italics*, ~~strike through~~, _underlined_, ^overlined^, __subscript__, ^^superscript^^.
     *
     * @param value the markdown code
     * @return the Label
     */
    public static Label markdown(String value) {
        return html(replaceMd(replaceMd(replaceMd(replaceMd(replaceMd(replaceMd(replaceMd(value.replace("\n", "<br/>"),
                "\\*\\*", "b"),
                "\\*", "i"),
                "~~", "s"),
                "__", "sub"),
                "_", "u"),
                "\\^\\^", "sup"),
                "\\^", "o")
                .replaceAll("\\\\([*~_^])", "$1"));
    }

    private static String replaceMd(String s, String from, String to) {
        return s.replaceAll("([^\\\\])?" + from + "(.*?[^\\\\])" + from, "$1<" + to + ">$2</" + to + ">");
    }

    /**
     * Create either a simple, HTML or markdown label.
     * If the value is not surrounded by &lt; and &gt;, a simple Label is created.
     * Otherwise if value contains some HTML tags, a HTML label is created.
     * Otherwise a markdown label is created.
     *
     * @param value the raw label
     * @return the Label
     */
    public static Label raw(String value) {
        final boolean isTagged = value.startsWith("<") && value.endsWith(">");
        if (!isTagged) {
            return of(value);
        }
        final String untagged = value.substring(1, value.length() - 1);
        final boolean hasTags = value.contains("/>") || value.contains("</");
        return hasTags ? html(untagged) : markdown(untagged);
    }

    public EndLabel head() {
        return EndLabel.head(this, null, null);
    }

    public EndLabel head(double angle, double distance) {
        return EndLabel.head(this, angle, distance);
    }

    public EndLabel tail() {
        return EndLabel.tail(this, null, null);
    }

    public EndLabel tail(double angle, double distance) {
        return EndLabel.tail(this, angle, distance);
    }

    public Label external() {
        return new Label(value, html, true, floating, decorated, just, loc);
    }

    public Label floating() {
        return new Label(value, html, external, true, decorated, just, loc);
    }

    public Label decorated() {
        return new Label(value, html, external, floating, true, just, loc);
    }

    public Label justify(Justification just) {
        return new Label(value, html, external, floating, decorated, just, loc);
    }

    public Label locate(Location loc) {
        return new Label(value, html, external, floating, decorated, just, loc);
    }

    public boolean isExternal() {
        return external;
    }

    @Override
    public Attributes<? super ForAll> applyTo(MapAttributes<? super ForAll> attributes) {
        attributes.add(external ? "xlabel" : "label", this);
        if (floating) {
            attributes.add("labelfloat", true);
        }
        if (decorated) {
            attributes.add("decorate", true);
        }
        if (just == LEFT) {
            attributes.add("labeljust", "l");
        }
        if (just == RIGHT) {
            attributes.add("labeljust", "r");
        }
        if (loc == TOP) {
            attributes.add("labelloc", "t");
        }
        if (loc == BOTTOM) {
            attributes.add("labelloc", "b");
        }
        return attributes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final Label label = (Label) o;
        return external == label.external
                && floating == label.floating
                && decorated == label.decorated
                && just == label.just
                && loc == label.loc;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), external, floating, decorated, just, loc);
    }

    @Override
    public String toString() {
        return value;
    }
}
