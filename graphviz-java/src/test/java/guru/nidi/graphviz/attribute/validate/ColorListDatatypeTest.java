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
        assertMessage("has the invalid color value '#12'.",
                new ColorListDatatype().validate("#12;1"));
        assertMessage("has the invalid color factor 'a' in '#121314;a'.",
                new ColorListDatatype().validate("#121314;a"));
        assertMessage("is missing color factor after ';' in '#121314;'.",
                new ColorListDatatype().validate("#121314;"));
        assertMessage("has a color factor '2' not between 0 and 1 in '#121314;2'.",
                new ColorListDatatype().validate("#121314;2"));
        assertMessage("has a sum of factors 2.0 greater than 1 in 'blu;1:red;1'.",
                new ColorListDatatype().validate("blu;1:red;1"));
    }

    private void assertMessage(String message, ValidatorMessage actual) {
        assertEquals(new ValidatorMessage(ERROR, "", message), actual);
    }
}
