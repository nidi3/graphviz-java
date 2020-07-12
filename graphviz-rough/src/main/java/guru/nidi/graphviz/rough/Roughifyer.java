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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static guru.nidi.graphviz.engine.Format.*;
import static guru.nidi.graphviz.engine.GraphvizLoader.loadAsString;
import static java.util.stream.Collectors.joining;

public class Roughifyer implements GraphvizPostProcessor {
    private static final String CODE = readCode();
    private static final Pattern FONT_PATTERN = Pattern.compile("font-family=\"(.*?)\"");
    private final JavascriptEngine engine;
    private final List<Entry<Pattern, String>> fonts;
    private final Map<String, Object> options;

    public Roughifyer() {
        this(new V8JavascriptEngine());
    }

    public Roughifyer(JavascriptEngine engine) {
        this(engine, new ArrayList<>(), new HashMap<>());
        engine.executeJavascript(CODE);
    }

    private Roughifyer(JavascriptEngine engine, List<Entry<Pattern, String>> fonts, Map<String, Object> options) {
        this.engine = engine;
        this.options = options;
        this.fonts = fonts;
    }

    public Roughifyer font(String from, String to) {
        final List<Entry<Pattern, String>> fs = new ArrayList<>(fonts);
        fs.add(new SimpleEntry<>(Pattern.compile(from.replace("*", ".*?"), Pattern.CASE_INSENSITIVE), to));
        return new Roughifyer(engine, fs, options);
    }

    public Roughifyer roughness(double roughness) {
        return new Roughifyer(engine, fonts, options("roughness", roughness));
    }

    public Roughifyer bowing(double bowing) {
        return new Roughifyer(engine, fonts, options("bowing", bowing));
    }

    public Roughifyer fillStyle(FillStyle fillStyle) {
        return new Roughifyer(engine, fonts, options("fillStyle", fillStyle));
    }

    public Roughifyer curveStepCount(double curveStepCount) {
        return new Roughifyer(engine, fonts, options("curveStepCount", curveStepCount));
    }

    private Map<String, Object> options(String key, Object value) {
        final Map<String, Object> os = new HashMap<>(options);
        os.put(key, value);
        return os;
    }

    @Override
    public EngineResult postProcess(EngineResult result, Options options, ProcessOptions processOptions) {
        if (options.format != SVG && options.format != SVG_STANDALONE && options.format != PNG) {
            return result;
        }
        return EngineResult.fromString(transform(result.map(this::read, s -> s)));
    }

    private String read(File file) {
        try {
            return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
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
        return loadAsString("graphviz-rough.js");
    }
}
