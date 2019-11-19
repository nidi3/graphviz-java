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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JavascriptEngineTest {
    @Test
    void jdkOk() throws ScriptException { //switch between nashorn and graal via dependency changes
        final JdkJavascriptEngine e = new JdkJavascriptEngine();
        final JdkJavascriptEngine f = new JdkJavascriptEngine();
        e.executeJavascript("a='42'");
        f.executeJavascript("a='hula'");
        assertEquals("42", e.executeJavascript("result(a) //", "", ""));
        assertEquals("hula", f.executeJavascript("result(a) //", "", ""));
    }

    @Test
    void jdkFail() { //switch between nashorn and graal via dependency changes
        assertThrows(GraphvizException.class, () -> {
            final JdkJavascriptEngine e = new JdkJavascriptEngine();
            e.executeJavascript("bla");
        });
    }

    @Test
    void v8() {
        final V8JavascriptEngine e = new V8JavascriptEngine();
        final V8JavascriptEngine f = new V8JavascriptEngine();
        e.executeJavascript("a='42'");
        f.executeJavascript("a='hula'");
        assertEquals("42", e.executeJavascript("result(a) //", "", ""));
        assertEquals("hula", f.executeJavascript("result(a) //", "", ""));
    }

    @ParameterizedTest
    @MethodSource
    void threading(Supplier<JavascriptEngine> engineSupplier) throws InterruptedException, ExecutionException {
        final ExecutorService pool = Executors.newFixedThreadPool(10);
        final List<Future<Integer>> futures = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final int j = i;
            futures.add(pool.submit(() -> {
                final JavascriptEngine engine = engineSupplier.get();
                final String s = engine.executeJavascript("s=0; for(i=0;i<" + Math.random() * 10000 + ";i++){ s+=Math.sin(i); } result('" + j + "'); //", "", "");
                System.out.println(s);
                return Integer.parseInt(s) - j;
            }));
        }
        for (final Future f : futures) {
            assertEquals(0, f.get());
        }
    }

    static List<Supplier<JavascriptEngine>> threading() {
        return asList(JdkJavascriptEngine::new, V8JavascriptEngine::new);
    }
}
