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

import javax.script.*;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GraphvizJdkEngine extends AbstractJsGraphvizEngine {
    private static final ScriptEngine ENGINE = new ScriptEngineManager().getEngineByExtension("js");
    private static final ThreadLocal<ResultHandler> HANDLER = new ThreadLocal<>();
    private static final Pattern JAVA_18_PATTERN = Pattern.compile("1.8.0_(\\d+).*");

    public GraphvizJdkEngine() {
        super(false);
        final String version = System.getProperty("java.version");
        final Matcher matcher = JAVA_18_PATTERN.matcher(version);
        if (matcher.matches() && Integer.parseInt(matcher.group(1)) < 40) {
            throw new GraphvizException("You are using an old version of java 1.8. Please update it.");
        }
    }

    @Override
    protected String jsExecute(String jsCall) {
        try {
            if (HANDLER.get() == null) {
                HANDLER.set(new ResultHandler());
            }
            ENGINE.getBindings(ScriptContext.ENGINE_SCOPE).put("handler", HANDLER.get());
            ENGINE.eval(jsCall);
            return HANDLER.get().waitFor();
        } catch (ScriptException e) {
            throw new GraphvizException("Problem executing graphviz", e);
        }
    }

    @Override
    protected void doInit() throws Exception {
        try (final InputStream api = getClass().getResourceAsStream("/net/arnx/nashorn/lib/promise.js")) {
            ENGINE.eval(IoUtils.readStream(api));
        }
        ENGINE.eval(jsVizCode());
        ENGINE.eval("var graphviz = Java.type('guru.nidi.graphviz.engine.GraphvizJdkEngine');"
                + "function result(r){ handler.setResult(r); }"
                + "function error(r){ handler.setError(r); }");
        ENGINE.eval(jsInitEnv());
        execute("digraph g { a -> b; }", Options.create());
    }
}
