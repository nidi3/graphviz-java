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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
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
        assertEquals(map("a", ""), opts.opts);
    }

    @Test
    void cmdLineValue() {
        final GraphvizServer.CmdOptions opts = GraphvizServer.CmdOptions.parse(new String[]{"-ab"});
        assertEquals(map("a", "b"), opts.opts);
    }

    @Test
    void cmdLineValueSeparated() {
        final GraphvizServer.CmdOptions opts = GraphvizServer.CmdOptions.parse(new String[]{"-a", "b"});
        assertEquals(map("a", "b"), opts.opts);
    }

    @Test
    void cmdLineArgs() {
        final GraphvizServer.CmdOptions opts = GraphvizServer.CmdOptions.parse(new String[]{"-ab", "c", "d"});
        assertEquals(map("a", "b"), opts.opts);
        assertEquals(asList("c", "d"), opts.args);
    }

    @Test
    void start() throws InterruptedException {
        final Thread server = new Thread(() -> {
            try {
                GraphvizServer.main("-p9876","GraphvizV8Engine");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        server.setDaemon(true);
        server.start();
        Thread.sleep(3000);
        final GraphvizServerEngine engine = new GraphvizServerEngine().port(9876);
        final EngineResult result = engine.execute("graph {a--b}", Options.create(), Rasterizer.DEFAULT);
        result.consume(f -> {
        }, s -> assertThat(s, startsWith("<svg")));
        engine.stopThisServer();
        server.join();
    }

    private Map<String, String> map(String a, String b) {
        final Map<String, String> res = new HashMap<>();
        res.put(a, b);
        return res;
    }
}
