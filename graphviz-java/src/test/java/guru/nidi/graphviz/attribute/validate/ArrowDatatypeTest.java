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

import static guru.nidi.graphviz.attribute.validate.ValidatorMessage.Severity.ERROR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ArrowDatatypeTest {
    @Test
    void arrowTypeOk() {
        assertNull(new ArrowDatatype().validate("box"));
        assertNull(new ArrowDatatype().validate("obox"));
        assertNull(new ArrowDatatype().validate("lbox"));
        assertNull(new ArrowDatatype().validate("olbox"));
    }

    @Test
    void arrowTypeWrongShape() {
        assertMessage("has unknown shape 'hula'.", new ArrowDatatype().validate("ohula"));
    }

    @Test
    void arrowTypeWrongPrefix() {
        assertMessage("has not allowed 'o' prefix in 'ocrow'.",
                new ArrowDatatype().validate("ocrow"));
        assertMessage("has not allowed 'l' prefix in 'ldot'.",
                new ArrowDatatype().validate("ldot"));
    }

    @Test
    void arrowTypeTooManyShapes() {
        assertMessage("has more than 4 shapes in 'dotcrowboxdotcrow'.",
                new ArrowDatatype().validate("dotcrowboxdotcrow"));
    }

    @Test
    void arrowTypeNone() {
        assertNull(new ArrowDatatype().validate("none"));
        assertMessage("has 'none' at last position in 'dotnone'.",
                new ArrowDatatype().validate("dotnone"));
    }

    private void assertMessage(String message, ValidatorMessage actual) {
        assertEquals(new ValidatorMessage(ERROR, "", message), actual);
    }
}
