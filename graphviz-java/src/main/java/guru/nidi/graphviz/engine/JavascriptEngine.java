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

/**
 * A javascript engine is NOT thread safe.
 * The engine must provide the global result(s: String) and error(s: String) functions.
 */
public interface JavascriptEngine extends AutoCloseable {
    /**
     * Execute a piece of javascript code.
     *
     * @param raw the javascript code
     */
    void executeJavascript(String raw);

    /**
     * Execute a piece of javascript code with a given string parameter.
     * At the end, the function must either call result(s: String) or error(s: String).
     *
     * @param pre  Prefixed javascript code
     * @param src  The string parameter, does not need to be escaped.
     * @param post Postfixed javascript code
     * @return the value given to result or error
     */
    String executeJavascript(String pre, String src, String post);
}
