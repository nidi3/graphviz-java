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

import java.util.Objects;

final class Token {
    static final int
            EOF = 0,
            SEMICOLON = 1,
            COMMA = 2,
            BRACE_OPEN = 3,
            BRACE_CLOSE = 4,
            EQUAL = 5,
            BRACKET_OPEN = 6,
            BRACKET_CLOSE = 7,
            COLON = 8,
            STRICT = 9,
            GRAPH = 10,
            DIGRAPH = 11,
            NODE = 12,
            EDGE = 13,
            SUBGRAPH = 14,
            ID = 16,
            MINUS_MINUS = 18,
            ARROW = 19,
            SUB_SIMPLE = 1,
            SUB_NUMERAL = 2,
            SUB_QUOTED = 3,
            SUB_HTML = 4;
    final int type;
    final int subtype;
    final String value;

    Token(int type, String value) {
        this(type, -1, value);
    }

    Token(int type, char value) {
        this(type, -1, Character.toString(value));
    }

    Token(int type, int subtype, String value) {
        this.type = type;
        this.subtype = subtype;
        this.value = value;
    }

    static String desc(int type) {
        switch (type) {
            case ID:
                return "identifier";
            case EQUAL:
                return "=";
            case EOF:
                return "end of file";
            case BRACKET_OPEN:
                return "[";
            case BRACKET_CLOSE:
                return "]";
            case BRACE_OPEN:
                return "{";
            case BRACE_CLOSE:
                return "}";
            default:
                return "";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Token token = (Token) o;
        return type == token.type
                && subtype == token.subtype
                && Objects.equals(value, token.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, subtype, value);
    }

    @Override
    public String toString() {
        return type + (subtype >= 0 ? "(" + subtype + ")" : "") + "`" + value + "`";
    }
}
