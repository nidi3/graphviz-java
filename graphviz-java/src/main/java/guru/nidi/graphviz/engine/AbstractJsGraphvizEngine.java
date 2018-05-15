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
        return jsExecute(jsVizExec(src, options));
    }

    protected abstract String jsExecute(String jsCall);

    protected String jsVizExec(String src, Options options) {
        return src.startsWith("render") ? src : ("render('" + jsEscape(src) + "'," + options.toJson(false) + ");");
    }

    protected String jsEscape(String js) {
        return js.replaceAll("\\R", " ").replace("\\", "\\\\").replace("'", "\\'");
    }

    protected String jsVizCode(String version) throws IOException {
        try (final InputStream api = getClass().getResourceAsStream("/viz-" + version + ".js");
             final InputStream engine = getClass().getResourceAsStream("/viz-full.render-" + version + ".js")) {
            return IoUtils.readStream(api) + IoUtils.readStream(engine);
        }
    }

    protected String jsInitEnv() {
        return "var viz = new Viz();"
                + "function render(src, options){"
                + "  try {"
                + "    viz.renderString(src, options)"
                + "      .then(function(res) { result(res); })"
                + "      .catch(function(err) { viz = new Viz(); error(err.toString()); });"
                + "  } catch(e) { error(e.toString()); }"
                + "}";
    }

}
