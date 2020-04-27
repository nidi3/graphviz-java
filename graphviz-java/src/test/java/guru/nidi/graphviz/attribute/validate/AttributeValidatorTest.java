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
        assertMessage(ERROR, "hula", "is unknown.", validate(attr("hula", 5), NODE));
    }

    @Test
    void wrongScope() {
        assertMessage(ERROR, "Damping", "is not allowed for nodes.",
                validate(attr("Damping", 5), NODE));
    }

    @Test
    void wrongEngine() {
        assertMessage(ERROR, "Damping", "is not allowed for engine 'DOT'.",
                validate(attr("Damping", 5), GRAPH, DOT, UNKNOWN_FORMAT));
    }

    @Test
    void wrongFormat() {
        assertMessage(ERROR, "URL", "is not allowed for format 'CMAP'.",
                validate(attr("URL", 5), GRAPH, null, CMAP));
    }

    @Test
    void useDoubleDefault() {
        assertMessage(INFO, "Damping", "has its default value '0.99'.",
                validate(attr("Damping", .99), GRAPH));
        assertMessage(INFO, "Damping", "has its default value '0.99'.",
                validate(attr("Damping", .99000001), GRAPH));
        assertMessage(INFO, "Damping", "has its default value '0.99'.",
                validate(attr("Damping", ".99"), GRAPH));
    }

    @Test
    void useIntDefault() {
        assertMessage(INFO, "dim", "has its default value '2'.", validate(attr("dim", 2), GRAPH));
        assertMessage(INFO, "dim", "has its default value '2'.", validate(attr("dim", "+2"), GRAPH));
    }

    @Test
    void useBoolDefault() {
        assertMessage(INFO, "headclip", "has its default value 'true'.",
                validate(attr("headclip", true), EDGE));
        assertMessage(INFO, "headclip", "has its default value 'true'.",
                validate(attr("headclip", "truE"), EDGE));
        assertMessage(INFO, "headclip", "has its default value 'true'.",
                validate(attr("headclip", "Yes"), EDGE));
        assertEquals(asList(
                new ValidatorMessage(INFO, "headclip", "has its default value 'true'."),
                new ValidatorMessage(WARN, "headclip", "uses the numerical value '42' as boolean.")),
                validate(attr("headclip", "42"), EDGE));
        assertMessage(INFO, "center", "has its default value 'false'.",
                validate(attr("center", false), GRAPH));
        assertMessage(INFO, "center", "has its default value 'false'.",
                validate(attr("center", "False"), GRAPH));
        assertMessage(INFO, "center", "has its default value 'false'.",
                validate(attr("center", "NO"), GRAPH));
        assertEquals(asList(
                new ValidatorMessage(INFO, "center", "has its default value 'false'."),
                new ValidatorMessage(WARN, "center", "uses the numerical value '0' as boolean.")),
                validate(attr("center", "0"), GRAPH));
    }

    @Test
    void minimum() {
        assertOk(validate(attr("Damping", 0), GRAPH));
        assertMessage(WARN, "Damping", "has the value '-0.1' smaller than the minimum of '0.0'.",
                validate(attr("Damping", -.1), GRAPH));
        assertMessage(WARN, "dim", "has the value '1' smaller than the minimum of '2.0'.",
                validate(attr("dim", 1), GRAPH));
    }

    @Test
    void invalidInt() {
        assertMessage(ERROR, "dim", "has the invalid integer value 'a'.", validate(attr("dim", "a"), GRAPH));
    }

    @Test
    void invalidFloat() {
        assertMessage(ERROR, "Damping", "has the invalid float value '1.2.3'.", validate(attr("Damping", "1.2.3"), GRAPH));
    }

    @Test
    void floatList() {
        assertOk(validate(attr("ranksep", "1.2:-4:5e2"), GRAPH));
        assertMessage(ERROR, "ranksep", "has the value '1.2:-4;5e2' which is not valid for any of the possible types:\n" +
                        "As float it has the invalid float value '1.2:-4;5e2'.\n" +
                        "As list of floats it has the invalid list of floats value '1.2:-4;5e2'.",
                validate(attr("ranksep", "1.2:-4;5e2"), GRAPH));
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
