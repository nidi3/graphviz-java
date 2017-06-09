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

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Map;
import java.util.stream.Collectors;

public class GraphvizJdkEngine extends AbstractJsGraphvizEngine {
    private static final ScriptEngine ENGINE = new ScriptEngineManager().getEngineByExtension("js");

    public GraphvizJdkEngine() {
        this(null);
    }

    public GraphvizJdkEngine(EngineInitListener engineInitListener) {
        super(false, engineInitListener);
    }

    @Override
    protected String jsExecute(String jsCall) {
        try {
            return (String) ENGINE.eval("$$prints=[]; " + jsCall);
        } catch (ScriptException e) {
            if (e.getMessage().startsWith("abort")) {
                try {
                    throw new GraphvizException(((Map<Integer, Object>) ENGINE.eval("$$prints"))
                            .values()
                            .stream()
                            .map(Object::toString)
                            .collect(Collectors.joining("\n")));
                } catch (ScriptException e1) {
                    //fall through to general exception
                }
            }
            throw new GraphvizException("Problem executing graphviz", e);
        }
    }

    @Override
    protected void doInit() throws Exception {
        try {
            ENGINE.eval(jsInitEnv());
            ENGINE.eval(jsVizCode("1.4.1"));
            ENGINE.eval("Viz('digraph g { a -> b; }');");
        } catch (AssertionError e) {
            throw new IllegalStateException(
                    "Not all versions of JDK 1.8's javascript engines support viz.js, sorry! You can try to\n"
                            + "- use the V8 engine\n"
                            + "- try a JDK 1.9 version\n"
                            + "- downgrade the JDK (1.8.0_31 seems to be a good guess)\n", e);
        }
    }
}
