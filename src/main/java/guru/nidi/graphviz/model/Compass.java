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
package guru.nidi.graphviz.model;

import java.util.Optional;
import java.util.stream.Stream;

public enum Compass {
    NORTH("n"), NORTH_EAST("ne"), EAST("e"), SOUTH_EAST("se"),
    SOUTH("s"), SOUTH_WEST("sw"), WEST("w"), NORTH_WEST("nw"),
    CENTER("c");

    final String value;

    Compass(String value) {
        this.value = value;
    }

    public static Optional<Compass> of(String value) {
        return Stream.of(values()).filter(c -> c.value.equals(value)).findFirst();
    }

}
