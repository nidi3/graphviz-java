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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private static final Pattern FORMAT = Pattern.compile("format:'(.*?)'");
    private static final Pattern ENGINE = Pattern.compile("engine:'(.*?)'");
    private static final Pattern MEMORY = Pattern.compile("totalMemory:'(.*?)'");

    final Engine engine;
    final Format format;
    final Integer totalMemory;

    private Options(Engine engine, Format format, Integer totalMemory) {
        this.engine = engine;
        this.format = format;
        this.totalMemory = totalMemory;
    }

    public static Options create() {
        return new Options(Engine.DOT, null, null);
    }

    public static Options fromJson(String json) {
        final Matcher format = FORMAT.matcher(json);
        format.find();
        final Matcher engine = ENGINE.matcher(json);
        engine.find();
        final Matcher memory = MEMORY.matcher(json);
        final boolean hasMemory = memory.find();
        return new Options(
                Engine.valueOf(engine.group(1)),
                Format.valueOf(format.group(1)),
                hasMemory ? Integer.parseInt(memory.group(1)) : null);
    }

    public Options engine(Engine engine) {
        return new Options(engine, format, totalMemory);
    }

    public Options format(Format format) {
        return new Options(engine, format, totalMemory);
    }

    public Options totalMemory(Integer totalMemory) {
        return new Options(engine, format, totalMemory);
    }

    public String toJson(boolean raw) {
        final String form = "format:'" + (raw ? format : format.vizName) + "'";
        final String eng = ",engine:'" + (raw ? engine : engine.toString().toLowerCase()) + "'";
        final String mem = totalMemory == null ? "" : (",totalMemory:'" + totalMemory + "'");
        return "{" + form + eng + mem + "}";
    }
}
