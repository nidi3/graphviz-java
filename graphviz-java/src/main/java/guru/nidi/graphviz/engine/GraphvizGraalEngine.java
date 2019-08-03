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

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;

import java.io.InputStream;

import static guru.nidi.graphviz.engine.IoUtils.readStream;

class GraphvizGraalEngine extends AbstractJsGraphvizEngine {
    private static final Context CTX = Context.newBuilder("js").allowAllAccess(true).build();
    private static final ThreadLocal<ResultHandler> HANDLER = new ThreadLocal<>();

    GraphvizGraalEngine() {
        super(false);
    }

    @Override
    protected String jsExecute(String jsCall) {
        try {
            if (HANDLER.get() == null) {
                HANDLER.set(new ResultHandler());
            }
            CTX.getPolyglotBindings().putMember("handler", HANDLER.get()); //TODO multithreading working?
            eval(jsCall);
            return HANDLER.get().waitFor();
        } catch (PolyglotException e) {
            throw new GraphvizException("Problem executing graphviz", e);
        }
    }

    @Override
    protected void doInit() throws Exception {
        try (final InputStream api = getClass().getResourceAsStream("/net/arnx/nashorn/lib/promise.js")) {
            eval(readStream(api));
        }
        eval(jsVizCode());
        eval("function result(r){ Polyglot.import('handler').setResult(r); }"
                + "function error(r){ Polyglot.import('handler').setError(r); }");
        eval(jsInitEnv());
        execute("digraph g { a -> b; }", Options.create(), null);
    }

    private void eval(String code) {
        CTX.eval("js", code);
    }
}
