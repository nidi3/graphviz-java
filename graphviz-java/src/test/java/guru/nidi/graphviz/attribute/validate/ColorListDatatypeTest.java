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

class ColorListDatatypeTest {

    @Test
    void colorListOk() {
        assertNull(new ColorListDatatype().validate("#12af 44"));
        assertNull(new ColorListDatatype().validate("#12af 44;.5"));
        assertNull(new ColorListDatatype().validate("#12af 44:blu"));
        assertNull(new ColorListDatatype().validate("#12af 44;1:blu;0"));
    }

    @Test
    void colorListNok() {
        assertMessage("'#12' is not a valid color.",
                new ColorListDatatype().validate("#12;1"));
        assertMessage("'#121314;a': a is not a valid number.",
                new ColorListDatatype().validate("#121314;a"));
        assertMessage("'#121314;': missing color factor after ';'.",
                new ColorListDatatype().validate("#121314;"));
        assertMessage("'#121314;2': 2 is not between 0 and 1.",
                new ColorListDatatype().validate("#121314;2"));
        assertMessage("'blu;1:red;1': The sum of the factors is greater than 1.",
                new ColorListDatatype().validate("blu;1:red;1"));
    }

    private void assertMessage(String message, ValidatorMessage actual) {
        assertEquals(new ValidatorMessage(ERROR, "", message), actual);
    }
}
