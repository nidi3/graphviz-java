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

import javax.annotation.Nullable;

import static guru.nidi.graphviz.engine.GraphvizLoader.isOnClasspath;

public class GraphvizJdkEngine extends AbstractJsGraphvizEngine {
    static final boolean AVAILABLE = AbstractJsGraphvizEngine.AVAILABLE
            && (tryGraal() != null || isOnClasspath("net/arnx/nashorn/lib/PromiseException.class"));

    public GraphvizJdkEngine() {
        super(false, GraphvizJdkEngine::newEngine);
        if (!AVAILABLE) {
            throw new MissingDependencyException("JDK engine is not available.", "net.arnx:nashorn-promise");
        }
    }

    private static JavascriptEngine newEngine() {
        final GraalJavascriptEngine graal = tryGraal();
        return graal == null ? new NashornJavascriptEngine() : graal;
    }

    @Nullable
    private static GraalJavascriptEngine tryGraal() {
        try {
            return new GraalJavascriptEngine();
        } catch (ExceptionInInitializerError | NoClassDefFoundError | IllegalStateException e) {
            return null;
        }
    }

    @Override
    protected void doInit() {
        final JavascriptEngine engine = engine();
        if (engine instanceof NashornJavascriptEngine) {
            engine.executeJavascript(promiseJsCode());
        }
        super.doInit();
    }
}
