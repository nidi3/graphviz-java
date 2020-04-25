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

import static guru.nidi.graphviz.engine.GraphvizLoader.isOnClasspath;

public class GraphvizV8Engine extends AbstractJsGraphvizEngine {
    static final boolean AVAILABLE = AbstractJsGraphvizEngine.AVAILABLE
            && isOnClasspath("com/eclipsesource/v8/V8.class");

    public GraphvizV8Engine() {
        this(null);
    }

    public GraphvizV8Engine(@Nullable String extractionPath) {
        super(true, () -> new V8JavascriptEngine(extractionPath));
        if (!AVAILABLE) {
            throw new MissingDependencyException("V8 engine is not available.", "com.eclipsesource.j2v8:j2v8_*");
        }
    }
}
