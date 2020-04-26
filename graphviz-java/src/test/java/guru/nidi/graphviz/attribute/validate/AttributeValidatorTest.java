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

import guru.nidi.graphviz.attribute.Attributes;
import guru.nidi.graphviz.attribute.For;
import guru.nidi.graphviz.attribute.validate.ValidatorMessage.Severity;
import org.junit.jupiter.api.Test;

import java.util.List;

import static guru.nidi.graphviz.attribute.Attributes.attr;
import static guru.nidi.graphviz.attribute.Attributes.attrs;
import static guru.nidi.graphviz.attribute.validate.AttributeValidator.Scope.*;
import static guru.nidi.graphviz.attribute.validate.ValidatorEngine.*;
import static guru.nidi.graphviz.attribute.validate.ValidatorFormat.*;
import static guru.nidi.graphviz.attribute.validate.ValidatorMessage.Severity.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AttributeValidatorTest {
    @Test
    void ok() {
        assertOk(validate(attr("Damping", 5), GRAPH, UNKNOWN_ENGINE, UNKNOWN_FORMAT));
        assertOk(validate(attr("Damping", 5), GRAPH, NEATO, UNKNOWN_FORMAT));
        assertOk(validate(attr("URL", 5), GRAPH, UNKNOWN_ENGINE, SVG));
    }

    @Test
    void wrongName() {
        assertMessage(ERROR, "hula", "Attribute is unknown.", validate(attr("hula", 5), NODE));
    }

    @Test
    void wrongScope() {
        assertMessage(ERROR, "Damping", "Attribute is not allowed for nodes.",
                validate(attr("Damping", 5), NODE));
    }

    @Test
    void wrongEngine() {
        assertMessage(ERROR, "Damping", "Attribute is not allowed for engine 'DOT'.",
                validate(attr("Damping", 5), GRAPH, DOT, UNKNOWN_FORMAT));
    }

    @Test
    void wrongFormat() {
        assertMessage(ERROR, "URL", "Attribute is not allowed for format 'CMAP'.",
                validate(attr("URL", 5), GRAPH, null, CMAP));
    }

    @Test
    void useDoubleDefault() {
        assertMessage(INFO, "Damping", "Attribute is set to its default value '0.99'.",
                validate(attr("Damping", .99), GRAPH));
        assertMessage(INFO, "Damping", "Attribute is set to its default value '0.99'.",
                validate(attr("Damping", .99000001), GRAPH));
        assertMessage(INFO, "Damping", "Attribute is set to its default value '0.99'.",
                validate(attr("Damping", ".99"), GRAPH));
    }

    @Test
    void useIntDefault() {
        assertMessage(INFO, "dim", "Attribute is set to its default value '2'.", validate(attr("dim", 2), GRAPH));
        assertMessage(INFO, "dim", "Attribute is set to its default value '2'.", validate(attr("dim", "+2"), GRAPH));
    }

    @Test
    void useBoolDefault() {
        assertMessage(INFO, "headclip", "Attribute is set to its default value 'true'.",
                validate(attr("headclip", true), EDGE));
        assertMessage(INFO, "headclip", "Attribute is set to its default value 'true'.",
                validate(attr("headclip", "truE"), EDGE));
        assertMessage(INFO, "headclip", "Attribute is set to its default value 'true'.",
                validate(attr("headclip", "Yes"), EDGE));
        assertEquals(asList(
                new ValidatorMessage(INFO, "headclip", "Attribute is set to its default value 'true'."),
                new ValidatorMessage(WARN, "headclip", "Using numerical value '42' as boolean.")),
                validate(attr("headclip", "42"), EDGE));
        assertMessage(INFO, "center", "Attribute is set to its default value 'false'.",
                validate(attr("center", false), GRAPH));
        assertMessage(INFO, "center", "Attribute is set to its default value 'false'.",
                validate(attr("center", "False"), GRAPH));
        assertMessage(INFO, "center", "Attribute is set to its default value 'false'.",
                validate(attr("center", "NO"), GRAPH));
        assertEquals(asList(
                new ValidatorMessage(INFO, "center", "Attribute is set to its default value 'false'."),
                new ValidatorMessage(WARN, "center", "Using numerical value '0' as boolean.")),
                validate(attr("center", "0"), GRAPH));
    }

    @Test
    void minimum() {
        assertOk(validate(attr("Damping", 0), GRAPH));
        assertMessage(WARN, "Damping", "Attribute has a minimum of '0.0' but is set to '-0.1'.",
                validate(attr("Damping", -.1), GRAPH));
        assertMessage(WARN, "dim", "Attribute has a minimum of '2.0' but is set to '1'.",
                validate(attr("dim", 1), GRAPH));
    }

    @Test
    void invalidInt() {
        assertMessage(ERROR, "dim", "'a' is not a valid integer.", validate(attr("dim", "a"), GRAPH));
    }

    @Test
    void invalidFloat() {
        assertMessage(ERROR, "Damping", "'1.2.3' is not a valid float.", validate(attr("Damping", "1.2.3"), GRAPH));
    }

    @Test
    void floatList() {
        assertOk(validate(attr("ranksep", "1.2:-4:5e2"), GRAPH));
        assertMessage(ERROR, "ranksep", "'1.2:-4;5e2' is not valid for any of the possible types:\n"
                        + "As float: '1.2:-4;5e2' is not a valid float.\n"
                        + "As list of floats: '1.2:-4;5e2' is not a valid list of floats.",
                validate(attr("ranksep", "1.2:-4;5e2"), GRAPH));
    }

    @Test
    void point() {
        assertOk(validate(attr("head_lp", "1.2,4.5"), EDGE));
        assertOk(validate(attr("head_lp", "1.2,4.5!"), EDGE));
        assertOk(validate(attr("head_lp", "1.2,4.5,5!"), EDGE));
        assertMessage(ERROR, "head_lp", "'1.2' is not a valid point.",
                validate(attr("head_lp", "1.2"), EDGE));
        assertMessage(ERROR, "head_lp", "'1.2,3,4,5' is not a valid point.",
                validate(attr("head_lp", "1.2,3,4,5"), EDGE));
    }

    @Test
    void pointList() {
        assertOk(validate(attr("vertices", "1.2,4.5"), NODE));
        assertOk(validate(attr("vertices", "1.2,4.5! 3,4,5"), NODE));
        assertMessage(ERROR, "vertices", "'1.2' is not a valid list of points.",
                validate(attr("vertices", "1.2"), NODE));
        assertMessage(ERROR, "vertices", "'1.2,3,4,5' is not a valid list of points.",
                validate(attr("vertices", "1.2,3,4,5"), NODE));
    }

    @Test
    void viewPortOK() {
        assertOk(validate(attr("viewport", "1,5.5"), GRAPH));
        assertOk(validate(attr("viewport", "1,5.5,6"), GRAPH));
        assertOk(validate(attr("viewport", "1,5.5,6,7,8e2"), GRAPH));
        assertOk(validate(attr("viewport", "1,5.5,6,'bla'"), GRAPH));
    }

    @Test
    void viewPortNoK() {
        assertMessage(ERROR, "viewport", "'a' is not a valid view port.", validate(attr("viewport", "a"), GRAPH));
        assertMessage(ERROR, "viewport", "'1,5.5,6,7' is not a valid view port.", validate(attr("viewport", "1,5.5,6,7"), GRAPH));
    }

    @Test
    void colorOk() {
        assertOk(validate(attr("fontcolor", "#12af 44"), NODE));
        assertOk(validate(attr("fontcolor", "#12af 44 0d"), NODE));
        assertOk(validate(attr("fontcolor", ".12, 0.5,.111"), NODE));
        assertOk(validate(attr("fontcolor", "blu"), NODE));
    }

    @Test
    void colorNok() {
        assertMessage(ERROR, "fontcolor", "'#12' is not a valid color.", validate(attr("fontcolor", "#12"), NODE));
        assertMessage(ERROR, "fontcolor", "'#12 gg hh' is not a valid color.", validate(attr("fontcolor", "#12 gg hh"), NODE));
        assertMessage(ERROR, "fontcolor", "'1,2,3' is not a valid color.", validate(attr("fontcolor", "1,2,3"), NODE));
        assertMessage(ERROR, "fontcolor", "'.4,.5,.6,.7' is not a valid color.", validate(attr("fontcolor", ".4,.5,.6,.7"), NODE));
    }

    private void assertMessage(Severity severity, String attribute, String message, List<ValidatorMessage> actual) {
        assertEquals(singletonList(new ValidatorMessage(severity, attribute, message)), actual);
    }

    private void assertOk(List<ValidatorMessage> actual) {
        assertEquals(emptyList(), actual);
    }

    private List<ValidatorMessage> validate(Attributes<? extends For> attr, AttributeValidator.Scope scope) {
        return validate(attr, scope, UNKNOWN_ENGINE, UNKNOWN_FORMAT);
    }

    private List<ValidatorMessage> validate(Attributes<? extends For> attr, AttributeValidator.Scope scope,
                                            ValidatorEngine engine, ValidatorFormat format) {
        return new AttributeValidator().forEngine(engine).forFormat(format).validate(attrs(attr), scope);
    }
}
