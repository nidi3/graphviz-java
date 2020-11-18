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

class StyleTest {
    @Test
    void simple() {
        assertEquals(attrs(attr("style", "bold")), attrs(Style.BOLD));
    }

    @Test
    void tapered() {
        assertEquals(attrs(attr("style", "tapered"), attr("penwidth", 5.0), attr("dir", "both")),
                attrs(Style.tapered(5, Arrow.DirType.BOTH)));
    }

    @Test
    void wedged() {
        assertEquals(attrs(attr("style", "wedged"), attr("color", "red:green")),
                attrs(Style.wedged(Color.RED, Color.GREEN)));
    }

    @Test
    void striped() {
        assertEquals(attrs(attr("style", "striped"), attr("color", "red:green")),
                attrs(Style.striped(Color.RED, Color.GREEN)));
    }

    @Test
    void lineWidth() {
        assertEquals(attrs(attr("penwidth", 5.5), attr("style", "solid,rounded")),
                attrs(Style.lineWidth(5.5), Style.combine(Style.SOLID, Style.ROUNDED)));
    }

}
