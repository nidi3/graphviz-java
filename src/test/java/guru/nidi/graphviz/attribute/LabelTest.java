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
        assertEquals(attrs(attr("label", label), attr("decorate", true)), attrs(label.decorated()));
    }

    @Test
    void external() {
        assertEquals(attrs(attr("xlabel", label)), attrs(label.external()));
    }

    @Test
    void floating() {
        assertEquals(attrs(attr("label", label), attr("labelfloat", true)), attrs(label.floating()));
    }

    @Test
    void justify() {
        assertEquals(attrs(attr("label", label), attr("labeljust", "l")), attrs(label.justify(LEFT)));
        assertEquals(attrs(attr("label", label)), attrs(label.justify(MIDDLE)));
        assertEquals(attrs(attr("label", label), attr("labeljust", "r")), attrs(label.justify(RIGHT)));
    }

    @Test
    void locate() {
        assertEquals(attrs(attr("label", label), attr("labelloc", "t")), attrs(label.locate(TOP)));
        assertEquals(attrs(attr("label", label)), attrs(label.locate(CENTER)));
        assertEquals(attrs(attr("label", label), attr("labelloc", "b")), attrs(label.locate(BOTTOM)));
    }

}
