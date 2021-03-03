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

import javax.annotation.Nullable;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static guru.nidi.graphviz.parse.Token.*;
import static java.util.Locale.ENGLISH;

class Lexer {
    private static final Map<String, Integer> KEYWORDS = new HashMap<>();

    static {
        KEYWORDS.put("strict", Token.STRICT);
        KEYWORDS.put("graph", Token.GRAPH);
        KEYWORDS.put("digraph", Token.DIGRAPH);
        KEYWORDS.put("node", Token.NODE);
        KEYWORDS.put("edge", Token.EDGE);
        KEYWORDS.put("subgraph", Token.SUBGRAPH);
    }

    private static final char CH_EOF = (char) -1;
    private final PushbackReader in;
    private char ch;
    Position pos;

    Lexer(Reader in, String name) throws IOException {
        this.in = new PushbackReader(in);
        pos = new Position(name);
        readChar();
    }

    Token token() throws IOException {
        final Token sym = symbol();
        if (sym != null) {
            readChar();
            return sym;
        }
        return numeralOrIdent();
    }

    @Nullable
    private Token symbol() throws IOException {
        switch (ch) {
            case CH_EOF:
                return new Token(EOF, ch);
            case ';':
                return new Token(SEMICOLON, ch);
            case ',':
                return new Token(COMMA, ch);
            case '{':
                return new Token(BRACE_OPEN, ch);
            case '}':
                return new Token(BRACE_CLOSE, ch);
            case '=':
                return new Token(EQUAL, ch);
            case '[':
                return new Token(BRACKET_OPEN, ch);
            case ']':
                return new Token(BRACKET_CLOSE, ch);
            case ':':
                return new Token(COLON, ch);
            case '-':
                final char next = readRawChar();
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
        if (isIdentStart()) {
            return simpleIdent();
        }
        throw new ParserException(pos, "Found unexpected character '" + ch + "'");
    }

    private boolean isIdentStart() {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= 128 && ch <= 255) || ch == '_';
    }

    private Token quotedIdent() throws IOException {
        final StringBuilder s = new StringBuilder();
        readRawChar();
        while (ch != '"' && ch != CH_EOF) {
            s.append(ch);
            readRawChar();
            if (ch == '"' && s.charAt(s.length() - 1) == '\\') {
                s.replace(s.length() - 1, s.length(), "\"");
                readRawChar();
            }
            if (ch == '\n' && s.charAt(s.length() - 1) == '\\') {
                s.delete(s.length() - 1, s.length());
                readRawChar();
            }
        }
        readChar();
        return new Token(ID, SUB_QUOTED, s.toString());
    }

    private Token htmlIdent() throws IOException {
        final StringBuilder s = new StringBuilder();
        int level = 1;
        readRawChar();
        level = htmlLevel(level, ch);
        while ((ch != '>' || level > 0) && ch != CH_EOF) {
            s.append(ch);
            readRawChar();
            level = htmlLevel(level, ch);
        }
        readChar();
        return new Token(ID, SUB_HTML, s.toString());
    }

    private int htmlLevel(int level, char ch) {
        if (ch == '<') {
            return level + 1;
        }
        if (ch == '>') {
            return level - 1;
        }
        return level;
    }

    private Token simpleIdent() throws IOException {
        final StringBuilder s = new StringBuilder();
        do {
            s.append(ch);
            readRawChar();
        } while ((isIdentStart() || (ch >= '0' && ch <= '9')) && ch != CH_EOF);
        sync();
        final Integer key = KEYWORDS.get(s.toString().toLowerCase(ENGLISH));
        return key == null ? new Token(ID, SUB_SIMPLE, s.toString()) : new Token(key, s.toString());
    }

    private void sync() throws IOException {
        if (ch <= ' ' && ch != CH_EOF) {
            readChar();
        }
    }

    private void readChar() throws IOException {
        do {
            readRawChar();
            if (ch == '/') {
                readComment();
            }
        } while (ch <= ' ' && ch != CH_EOF);
    }

    private void readComment() throws IOException {
        final char next = readRawChar();
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
    }

    private char readRawChar() throws IOException {
        if (ch != CH_EOF) {
            doReadRawChar();
            if (ch == '\n') {
                pos.newLine();
                do {
                    doReadRawChar();
                } while (ch <= ' ');
                if (ch == '#') {
                    do {
                        doReadRawChar();
                    } while (ch != '\n' && ch != CH_EOF);
                    pos.newChar();
                } else {
                    unread('\n', ch);
                }
            }
        }
        return ch;
    }

    private void doReadRawChar() throws IOException {
        pos.newChar();
        ch = (char) in.read();
    }

    private void unread(char before, char next) throws IOException {
        pos.lastChar();
        ch = before;
        in.unread(next);
    }
}
