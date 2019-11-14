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

public class Position {
    private final String name;
    private int line;
    private int col;

    Position(String name) {
        this(name, 1, 0);
    }

    private Position(String name, int line, int col) {
        this.name = name;
        this.line = line;
        this.col = col;
    }

    Position copy(int delta) {
        return new Position(name, line, col + delta);
    }

    void newLine() {
        col = 0;
        line++;
    }

    void newChar() {
        col++;
    }

    void lastChar() {
        col--;
    }

    public String getName() {
        return name;
    }

    public int getLine() {
        return line;
    }

    public int getCol() {
        return col;
    }

    @Override
    public String toString() {
        return name + ":" + line + ":" + col;
    }
}
