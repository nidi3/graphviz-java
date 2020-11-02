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

import org.junit.jupiter.api.Test;

import static guru.nidi.graphviz.attribute.Attributes.attr;
import static guru.nidi.graphviz.attribute.Attributes.attrs;
import static guru.nidi.graphviz.attribute.Label.Justification.*;
import static guru.nidi.graphviz.attribute.Label.Location.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LabelTest {
    private final Label label = Label.of("hula");

    @Test
    void simple() {
        assertEquals(attrs(attr("label", label)), attrs(label));
    }

    @Test
    void decorated() {
        assertEquals(attrs(attr("label", label.decorated()), attr("decorate", true)), attrs(label.decorated()));
    }

    @Test
    void external() {
        assertEquals(attrs(attr("xlabel", label.external())), attrs(label.external()));
    }

    @Test
    void floating() {
        assertEquals(attrs(attr("label", label.floating()), attr("labelfloat", true)), attrs(label.floating()));
    }

    @Test
    void justify() {
        assertEquals(attrs(attr("label", label.justify(LEFT)), attr("labeljust", "l")), attrs(label.justify(LEFT)));
        assertEquals(attrs(attr("label", label.justify(MIDDLE))), attrs(label.justify(MIDDLE)));
        assertEquals(attrs(attr("label", label.justify(RIGHT)), attr("labeljust", "r")), attrs(label.justify(RIGHT)));
    }

    @Test
    void locate() {
        assertEquals(attrs(attr("label", label.locate(TOP)), attr("labelloc", "t")), attrs(label.locate(TOP)));
        assertEquals(attrs(attr("label", label.locate(CENTER))), attrs(label.locate(CENTER)));
        assertEquals(attrs(attr("label", label.locate(BOTTOM)), attr("labelloc", "b")), attrs(label.locate(BOTTOM)));
    }

    @Test
    void nodeName() {
        assertEquals("\\N", Label.nodeName().value());
    }

    @Test
    void graphName() {
        assertEquals("\\G", Label.graphName().value());
    }

    @Test
    void headName() {
        assertEquals("\\H", Label.headName().value());
    }

    @Test
    void tailName() {
        assertEquals("\\T", Label.tailName().value());
    }

    @Test
    void lines() {
        assertEquals("a\nb\nc\n", Label.lines("a", "b", "c").value);
        assertEquals("a\\lb\\lc\\l", Label.lines(LEFT, "a", "b", "c").value);
        assertEquals("a\\rb\\rc\\r", Label.lines(RIGHT, "a", "b", "c").value);
    }

    @Test
    void htmlLines() {
        assertEquals("a<br/>b<br/>", Label.htmlLines("a", "b").value);
        assertEquals("a<br align=\"left\"/>b<br align=\"left\"/>", Label.htmlLines(LEFT, "a", "b").value);
        assertEquals("a<br align=\"right\"/>b<br align=\"right\"/>", Label.htmlLines(RIGHT, "a", "b").value);
    }

    @Test
    void html() {
        assertEquals("<html>", Label.html("html").serialized());
    }

    @Test
    void backslashAtEnd() {
        assertEquals("\"a\\\\\"", Label.of("a\\").serialized());
        assertEquals("\"a\\\\\"", Label.of("a\\\\").serialized());
    }

    @Test
    void newlines() {
        assertEquals("\"1\\n2\\n3\\n4\\n\\n5\"", Label.of("1\n2\r3\r\n4\n\r5").serialized());
    }

    @Test
    void markdown() {
        assertEquals("a<br/>b <b>b<i>ol</i>d</b> <s>s</s> <sub>sub</sub> <sup>sup</sup> <o>o</o> *",
                Label.markdown("a\nb **b*ol*d** ~~s~~ __sub__ ^^sup^^ ^o^ \\*").value);
    }

    @Test
    void raw() {
        assertEquals("\"hula\"", Label.raw("hula").serialized());
        assertEquals("<hula>", Label.raw("<hula>").serialized());
        assertEquals("<hula<b>bold</b>**star**>", Label.raw("<hula<b>bold</b>**star**>").serialized());
        assertEquals("<hula<b>bold</b>>", Label.raw("<hula**bold**>").serialized());
    }

    @Test
    void head() {
        assertEquals(attrs(attr("headlabel", EndLabel.head(Label.of("hula"), null, null))),
                attrs(label.head()));
        assertEquals(attrs(attr("headlabel", EndLabel.head(Label.of("hula"), 1.2, 3.4)), attr("labelangle", 1.2), attr("labeldistance", 3.4)),
                attrs(label.head(1.2, 3.4)));
    }

    @Test
    void tail() {
        assertEquals(attrs(attr("taillabel", EndLabel.tail(Label.of("hula"), null, null))),
                attrs(label.tail()));
        assertEquals(attrs(attr("taillabel", EndLabel.tail(Label.of("hula"), 1.2, 3.4)), attr("labelangle", 1.2), attr("labeldistance", 3.4)),
                attrs(label.tail(1.2, 3.4)));
    }
}
