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
import static org.junit.jupiter.api.Assertions.assertEquals;

class RecordsTest {
    @Test
    void label() {
        assertEquals(attrs(attr("shape", "record"), attr("label", "label")), Records.label("label"));
    }

    @Test
    void mlabel() {
        assertEquals(attrs(attr("shape", "Mrecord"), attr("label", "label")), Records.mLabel("label"));
    }

    @Test
    void of() {
        assertEquals(attrs(attr("shape", "record"), attr("label", "a|b|c")), Records.of("a", "b", "c"));
    }

    @Test
    void mof() {
        assertEquals(attrs(attr("shape", "Mrecord"), attr("label", "a|b|c")), Records.mOf("a", "b", "c"));
    }

    @Test
    void rec() {
        assertEquals("l\\<a\\>\\ \\{b\\}\\ \\|\\ el", Records.rec("l<a> {b} | el"));
    }

    @Test
    void tagRec() {
        assertEquals("<tag>label", Records.rec("tag", "label"));
    }

    @Test
    void turn() {
        assertEquals("{<tag>label|hula}", Records.turn(Records.rec("tag", "label"), "hula"));
    }

}
