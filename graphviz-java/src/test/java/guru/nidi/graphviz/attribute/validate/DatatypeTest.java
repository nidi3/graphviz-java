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

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DatatypeTest {
    @Nested
    class Point extends DatatypeTestBase {
        Point() {
            super(Datatypes.POINT);
        }

        @Test
        void point() {
            assertOk("1.2,4.5");
            assertOk("1.2,4.5!");
            assertOk("1.2,4.5,5!");
            assertMessage("has the invalid point value '1.2'.", "1.2");
            assertMessage("has the invalid point value '1.2,3,4,5'.", "1.2,3,4,5");
        }
    }

    @Nested
    class PointList extends DatatypeTestBase {
        PointList() {
            super(Datatypes.POINT_LIST);
        }

        @Test
        void pointList() {
            assertOk("1.2,4.5");
            assertOk("1.2,4.5! 3,4,5");
            assertMessage("has the invalid list of points value '1.2'.", "1.2");
            assertMessage("has the invalid list of points value '1.2,3,4,5'.", "1.2,3,4,5");
        }
    }

    @Nested
    class ViewPort extends DatatypeTestBase {
        ViewPort() {
            super(Datatypes.VIEW_PORT);
        }

        @Test
        void viewPortOK() {
            assertOk("1,5.5");
            assertOk("1,5.5,6");
            assertOk("1,5.5,6,7,8e2");
            assertOk("1,5.5,6,'bla'");
        }

        @Test
        void viewPortNoK() {
            assertMessage("has the invalid view port value 'a'.", "a");
            assertMessage("has the invalid view port value '1,5.5,6,7'.", "1,5.5,6,7");
        }
    }

    @Nested
    class Color extends DatatypeTestBase {
        Color() {
            super(Datatypes.COLOR);
        }

        @Test
        void colorOk() {
            assertOk("#12af 44");
            assertOk("#12af 44 0d");
            assertOk(".12, 0.5,.111");
            assertOk("blu");
        }

        @Test
        void colorNok() {
            assertMessage("has the invalid color value '#12'.", "#12");
            assertMessage("has the invalid color value '#12 gg hh'.", "#12 gg hh");
            assertMessage("has the invalid color value '1,2,3'.", "1,2,3");
            assertMessage("has the invalid color value '.4,.5,.6,.7'.", ".4,.5,.6,.7");
        }
    }

    @Nested
    class Style extends DatatypeTestBase {
        Style() {
            super(Datatypes.STYLE);
        }

        @Test
        void styleOk() {
            assertOk("dashed");
            assertOk("dashed,filled, wedged");
        }

        @Test
        void styleNok() {
            assertMessage("has the invalid style value(s) 'hula'.", "hula");
            assertMessage("has the invalid style value(s) 'bla, hula'.", "bla,dashed,hula");
        }
    }
}
