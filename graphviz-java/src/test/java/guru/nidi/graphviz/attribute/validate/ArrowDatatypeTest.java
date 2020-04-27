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
package guru.nidi.graphviz.attribute.validate;

import org.junit.jupiter.api.Test;

class ArrowDatatypeTest extends DatatypeTestBase {
    ArrowDatatypeTest() {
        super(new ArrowDatatype());
    }

    @Test
    void arrowTypeOk() {
        assertOk("box");
        assertOk("obox");
        assertOk("lbox");
        assertOk("olbox");
    }

    @Test
    void arrowTypeWrongShape() {
        assertMessage("has unknown shape 'hula'.", "ohula");
    }

    @Test
    void arrowTypeWrongPrefix() {
        assertMessage("has not allowed 'o' prefix in 'ocrow'.", "ocrow");
        assertMessage("has not allowed 'l' prefix in 'ldot'.", "ldot");
    }

    @Test
    void arrowTypeTooManyShapes() {
        assertMessage("has more than 4 shapes in 'dotcrowboxdotcrow'.", "dotcrowboxdotcrow");
    }

    @Test
    void arrowTypeNone() {
        assertOk("none");
        assertMessage("has 'none' at last position in 'dotnone'.", "dotnone");
    }
}
