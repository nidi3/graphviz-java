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

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8RuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

public class V8JavascriptEngine extends AbstractJavascriptEngine {
    private static final Logger LOG = LoggerFactory.getLogger(V8JavascriptEngine.class);
    private final V8 v8;
    private final ResultHandler resultHandler = new ResultHandler();

    public V8JavascriptEngine() {
        this(null);
    }

    public V8JavascriptEngine(@Nullable String extractionPath) {
        LOG.info("Starting V8 runtime...");
        v8 = V8.createV8Runtime(null, extractionPath);
        LOG.info("Started V8 runtime. Initializing javascript...");
        v8.registerJavaMethod((receiver, parameters) -> {
            resultHandler.setResult(parameters.getString(0));
        }, "result");
        v8.registerJavaMethod((receiver, parameters) -> {
            resultHandler.setError(parameters.getString(0));
        }, "error");
        v8.registerJavaMethod((receiver, parameters) -> {
            resultHandler.log(parameters.getString(0));
        }, "log");
        LOG.info("Initialized javascript.");
    }

    @Override
    protected String execute(String js) {
        try {
            v8.executeVoidScript(js);
            return resultHandler.waitFor();
        } catch (V8RuntimeException e) {
            throw new GraphvizException("Problem executing javascript", e);
        }
    }

    @Override
    public void close() {
        v8.release(true);
    }
}
