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

class GraalJavascriptEngine extends AbstractJavascriptEngine {
    private final ResultHandler resultHandler = new ResultHandler();
    private final Context context = Context.newBuilder("js").allowAllAccess(true).build();

    GraalJavascriptEngine() {
        context.getPolyglotBindings().putMember("handler", resultHandler);
        eval("function result(r){ Polyglot.import('handler').setResult(r); }"
                + "function error(r){ Polyglot.import('handler').setError(r); }"
                + "function log(r){ Polyglot.import('handler').log(r); }");
    }

    @Override
    protected String execute(String js) {
        try {
            eval(js);
            return resultHandler.waitFor();
        } catch (PolyglotException e) {
            throw new GraphvizException("Problem executing javascript", e);
        }
    }

    private void eval(String code) {
        context.eval("js", code);
    }
}
