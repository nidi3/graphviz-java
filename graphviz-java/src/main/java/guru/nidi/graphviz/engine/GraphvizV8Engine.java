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

import com.eclipsesource.v8.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GraphvizV8Engine extends AbstractJsGraphvizEngine {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractGraphvizEngine.class);
    private static ThreadLocal<Env> envs = new ThreadLocal<>();
    private final String extractionPath;

    public GraphvizV8Engine() {
        this(null);
    }

    public GraphvizV8Engine(String extractionPath) {
        super(true);
        this.extractionPath = extractionPath;
    }

    @Override
    public void release() {
        releaseThread();
    }

    static void releaseThread() {
        final Env env = envs.get();
        if (env != null) {
            env.release();
            envs.remove();
        }
    }

    @Override
    protected void doInit() throws IOException {
        envs.set(new Env(extractionPath, jsInitEnv(), jsVizCode("2.0.0")));
    }

    @Override
    protected String jsExecute(String call) {
        final Env env = envs.get();
        if (env == null) {
            try {
                doInit();
            } catch (IOException e) {
                throw new GraphvizException("Could not initialize v8 engine for new thread", e);
            }
        }
        return envs.get().execute(call);
    }

    private static class Env {
        final V8 v8;
        final ResultHandler resultHandler = new ResultHandler();

        Env(String extractionPath, String init, String viz) {
            LOG.info("Starting V8 runtime...");
            v8 = V8.createV8Runtime(null, extractionPath);
            LOG.info("Started V8 runtime. Initializing graphviz...");
            v8.executeVoidScript(viz);
            v8.executeVoidScript(init);
            v8.registerJavaMethod((JavaVoidCallback) (receiver, parameters) ->
                    resultHandler.setResult(parameters.getString(0)), "result");
            v8.registerJavaMethod((JavaVoidCallback) (receiver, parameters) ->
                    resultHandler.setError(parameters.getString(0)), "error");
            LOG.info("Initialized graphviz.");
        }

        String execute(String call) {
            try {
                v8.executeVoidScript(call);
                return resultHandler.waitFor();
            } catch (V8RuntimeException e) {
                throw new GraphvizException("Problem executing graphviz", e);
            }
        }

        void release() {
            v8.release(true);
        }
    }
}
