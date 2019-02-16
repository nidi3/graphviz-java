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
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

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
        final Entry<String, Options> srcAndOptions = preprocessCode(src, options);
        return src.startsWith("render")
                ? src
                : ("render('" + srcAndOptions.getKey() + "'," + srcAndOptions.getValue().toJson(false) + ");");
    }

    protected Entry<String, Options> preprocessCode(String src, Options options) {
        if (src.contains("<img")) {
            throw new GraphvizException("Found <img> tag. This is not supported by JS engines. "
                    + "Either use the GraphvizCmdLineEngine or a node with image attribute.");
        }
        final Options[] opts = new Options[]{options};
        final String pathsReplaced = replacePaths(src, IMAGE_ATTR, path -> {
            final String realPath = replacePath(path, options.basedir);
            opts[0] = opts[0].image(realPath);
            return realPath;
        });
        return new SimpleEntry<>(jsEscape(pathsReplaced), opts[0]);
    }

    protected String jsEscape(String js) {
        return js.replaceAll("\\R", " ").replace("\\", "\\\\").replace("'", "\\'");
    }

    protected String jsVizCode() throws IOException {
        final String path = "/META-INF/resources/webjars/viz.js/2.1.2/";
        try (final InputStream api = getClass().getResourceAsStream(path + "viz.js");
             final InputStream engine = getClass().getResourceAsStream(path + "full.render.js")) {
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
