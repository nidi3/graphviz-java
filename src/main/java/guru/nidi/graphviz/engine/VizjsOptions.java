package guru.nidi.graphviz.engine;

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
final class VizjsOptions {
    final Engine engine;
    final Format format;
    final Integer totalMemory;

    private VizjsOptions(Engine engine, Format format, Integer totalMemory) {
        this.engine = engine;
        this.format = format;
        this.totalMemory = totalMemory;
    }

    public static VizjsOptions create() {
        return new VizjsOptions(Engine.DOT, null, null);
    }

    public VizjsOptions engine(Engine engine) {
        return new VizjsOptions(engine, format, totalMemory);
    }

    public VizjsOptions format(Format format) {
        return new VizjsOptions(engine, format, totalMemory);
    }

    public VizjsOptions totalMemory(int totalMemory) {
        return new VizjsOptions(engine, format, totalMemory);
    }

    public String toJson() {
        final String form = "format:'" + format.vizName + "'";
        final String eng = ",engine:'" + engine.toString().toLowerCase() + "'";
        final String mem = totalMemory == null ? "" : (",totalMemory:'" + totalMemory + "'");
        return "{" + form + eng + mem + "}";
    }
}
