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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class ArrowTest {
    @Test
    public void simple() {
        assertArrow("dot", Arrow.DOT);
    }

    @Test
    public void and() {
        assertArrow("boxdot", Arrow.DOT.and(Arrow.BOX));
    }

    @Test
    public void left() {
        assertArrow("lbox", Arrow.BOX.left());
    }

    @Test
    public void right() {
        assertArrow("rbox", Arrow.BOX.right());
    }

    @Test
    public void rightAfterLeft() {
        assertArrow("rbox", Arrow.BOX.left().right());
    }

    @Test
    public void open() {
        assertArrow("obox", Arrow.BOX.open());
    }

    @Test
    public void openAfterOpen() {
        assertArrow("obox", Arrow.BOX.open().open());
    }

    @Test
    public void openAfterLeft() {
        assertArrow("olbox", Arrow.BOX.left().open());
    }

    @Test
    public void leftAfterOpen() {
        assertArrow("olbox", Arrow.BOX.open().left());
    }

    @Test
    public void leftAfterOpenRight() {
        assertArrow("olbox", Arrow.BOX.open().right().left());
    }

    @Test
    public void tail() {
        assertEquals("arrowtail", Arrow.BOX.tail().key);
    }

    private void assertArrow(String value, Arrow arrow) {
        assertEquals("arrowhead", arrow.key);
        assertEquals(value, arrow.value);
    }
}
