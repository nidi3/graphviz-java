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
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static guru.nidi.graphviz.engine.GraphvizLoader.*;
import static java.util.stream.Collectors.joining;

public abstract class AbstractJsGraphvizEngine extends AbstractGraphvizEngine {
    private static final String VIZ_BASE = "META-INF/resources/webjars/viz.js-for-graphviz-java/2.1.2/";
    static final boolean AVAILABLE = isOnClasspath(VIZ_BASE + "viz.js");
    private static final Pattern FONT_NAME_PATTERN = Pattern.compile("\"?fontname\"?\\s*=\\s*\"?(.*?)[\",;\\]]");
    private static final Map<Class<?>, ThreadLocal<EngineState>> ENGINES = new HashMap<>();
    private final Supplier<JavascriptEngine> engineSupplier;
    private final FontMeasurer fontMeasurer = new FontMeasurer();

    protected AbstractJsGraphvizEngine(boolean sync, Supplier<JavascriptEngine> engineSupplier) {
        super(sync);
        if (!AVAILABLE) {
            throw new MissingDependencyException("Javascript engines are not available.",
                    "org.webjars.npm:viz.js-for-graphviz-java");
        }
        this.engineSupplier = engineSupplier;
    }

    @Override
    protected void doInit() {
        final EngineState state = getState();
        if (state == null || !state.ininted) {
            final JavascriptEngine engine = engine(true);
            engine.executeJavascript(vizJsCode());
            engine.executeJavascript(renderJsCode());
            execute("graph g { a -- b }", Options.create(), null);
        }
    }

    protected JavascriptEngine engine() {
        return engine(false);
    }

    private JavascriptEngine engine(boolean init) {
        // TODO thread local causes 2 inits of async engines
        final ThreadLocal<EngineState> holder = ENGINES.computeIfAbsent(getClass(), e -> new ThreadLocal<>());
        EngineState state = holder.get();
        if (state == null) {
            state = new EngineState(engineSupplier.get());
            holder.set(state);
            if (!init) {
                throwingInit();
            }
        }
        if (init) {
            state.ininted = true;
        }
        return state.engine;
    }

    @Nullable
    private EngineState getState() {
        final ThreadLocal<EngineState> holder = ENGINES.get(getClass());
        return holder == null ? null : holder.get();
    }

    @Override
    public void close() {
        final EngineState state = getState();
        if (state != null) {
            closeQuietly(state.engine);
            ENGINES.get(getClass()).remove();
        }
    }

    @Override
    public EngineResult execute(String src, Options options, @Nullable Rasterizer rasterizer) {
        if (rasterizer instanceof BuiltInRasterizer) {
            throw new GraphvizException("Built-in Rasterizer can only be used together with GraphvizCmdLineEngine.");
        }
        return EngineResult.fromString(jsVizExec(src, options));
    }

    protected String jsVizExec(String src, Options options) {
        if (src.startsWith("totalMemory") || src.startsWith("render")) {
            return src;
        }
        final String memory = options.totalMemory == null ? "" : "totalMemory=" + options.totalMemory + ";";
        measureFonts(src);
        final Entry<String, Options> srcAndOpts = preprocessCode(src, options);
        return engine().executeJavascript(
                memory + "render(",
                srcAndOpts.getKey(),
                "," + srcAndOpts.getValue().toJson(false) + ");");
    }

    private void measureFonts(String src) {
        final Matcher matcher = FONT_NAME_PATTERN.matcher(src);
        while (matcher.find()) {
            final String font = matcher.group(1).trim();
            final double[] widths = fontMeasurer.measureFont(font);
            if (widths.length > 0) {
                final String widthsString = Arrays.stream(widths).mapToObj(Double::toString).collect(joining(","));
                engine().executeJavascript("initViz().setFontWidth('" + font + "',[" + widthsString + "])");
            }
        }
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
        return new SimpleEntry<>(pathsReplaced, opts[0]);
    }

    private String vizJsCode() {
        return loadAsString(VIZ_BASE + "viz.js") + loadAsString(VIZ_BASE + "full.render.js");
    }

    protected String promiseJsCode() {
        return loadAsString("net/arnx/nashorn/lib/promise.js");
    }

    private String renderJsCode() {
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

    private static class EngineState {
        final JavascriptEngine engine;
        boolean ininted;

        EngineState(JavascriptEngine engine) {
            this.engine = engine;
            this.ininted = false;
        }
    }
}
