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

class ArrowTest {
    @Test
    void simple() {
        assertArrow("dot", Arrow.DOT);
    }

    @Test
    void and() {
        assertArrow("boxdot", Arrow.DOT.and(Arrow.BOX));
    }

    @Test
    void left() {
        assertArrow("lbox", Arrow.BOX.left());
    }

    @Test
    void right() {
        assertArrow("rbox", Arrow.BOX.right());
    }

    @Test
    void rightAfterLeft() {
        assertArrow("rbox", Arrow.BOX.left().right());
    }

    @Test
    void open() {
        assertArrow("obox", Arrow.BOX.open());
    }

    @Test
    void openAfterOpen() {
        assertArrow("obox", Arrow.BOX.open().open());
    }

    @Test
    void openAfterLeft() {
        assertArrow("olbox", Arrow.BOX.left().open());
    }

    @Test
    void leftAfterOpen() {
        assertArrow("olbox", Arrow.BOX.open().left());
    }

    @Test
    void leftAfterOpenRight() {
        assertArrow("olbox", Arrow.BOX.open().right().left());
    }

    @Test
    void tail() {
        assertEquals("arrowtail", Arrow.BOX.tail().key);
    }

    @Test
    void size() {
        assertEquals(attrs(attr("arrowhead", "box"), attr("arrowsize", 2d)),
                Arrow.BOX.size(2));
    }

    @Test
    void dir() {
        assertEquals(attrs(attr("arrowhead", "box"), attr("dir", "back")),
                Arrow.BOX.dir(Arrow.DirType.BACK));
    }

    @Test
    void config() {
        assertEquals(attrs(attr("arrowhead", "box"), attr("arrowsize", 2d), attr("dir", "back")),
                Arrow.BOX.config(2, Arrow.DirType.BACK));
    }

    private void assertArrow(String value, Arrow arrow) {
        assertEquals("arrowhead", arrow.key);
        assertEquals(value, arrow.value);
    }
}
