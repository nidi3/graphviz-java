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

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleLabelTest {
    @Test
    void simple() {
        assertEquals("\"hula\"", SimpleLabel.of("hula").serialized());
    }

    @Test
    void html() {
        assertEquals("<hula>", new SimpleLabel("hula", true).serialized());

    }

    @Test
    void ofSimpleLabel() {
        assertEquals("\"hula\"", SimpleLabel.of(SimpleLabel.of("hula")).serialized());
    }

    @Test
    void ofObject() {
        assertEquals("\"1\"", SimpleLabel.of(1).serialized());
    }

}
