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

class FontTest {
    @Test
    void name() {
        assertEquals(attr("fontname", "Arial"), Font.name("Arial"));
    }

    @Test
    void size() {
        assertEquals(attr("fontsize", 12), Font.size(12));
    }

    @Test
    void config() {
        assertEquals(attrs(attr("fontname", "Arial"), attr("fontsize", 12)), Font.config("Arial", 12));
    }
}
