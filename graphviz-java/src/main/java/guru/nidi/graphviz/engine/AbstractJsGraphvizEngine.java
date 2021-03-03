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
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static guru.nidi.graphviz.engine.GraphvizLoader.*;
import static guru.nidi.graphviz.engine.StringFunctions.replaceRegex;
import static java.util.stream.Collectors.joining;

public abstract class AbstractJsGraphvizEngine extends AbstractGraphvizEngine {
    private static final String RENDER_JS = loadAsString("guru/nidi/graphviz/engine/render.js");
    private static final String VIZ_BASE = "META-INF/resources/webjars/viz.js-graphviz-java/2.1.3/";
    static final boolean AVAILABLE = isOnClasspath(VIZ_BASE + "viz.js");
    private static final Pattern FONT_NAME_PATTERN = Pattern.compile("\"?fontname\"?\\s*=\\s*\"?(.*?)[\",;\\]]");
    private static final Map<Class<?>, ThreadLocal<EngineState>> ENGINES = new HashMap<>();
    private final Supplier<JavascriptEngine> engineSupplier;

    protected AbstractJsGraphvizEngine(boolean sync, Supplier<JavascriptEngine> engineSupplier) {
        super(sync);
        if (!AVAILABLE) {
            throw new MissingDependencyException("Javascript engines are not available.",
                    "org.webjars.npm:viz.js-graphviz-java");
        }
        this.engineSupplier = engineSupplier;
    }

    @Override
    protected void doInit() {
        final EngineState state = getState();
        if (state == null || !state.ininted) {
            final JavascriptEngine engine = engine(true);
            engine.executeJavascript(vizJsCode());
            engine.executeJavascript(RENDER_JS);
            execute("graph g { a -- b }", Options.create(), Rasterizer.NONE);
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
    public EngineResult execute(String src, Options options, Rasterizer rasterizer) {
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
        return engine().executeJavascript(
                memory + "render(",
                preprocessCode(src, options),
                "," + options.toJson(false) + ");");
    }

    private void measureFonts(String src) {
        final Matcher matcher = FONT_NAME_PATTERN.matcher(src);
        while (matcher.find()) {
            final String font = matcher.group(1).trim();
            final double[] widths = FontMeasurer.measureFont(font);
            if (widths.length > 0) {
                final String widthsString = Arrays.stream(widths).mapToObj(Double::toString).collect(joining(","));
                engine().executeJavascript("initViz().setFontWidth('" + font + "',[" + widthsString + "])");
            }
        }
    }

    protected String preprocessCode(String src, Options options) {
        if (src.contains("<img")) {
            throw new GraphvizException("Found <img> tag. This is not supported by JS engines. "
                    + "Either use the GraphvizCmdLineEngine or a node with image attribute.");
        }
        return replaceRegex(src, IMAGE_ATTR, path -> options.image(path).processImagePath(path));
    }

    private String vizJsCode() {
        return loadAsString(VIZ_BASE + "viz.js") + loadAsString(VIZ_BASE + "full.render.js");
    }

    protected String promiseJsCode() {
        return loadAsString("net/arnx/nashorn/lib/promise.js");
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
