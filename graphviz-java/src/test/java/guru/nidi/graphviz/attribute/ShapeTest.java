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

import static guru.nidi.graphviz.attribute.Attributes.attr;
import static guru.nidi.graphviz.attribute.Attributes.attrs;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ShapeTest {
    @Test
    void simple() {
        assertEquals(attr("shape", "house"), attrs(Shape.HOUSE));
    }

    @Test
    void mDiamond() {
        assertEquals(attrs(attr("shape", "Mdiamond")),
                attrs(Shape.M_DIAMOND));
    }

    @Test
    void polygon() {
        assertEquals(attrs(attr("sides", 4), attr("shape", "polygon"), attr("skew", 2.5), attr("distortion", .66)),
                Shape.polygon(4).skew(2.5).distortion(.66).applyTo(attrs()));
    }

}
