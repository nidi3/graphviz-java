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
package guru.nidi.graphviz.engine;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringFunctionsTest {
    @Test
    void replaceRegex() {
        assertEquals("1-a2-b3c", StringFunctions.replaceRegex("1a2b3c", Pattern.compile("([a-z])[0-9]"), a -> "-" + a));
    }

    @Test
    void escapeJs() {
        assertEquals("1\\\\a2\\'3\\n4\\n5\\n6", StringFunctions.escapeJs("1\\a2'3\n4\r5\r\n6"));
    }

    @Test
    void replaceSubSpaces() {
        char[] cs = new char[34];
        for (int i = 0; i < cs.length; i++) {
            cs[i] = (char) i;
        }
        assertEquals("         \t\n  \r                   !", StringFunctions.replaceSubSpaces(new String(cs)));
    }

    @Test
    void replaceNonWordChars() {
        char[] cs = new char[127];
        for (int i = 0; i < cs.length; i++) {
            cs[i] = (char) i;
        }
        assertEquals("----------------------------------------------.-0123456789-------"
                        + "ABCDEFGHIJKLMNOPQRSTUVWXYZ------abcdefghijklmnopqrstuvwxyz----",
                StringFunctions.replaceNonWordChars(new String(cs)));
    }
}
