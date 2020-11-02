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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EngineResultTest {
    @Test
    void mapStringOfFile() {
        EngineResult.fromFile(new File("test")).mapString(s -> s + "-")
                .consume(
                        f -> assertEquals("test", f.getName()),
                        Assertions::assertNull);
    }

    @Test
    void mapStringOfString() {
        EngineResult.fromString("s").mapString(s -> s + "-")
                .consume(
                        Assertions::assertNull,
                        s -> assertEquals("s-", s));
    }

    @Test
    void mapOfFile() {
        assertEquals(new File("test/sub"), EngineResult.fromFile(new File("test"))
                .map(f -> new File(f, "sub"), s -> s + "-"));
    }

    @Test
    void mapOfString() {
        assertEquals("s-", EngineResult.fromString("s")
                .map(f -> new File(f, "sub"), s -> s + "-"));
    }

    @Test
    void mapIoOfFile() throws IOException {
        assertEquals(new File("test/sub"), EngineResult.fromFile(new File("test"))
                .mapIO(f -> new File(f, "sub"), s -> s + "-"));
    }

    @Test
    void mapIoOfString() throws IOException {
        assertEquals("s-", EngineResult.fromString("s")
                .mapIO(f -> new File(f, "sub"), s -> s + "-"));
    }

}
