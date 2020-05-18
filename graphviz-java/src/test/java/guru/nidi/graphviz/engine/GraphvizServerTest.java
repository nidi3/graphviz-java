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

import java.util.HashMap;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GraphvizServerTest {
    @Test
    void cmdLineIllegal() {
        assertThrows(IllegalArgumentException.class, () -> GraphvizServer.CmdOptions.parse(new String[]{"-"}));
    }

    @Test
    void cmdLineSimple() {
        final GraphvizServer.CmdOptions opts = GraphvizServer.CmdOptions.parse(new String[]{"-a"});
        assertEquals(new HashMap<String, String>() {{
            put("a", "");
        }}, opts.opts);
    }

    @Test
    void cmdLineValue() {
        final GraphvizServer.CmdOptions opts = GraphvizServer.CmdOptions.parse(new String[]{"-ab"});
        assertEquals(new HashMap<String, String>() {{
            put("a", "b");
        }}, opts.opts);
    }

    @Test
    void cmdLineValueSeparated() {
        final GraphvizServer.CmdOptions opts = GraphvizServer.CmdOptions.parse(new String[]{"-a", "b"});
        assertEquals(new HashMap<String, String>() {{
            put("a", "b");
        }}, opts.opts);
    }

    @Test
    void cmdLineArgs() {
        final GraphvizServer.CmdOptions opts = GraphvizServer.CmdOptions.parse(new String[]{"-ab", "c", "d"});
        assertEquals(new HashMap<String, String>() {{
            put("a", "b");
        }}, opts.opts);
        assertEquals(asList("c", "d"), opts.args);
    }
}
