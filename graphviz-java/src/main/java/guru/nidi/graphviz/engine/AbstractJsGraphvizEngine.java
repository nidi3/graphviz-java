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

import guru.nidi.graphviz.service.SystemUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import static guru.nidi.graphviz.engine.IoUtils.readStream;

public abstract class AbstractJsGraphvizEngine extends AbstractGraphvizEngine {
    public AbstractJsGraphvizEngine(boolean sync) {
        super(sync);
    }

    @Override
    public EngineResult execute(String src, Options options, @Nullable Rasterizer rasterizer) {
        if (rasterizer instanceof BuiltInRasterizer) {
            throw new GraphvizException("Built-in Rasterizer can only be used together with GraphvizCmdLineEngine.");
        }
        return EngineResult.fromString(jsExecute(jsVizExec(src, options)));
    }

    protected abstract String jsExecute(String jsCall);

    protected String jsVizExec(String src, Options options) {
        if (src.startsWith("totalMemory") || src.startsWith("render")) {
            return src;
        }
        final String memory = options.totalMemory == null ? "" : "totalMemory=" + options.totalMemory + ";";
        final Entry<String, Options> srcAndOpts = preprocessCode(src, options);
        final String render = "render('" + srcAndOpts.getKey() + "'," + srcAndOpts.getValue().toJson(false) + ");";
        return memory + render;
    }

    protected Entry<String, Options> preprocessCode(String src, Options options) {
        if (src.contains("<img")) {
            throw new GraphvizException("Found <img> tag. This is not supported by JS engines. "
                    + "Either use the GraphvizCmdLineEngine or a node with image attribute.");
        }
        final Options[] opts = new Options[]{options};
        final String pathsReplaced = replacePaths(src, IMAGE_ATTR, path -> {
            final String realPath = SystemUtils.uriPathOf(replacePath(path, options.basedir));
            opts[0] = opts[0].image(realPath);
            return realPath;
        });
        return new SimpleEntry<>(jsEscape(pathsReplaced), opts[0]);
    }

    protected String jsEscape(String js) {
        return js.replace("\\", "\\\\").replace("'", "\\'").replaceAll("\\R", "\\\\n");
    }

    protected String jsVizCode() throws IOException {
        final String path = "/META-INF/resources/webjars/viz.js/2.1.2/";
        try (final InputStream api = getClass().getResourceAsStream(path + "viz.js");
             final InputStream engine = getClass().getResourceAsStream(path + "full.render.js")) {
            return readStream(api) + readStream(engine);
        }
    }

    protected String jsInitEnv() {
        return "var viz; var totalMemory = 16777216;"
                + "function initViz(force){"
                + "  if (force || !viz || viz.totalMemory !== totalMemory){"
                + "    viz = new Viz({"
                + "      Module: function(){ return Viz.Module({TOTAL_MEMORY: totalMemory}); },"
                + "      render: Viz.render"
                + "    });"
                + "    viz.totalMemory = totalMemory;"
                + "  }"
                + "  return viz;"
                + "}"
                + "function render(src, options){"
                + "  try {"
                + "    initViz().renderString(src, options)"
                + "      .then(function(res) { result(res); })"
                + "      .catch(function(err) { initViz(true); error(err.toString()); });"
                + "  } catch(e) { error(e.toString()); }"
                + "}";
    }

}
