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
package guru.nidi.graphviz.rough;

import guru.nidi.graphviz.engine.*;

import java.io.*;
import java.nio.file.Files;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.joining;

public class RoughFilter implements GraphvizFilter {
    private static final String CODE = readCode();
    private static final Pattern FONT_PATTERN = Pattern.compile("font-family=\"(.*?)\"");
    private final JavascriptEngine engine;
    private final List<Entry<Pattern, String>> fonts;
    private final Map<String, Object> options;

    public RoughFilter() {
        this(new GraphvizV8Engine());
    }

    public RoughFilter(JavascriptEngine engine) {
        this(engine, new ArrayList<>(), new HashMap<>());
    }

    private RoughFilter(JavascriptEngine engine, List<Entry<Pattern, String>> fonts, Map<String, Object> options) {
        this.engine = engine;
        engine.init();
        engine.executeJavascript(CODE);
        this.options = options;
        this.fonts = fonts;
    }

    public RoughFilter font(String from, String to) {
        final List<Entry<Pattern, String>> fs = new ArrayList<>(fonts);
        fs.add(new SimpleEntry<>(Pattern.compile(from.replace("*", ".*?"), Pattern.CASE_INSENSITIVE), to));
        return new RoughFilter(engine, fs, options);
    }

    public RoughFilter roughness(double roughness) {
        return new RoughFilter(engine, fonts, options("roughness", roughness));
    }

    public RoughFilter bowing(double bowing) {
        return new RoughFilter(engine, fonts, options("bowing", bowing));
    }

    public RoughFilter fillStyle(FillStyle fillStyle) {
        return new RoughFilter(engine, fonts, options("fillStyle", fillStyle));
    }

    public RoughFilter curveStepCount(double curveStepCount) {
        return new RoughFilter(engine, fonts, options("curveStepCount", curveStepCount));
    }

    private Map<String, Object> options(String key, Object value) {
        final Map<String, Object> os = new HashMap<>(options);
        os.put(key, value);
        return os;
    }

    @Override
    public EngineResult filter(Format format, EngineResult engineResult) {
        if (format != Format.SVG && format != Format.SVG_STANDALONE && format != Format.PNG) {
            throw new GraphvizException("RoughFilter supports only SVG and PNG formats.");
        }
        return EngineResult.fromString(transform(engineResult.map(this::read, s -> s)));
    }

    private String read(File file) {
        try {
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            throw new GraphvizException("Error reading file", e);
        }
    }

    private String transform(String svg) {
        return engine.executeJavascript(
                "try{ result(rough(",
                replaceFonts(svg),
                ",{" + optionString(options) + "})); } catch(e){ error(e.toString()); };");
    }

    private String optionString(Map<String, Object> options) {
        return options.entrySet().stream()
                .map(e -> optionValue(e.getKey(), e.getValue()))
                .collect(joining(","));
    }

    private String optionValue(String key, Object value) {
        if (value instanceof Number) {
            return key + ":" + value.toString();
        }
        if (value instanceof FillStyle) {
            return optionString(((FillStyle) value).values);
        }
        return key + ":\"" + value + "\"";
    }

    private String replaceFonts(String svg) {
        final Matcher m = FONT_PATTERN.matcher(svg);
        final StringBuffer sb = new StringBuffer();
        while (m.find()) {
            final String r = replaceFont(m.group(1));
            m.appendReplacement(sb, "font-family=\"" + (r == null ? m.group(1) : r) + "\"");
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private String replaceFont(String font) {
        for (final Entry<Pattern, String> f : fonts) {
            if (f.getKey().matcher(font).matches()) {
                return f.getValue();
            }
        }
        return null;
    }

    private static String readCode() {
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
            try (final InputStream api = cl.getResourceAsStream("graphviz-rough.js")) {
                if (api == null) {
                    throw new AssertionError("graphviz-rough.js not found, corrupted jar file?");
                }
                return IoUtils.readStream(api);
            }
        } catch (IOException e) {
            throw new AssertionError("Could not read graphviz-rough.js", e);
        }
    }
}
