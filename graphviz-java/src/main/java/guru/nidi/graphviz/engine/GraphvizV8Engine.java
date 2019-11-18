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
import java.io.IOException;

public class GraphvizV8Engine extends AbstractJsGraphvizEngine {
    private static final ThreadLocal<Env> ENVS = new ThreadLocal<>();
    @Nullable
    private final String extractionPath;

    public GraphvizV8Engine() {
        this(null);
    }

    public GraphvizV8Engine(@Nullable String extractionPath) {
        super(true);
        this.extractionPath = extractionPath;
    }

    @Override
    public void close() {
        releaseThread();
    }

    static void releaseThread() {
        final Env env = ENVS.get();
        if (env != null) {
            env.close();
            ENVS.remove();
        }
    }

    @Override
    protected void doInit(boolean onlyCallbacks) throws IOException {
        final Env env = ENVS.get();
        if (env == null) {
            ENVS.set(new Env(extractionPath, jsInitEnv(), jsVizCode(), onlyCallbacks));
        }
    }

    @Override
    protected String jsExecute(String call) {
        try {
            doInit(false);
        } catch (IOException e) {
            throw new GraphvizException("Could not initialize v8 engine for new thread", e);
        }
        return ENVS.get().execute(call);
    }

    private static class Env implements AutoCloseable {
        private static final Logger LOG = LoggerFactory.getLogger(AbstractGraphvizEngine.class);
        final V8 v8;
        final ResultHandler resultHandler = new ResultHandler();

        Env(@Nullable String extractionPath, String init, String viz, boolean onlyCallbacks) {
            LOG.info("Starting V8 runtime...");
            v8 = V8.createV8Runtime(null, extractionPath);
            LOG.info("Started V8 runtime. Initializing graphviz...");
            v8.registerJavaMethod((receiver, parameters) -> {
                resultHandler.setResult(parameters.getString(0));
            }, "result");
            v8.registerJavaMethod((receiver, parameters) -> {
                final String rawMsg = parameters.getString(0);
                final String msg = rawMsg.matches("TypeError: Module\\..*? is not a function")
                        ? "Got Error: '" + rawMsg + "'. This is probably an out of memory error."
                        + " Try using the totalMemory method."
                        : rawMsg;
                resultHandler.setError(msg);
            }, "error");
            if (!onlyCallbacks) {
                v8.executeVoidScript(viz);
                v8.executeVoidScript(init);
            }
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

        public void close() {
            v8.release(true);
        }
    }
}
