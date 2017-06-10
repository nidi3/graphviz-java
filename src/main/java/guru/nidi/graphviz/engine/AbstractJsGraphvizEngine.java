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

import java.io.IOException;
import java.io.InputStream;

public abstract class AbstractJsGraphvizEngine extends AbstractGraphvizEngine {
    public AbstractJsGraphvizEngine(boolean sync) {
        super(sync);
    }

    @Override
    public String execute(String src, Options options) {
        return jsExecute(jsVizExec(src,options));
    }

    protected abstract String jsExecute(String jsCall);

    protected String jsVizExec(String src, Options options) {
        return src.startsWith("Viz(") ? src : ("Viz('" + jsEscape(src) + "'," + options.toJson() + ");");
    }

    protected String jsEscape(String js) {
        return js.replace("\n", " ").replace("\\", "\\\\").replace("'", "\\'");
    }

    protected String jsVizCode(String version) throws IOException {
        try (final InputStream in = getClass().getResourceAsStream("/viz-" + version + ".js")) {
            return IoUtils.readStream(in);
        }
    }

    protected String jsInitEnv() {
        return "var $$prints=[], print=function(s){$$prints.push(s);};";
    }

}
