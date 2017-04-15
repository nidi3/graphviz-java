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
package guru.nidi.graphviz.parse;

class Position {
    final String name;
    int line;
    int col;

    public Position(String name) {
        this.name = name;
        line = col = 1;
    }

    public void newLine() {
        col = 1;
        line++;
    }

    public void newChar() {
        col++;
    }

    @Override
    public String toString() {
        return name + ":" + line + ":" + col;
    }
}
