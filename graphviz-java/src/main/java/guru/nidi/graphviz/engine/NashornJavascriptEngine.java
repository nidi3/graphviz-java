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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class NashornJavascriptEngine extends AbstractJavascriptEngine {
    private static final ScriptEngine ENGINE = new ScriptEngineManager().getEngineByExtension("js");
    private static final Pattern JAVA_18_PATTERN = Pattern.compile("1.8.0_(\\d+).*");
    private final ScriptContext context = new SimpleScriptContext();
    private final ResultHandler resultHandler = new ResultHandler();

    NashornJavascriptEngine() {
        final String version = System.getProperty("java.version");
        final Matcher matcher = JAVA_18_PATTERN.matcher(version);
        if (matcher.matches() && Integer.parseInt(matcher.group(1)) < 40) {
            throw new GraphvizException("You are using an old version of java 1.8. Please update it.");
        }
        context.getBindings(ScriptContext.ENGINE_SCOPE).put("handler", resultHandler);
        eval("function result(r){ handler.setResult(r); }" + "function error(r){ handler.setError(r); }");
    }

    @Override
    protected String execute(String js) {
        eval(js);
        return resultHandler.waitFor();
    }

    private void eval(String js) {
        try {
            ENGINE.eval(js, context);
        } catch (ScriptException e) {
            throw new GraphvizException("Problem executing javascript", e);
        }
    }
}
