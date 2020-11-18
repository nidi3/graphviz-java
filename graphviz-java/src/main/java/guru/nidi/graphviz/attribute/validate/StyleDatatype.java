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

import guru.nidi.graphviz.attribute.Style;

import java.util.Set;
import java.util.stream.Stream;

import static guru.nidi.graphviz.attribute.validate.ValidatorMessage.Severity.ERROR;
import static java.util.stream.Collectors.joining;

//TODO support shape dependent styles
class StyleDatatype extends Datatype {
    private static final Set<String> STYLES = fieldNames(Style.class);

    StyleDatatype() {
        super("style");
    }

    @Override
    ValidatorMessage validate(Object value) {
        final String[] styles = value.toString().split(",");
        final String invalids = Stream.of(styles).filter(s -> !STYLES.contains(s.trim())).collect(joining(", "));
        return invalids.isEmpty()
                ? null
                : new ValidatorMessage(ERROR, "has the invalid " + name + " value(s) '" + invalids + "'.");
    }
}
