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
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Locale.ENGLISH;

/*
 * Copyright (C) 2015 Stefan Niederhauser (nidin@gmx.ch)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public final class Options {
    private static final Pattern
            FORMAT = Pattern.compile("format:'(.*?)'"),
            ENGINE = Pattern.compile("engine:'(.*?)'"),
            MEMORY = Pattern.compile("totalMemory:'(.*?)'"),
            Y_INVERT = Pattern.compile("yInvert:(.*?)"),
            BASE_DIR = Pattern.compile("basedir:'(.*?)'");

    final Engine engine;
    final Format format;
    @Nullable
    final Integer totalMemory;
    @Nullable
    final Boolean yInvert;
    final File basedir;

    private Options(Engine engine, Format format, @Nullable Integer totalMemory, @Nullable Boolean yInvert, File basedir) {
        this.engine = engine;
        this.format = format;
        this.totalMemory = totalMemory;
        this.yInvert = yInvert;
        this.basedir = basedir;
    }

    public static Options create() {
        return new Options(Engine.DOT, Format.SVG, null, null, new File("."));
    }

    public static Options fromJson(String json) {
        final Matcher format = FORMAT.matcher(json);
        format.find();
        final Matcher engine = ENGINE.matcher(json);
        engine.find();
        final Matcher memory = MEMORY.matcher(json);
        final boolean hasMemory = memory.find();
        final Matcher yInvert = Y_INVERT.matcher(json);
        final boolean hasYInvert = yInvert.find();
        final Matcher basedir = BASE_DIR.matcher(json);
        basedir.find();

        return new Options(
                Engine.valueOf(engine.group(1)),
                Format.valueOf(format.group(1)),
                hasMemory ? Integer.parseInt(memory.group(1)) : null,
                hasYInvert ? Boolean.parseBoolean(yInvert.group(1)) : null,
                new File(basedir.group(1)));
    }

    public Options engine(Engine engine) {
        return new Options(engine, format, totalMemory, yInvert, basedir);
    }

    public Options format(Format format) {
        return new Options(engine, format, totalMemory, yInvert, basedir);
    }

    public Options totalMemory(@Nullable Integer totalMemory) {
        return new Options(engine, format, totalMemory, yInvert, basedir);
    }

    public Options yInvert(@Nullable Boolean yInvert) {
        return new Options(engine, format, totalMemory, yInvert, basedir);
    }

    public Options basedir(File basedir) {
        return new Options(engine, format, totalMemory, yInvert, basedir);
    }

    public String toJson(boolean raw) {
        final String form = "format:'" + (raw ? format : format.vizName) + "'";
        final String eng = ",engine:'" + (raw ? engine : engine.toString().toLowerCase(ENGLISH)) + "'";
        final String mem = totalMemory == null ? "" : (",totalMemory:'" + totalMemory + "'");
        final String yInv = yInvert == null ? "" : (",yInvert:" + yInvert);
        final String base = ",basedir:'" + basedir.getAbsolutePath() + "'";
        return "{" + form + eng + mem + yInv + base + ",images: [ { path: '/Users/nidi/idea/graphviz-java-parent/out2.png', width: '400px', height: '300px' }]}";
    }
}
