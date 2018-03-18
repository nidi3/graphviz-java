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

class EndLabelTest {
    @Test
    void simpleHead() {
        final EndLabel head = EndLabel.head(SimpleLabel.of("hula"), null, null);
        assertEquals(attr("headlabel", head), attrs(head));
    }

    @Test
    void simpleTail() {
        final EndLabel tail = EndLabel.tail(SimpleLabel.of("hula"), null, null);
        assertEquals(attr("taillabel", tail), attrs(tail));
    }

    @Test
    void ofHtml() {
        final EndLabel tail = EndLabel.tail(Label.html("hula"), null, null);
        assertEquals(attr("taillabel", tail), attrs(tail));
    }

    @Test
    void attrHead() {
        final EndLabel head = EndLabel.head(Label.of("hula"), 1d, 2d);
        assertEquals(attrs(attr("headlabel", head), attr("labelangle", 1d), attr("labeldistance", 2d)),
                attrs(head));
    }

    @Test
    void attrTail() {
        final EndLabel tail = EndLabel.tail(Label.of("hula"), 1d, 2d);
        assertEquals(attrs(attr("taillabel", tail), attr("labelangle", 1d), attr("labeldistance", 2d)),
                attrs(tail));
    }


}
