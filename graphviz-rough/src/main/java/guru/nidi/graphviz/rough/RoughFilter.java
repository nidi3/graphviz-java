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

public class RoughFilter implements GraphvizFilter {
    private static final String CODE = readCode();
    private JavascriptEngine engine;

    public RoughFilter() {
        this(new GraphvizV8Engine());
    }

    public RoughFilter(JavascriptEngine engine) {
        this.engine = engine;
        engine.init();
        engine.executeJavascript(CODE);
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
        return engine.executeJavascript("try{ result(rough(", svg, ")); } catch(e){ error(e.toString()); };");
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
