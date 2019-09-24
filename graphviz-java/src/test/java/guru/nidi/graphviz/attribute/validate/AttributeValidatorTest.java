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

import static guru.nidi.graphviz.attribute.Attributes.attr;
import static guru.nidi.graphviz.attribute.Attributes.attrs;
import static guru.nidi.graphviz.attribute.validate.AttributeValidator.Scope.*;
import static guru.nidi.graphviz.attribute.validate.AttributeValidator.validate;
import static guru.nidi.graphviz.attribute.validate.ValidatorMessage.Severity.ERROR;
import static guru.nidi.graphviz.attribute.validate.ValidatorMessage.Severity.WARNING;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AttributeValidatorTest {

    @Test
    void ok() {
        assertEquals(emptyList(), validate(attrs(attr("Damping", 5)), GRAPH, null, null));
        assertEquals(emptyList(), validate(attrs(attr("Damping", 5)), GRAPH, "neato", null));
        assertEquals(emptyList(), validate(attrs(attr("URL", 5)), GRAPH, null, "svg"));
    }

    @Test
    void invalidEngine() {
        assertThrows(IllegalArgumentException.class, () -> validate(attrs(attr("Damping", 5)), GRAPH, "hula", null));
    }

    @Test
    void invalidFormat() {
        assertThrows(IllegalArgumentException.class, () -> validate(attrs(attr("Damping", 5)), GRAPH, null, "hula"));
    }

    @Test
    void wrongName() {
        assertEquals(asList(new ValidatorMessage(ERROR, "hula", "Attribute is unknown.")),
                validate(attrs(attr("hula", 5)), NODE, null, null));
    }

    @Test
    void wrongScope() {
        assertEquals(asList(new ValidatorMessage(ERROR, "Damping", "Attribute is not allowed for scope 'NODE'.")),
                validate(attrs(attr("Damping", 5)), NODE, null, null));
    }

    @Test
    void wrongEngine() {
        assertEquals(asList(new ValidatorMessage(ERROR, "Damping", "Attribute is not allowed for engine 'dot'.")),
                validate(attrs(attr("Damping", 5)), GRAPH, "dot", null));
    }

    @Test
    void wrongFormat() {
        assertEquals(asList(new ValidatorMessage(ERROR, "URL", "Attribute is not allowed for format 'cmap'.")),
                validate(attrs(attr("URL", 5)), GRAPH, null, "cmap"));
    }

    @Test
    void useDoubleDefault() {
        assertEquals(asList(new ValidatorMessage(WARNING, "Damping", "Attribute is set to its default value '0.99'.")),
                validate(attrs(attr("Damping", .99)), GRAPH, null, null));
        assertEquals(asList(new ValidatorMessage(WARNING, "Damping", "Attribute is set to its default value '0.99'.")),
                validate(attrs(attr("Damping", .99000001)), GRAPH, null, null));
        assertEquals(asList(new ValidatorMessage(WARNING, "Damping", "Attribute is set to its default value '0.99'.")),
                validate(attrs(attr("Damping", ".99")), GRAPH, null, null));
    }

    @Test
    void useIntDefault() {
        assertEquals(asList(new ValidatorMessage(WARNING, "dim", "Attribute is set to its default value '2'.")),
                validate(attrs(attr("dim", 2)), GRAPH, null, null));
        assertEquals(asList(new ValidatorMessage(WARNING, "dim", "Attribute is set to its default value '2'.")),
                validate(attrs(attr("dim", "+2")), GRAPH, null, null));
    }

    @Test
    void useBoolDefault() {
        assertEquals(asList(new ValidatorMessage(WARNING, "headclip", "Attribute is set to its default value 'true'.")),
                validate(attrs(attr("headclip", true)), EDGE, null, null));
        assertEquals(asList(new ValidatorMessage(WARNING, "headclip", "Attribute is set to its default value 'true'.")),
                validate(attrs(attr("headclip", "truE")), EDGE, null, null));
        assertEquals(asList(new ValidatorMessage(WARNING, "headclip", "Attribute is set to its default value 'true'.")),
                validate(attrs(attr("headclip", "Yes")), EDGE, null, null));
        assertEquals(asList(
                new ValidatorMessage(WARNING, "headclip", "Attribute is set to its default value 'true'."),
                new ValidatorMessage(WARNING, "headclip", "Using numerical value '42' as boolean.")),
                validate(attrs(attr("headclip", "42")), EDGE, null, null));
        assertEquals(asList(new ValidatorMessage(WARNING, "center", "Attribute is set to its default value 'false'.")),
                validate(attrs(attr("center", false)), GRAPH, null, null));
        assertEquals(asList(new ValidatorMessage(WARNING, "center", "Attribute is set to its default value 'false'.")),
                validate(attrs(attr("center", "False")), GRAPH, null, null));
        assertEquals(asList(new ValidatorMessage(WARNING, "center", "Attribute is set to its default value 'false'.")),
                validate(attrs(attr("center", "NO")), GRAPH, null, null));
        assertEquals(asList(
                new ValidatorMessage(WARNING, "center", "Attribute is set to its default value 'false'."),
                new ValidatorMessage(WARNING, "center", "Using numerical value '0' as boolean.")),
                validate(attrs(attr("center", "0")), GRAPH, null, null));
    }

    @Test
    void minimum() {
        assertEquals(asList(), validate(attrs(attr("Damping", 0)), GRAPH, null, null));
        assertEquals(asList(new ValidatorMessage(WARNING, "Damping", "Attribute has a minimum of '0.0' but is set to '-0.1'.")),
                validate(attrs(attr("Damping", -.1)), GRAPH, null, null));
        assertEquals(asList(new ValidatorMessage(WARNING, "dim", "Attribute has a minimum of '2.0' but is set to '1'.")),
                validate(attrs(attr("dim", 1)), GRAPH, null, null));
    }

    @Test
    void invalidInt() {
        assertEquals(asList(new ValidatorMessage(ERROR, "dim", "'a' is not a valid integer.")),
                validate(attrs(attr("dim", "a")), GRAPH, null, null));
    }

    @Test
    void invalidFloat() {
        assertEquals(asList(new ValidatorMessage(ERROR, "Damping", "'1.2.3' is not a valid float.")),
                validate(attrs(attr("Damping", "1.2.3")), GRAPH, null, null));
    }

    @Test
    void floatList() {
        assertEquals(asList(), validate(attrs(attr("ranksep", "1.2 :-4:5e2")), GRAPH, null, null));
        assertEquals(asList(new ValidatorMessage(ERROR, "ranksep", "'1.2 :-4;5e2' is not valid for any of the types 'float, list of floats'.")),
                validate(attrs(attr("ranksep", "1.2 :-4;5e2")), GRAPH, null, null));
    }

    @Test
    void point() {
        assertEquals(asList(), validate(attrs(attr("head_lp", "1.2, 4.5")), EDGE, null, null));
        assertEquals(asList(), validate(attrs(attr("head_lp", "1.2, 4.5!")), EDGE, null, null));
        assertEquals(asList(), validate(attrs(attr("head_lp", "1.2, 4.5,5!")), EDGE, null, null));
        assertEquals(asList(new ValidatorMessage(ERROR, "head_lp", "'1.2' is not a valid point.")),
                validate(attrs(attr("head_lp", "1.2")), EDGE, null, null));
        assertEquals(asList(new ValidatorMessage(ERROR, "head_lp", "'1.2,3,4,5' is not a valid point.")),
                validate(attrs(attr("head_lp", "1.2,3,4,5")), EDGE, null, null));
    }

    @Test
    void pointList() {
        assertEquals(asList(), validate(attrs(attr("vertices", "1.2,4.5")), NODE, null, null));
        assertEquals(asList(), validate(attrs(attr("vertices", "1.2,4.5!  3,4,5")), NODE, null, null));
        assertEquals(asList(new ValidatorMessage(ERROR, "vertices", "'1.2' is not a valid list of points.")),
                validate(attrs(attr("vertices", "1.2")), NODE, null, null));
        assertEquals(asList(new ValidatorMessage(ERROR, "vertices", "'1.2,3,4,5' is not a valid list of points.")),
                validate(attrs(attr("vertices", "1.2,3,4,5")), NODE, null, null));
    }
}
