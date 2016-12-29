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
package guru.nidi.graphviz.parse;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import static guru.nidi.graphviz.parse.Lexer.Token.*;

class Lexer {
    public static final class Token {
        public static final int
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
        public final int type;
        public final int subtype;
        public final String value;

        public Token(int type, String value) {
            this(type, -1, value);
        }

        public Token(int type, int subtype, String value) {
            this.type = type;
            this.subtype = subtype;
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Token token = (Token) o;

            if (type != token.type) {
                return false;
            }
            if (subtype != token.subtype) {
                return false;
            }
            return value.equals(token.value);

        }

        @Override
        public int hashCode() {
            int result = type;
            result = 31 * result + subtype;
            result = 31 * result + value.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return type + (subtype >= 0 ? "(" + subtype + ")" : "") + "`" + value + "`";
        }
    }

    private static final Map<String, Integer> keywords = new HashMap<>();

    static {
        keywords.put("strict", Token.STRICT);
        keywords.put("graph", Token.GRAPH);
        keywords.put("digraph", Token.DIGRAPH);
        keywords.put("node", Token.NODE);
        keywords.put("edge", Token.EDGE);
        keywords.put("subgraph", Token.SUBGRAPH);
    }

    private static final char CH_EOF = (char) -1;
    private final PushbackReader in;
    private char ch;

    public Lexer(Reader in) throws IOException {
        this.in = new PushbackReader(in);
        readChar();
    }

    public Token token() throws IOException {
        final Token sym = symbol();
        if (sym != null) {
            readChar();
            return sym;
        }
        return numeralOrIdent();
    }

    private Token symbol() throws IOException {
        switch (ch) {
            case CH_EOF:
                return new Token(EOF, "" + ch);
            case ';':
                return new Token(SEMICOLON, "" + ch);
            case ',':
                return new Token(COMMA, "" + ch);
            case '{':
                return new Token(BRACE_OPEN, "" + ch);
            case '}':
                return new Token(BRACE_CLOSE, "" + ch);
            case '=':
                return new Token(EQUAL, "" + ch);
            case '[':
                return new Token(BRACKET_OPEN, "" + ch);
            case ']':
                return new Token(BRACKET_CLOSE, "" + ch);
            case ':':
                return new Token(COLON, "" + ch);
            case '-':
                char next = readRawChar();
                if (next == '-') {
                    return new Token(MINUS_MINUS, "--");
                }
                if (next == '>') {
                    return new Token(ARROW, "->");
                }
                unread('-', next);
                return null;
            default:
                return null;
        }
    }

    private Token numeralOrIdent() throws IOException {
        if (ch == '-' || ch == '.' || (ch >= '0' && ch <= '9')) {
            return numeral();
        }
        return ident();
    }

    private Token numeral() throws IOException {
        final StringBuilder s = new StringBuilder();
        do {
            s.append(ch);
            readRawChar();
        } while (ch == '.' || (ch >= '0' && ch <= '9'));
        sync();
        return new Token(ID, SUB_NUMERAL, s.toString());
    }

    private Token ident() throws IOException {
        if (ch == '"') {
            return quotedIdent();
        }
        if (ch == '<') {
            return htmlIdent();
        }
        if (isIdentStart(ch)) {
            return simpleIdent();
        }
        throw new ParserException("Found unexpected character '" + ch + "'");
    }

    private boolean isIdentStart(char c) {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= 128 && ch <= 255) || ch == '_';
    }

    private Token quotedIdent() throws IOException {
        final StringBuilder s = new StringBuilder();
        readRawChar();
        do {
            s.append(ch);
            readRawChar();
            if (s.length() > 0) {
                if (ch == '"' && s.charAt(s.length() - 1) == '\\') {
                    s.replace(s.length() - 1, s.length(), "\"");
                    readRawChar();
                }
                if (ch == '\n' && s.charAt(s.length() - 1) == '\\') {
                    s.delete(s.length() - 1, s.length());
                    readRawChar();
                }
            }
        } while (ch != '"' && ch != CH_EOF);
        readChar();
        return new Token(ID, SUB_QUOTED, s.toString());
    }

    private Token htmlIdent() throws IOException {
        final StringBuilder s = new StringBuilder();
        int level = 1;
        readRawChar();
        do {
            s.append(ch);
            readRawChar();
            if (ch == '<') {
                level++;
            }
            if (ch == '>') {
                level--;
            }
        } while ((ch != '>' || level > 0) && ch != CH_EOF);
        readChar();
        return new Token(ID, SUB_HTML, s.toString());
    }

    private Token simpleIdent() throws IOException {
        final StringBuilder s = new StringBuilder();
        do {
            s.append(ch);
            readRawChar();
        } while ((isIdentStart(ch) || (ch >= '0' && ch <= '9')) && ch != CH_EOF);
        sync();
        final Integer key = keywords.get(s.toString().toLowerCase());
        return key == null ? new Token(ID, SUB_SIMPLE, s.toString()) : new Token(key, s.toString());
    }

    private void sync() throws IOException {
        if (ch <= ' ') {
            readChar();
        }
    }

    private char readChar() throws IOException {
        do {
            readRawChar();
            if (ch == '/') {
                char next = readRawChar();
                if (next == '/') {
                    do {
                        readRawChar();
                    } while (ch != '\n' && ch != CH_EOF);
                } else if (next == '*') {
                    do {
                        do {
                            readRawChar();
                        } while (ch != '*' && ch != CH_EOF);
                        readRawChar();
                    } while (ch != '/' && ch != CH_EOF);
                    readRawChar();
                } else {
                    unread('/', next);
                }
            } else if (ch == '\n') {
                char next = readRawChar();
                if (next == '#') {
                    do {
                        readRawChar();
                    } while (ch != '\n' && ch != CH_EOF);
                } else {
                    unread('\n', next);
                }
            }
        } while (ch <= ' ' && ch != CH_EOF);
        return ch;
    }

    private char readRawChar() throws IOException {
        return ch = (char) in.read();
    }

    private void unread(char before, char next) throws IOException {
        ch = before;
        in.unread(next);
    }
}
