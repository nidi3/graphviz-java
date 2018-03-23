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

import static java.lang.Math.round;

class XdotTokenizer {
    private final String in;
    private int pos;

    public XdotTokenizer(String in) {
        this.in = in;
    }

    public String readString() {
        int len = readInt();
        String s = in.substring(pos + 1, pos + len + 1);
        pos += len + 2;
        return s;
    }

    private String readTillSpace() {
        int e = in.indexOf(' ', pos);
        String s = in.substring(pos, e);
        pos = e + 1;
        return s;
    }

    public char readChar() {
        final String s = readTillSpace();
        if (s.length() != 1) {
            throw new GraphvizException("Expected char, but found '" + s + "'");
        }
        return s.charAt(0);
    }

    public int readInt() {
        return Integer.parseInt(readTillSpace());
    }

    public int readIntDouble() {
        return (int) round(readDouble());
    }

    public double readDouble() {
        return Double.parseDouble(readTillSpace());
    }

    public int[][] readIntCoords(int len) {
        int[] x = new int[len];
        int[] y = new int[len];
        for (int i = 0; i < len; i++) {
            x[i] = readIntDouble();
            y[i] = readIntDouble();
        }
        return new int[][]{x, y};
    }

    public double[][] readDoubleCoords(int len) {
        double[] x = new double[len + 4];
        double[] y = new double[len + 4];
        for (int i = 2; i < len + 2; i++) {
            x[i] = readDouble();
            y[i] = readDouble();
        }
        x[0] = x[1] = x[2];
        y[0] = y[1] = y[2];
        x[len + 3] = x[len + 2] = x[len + 1];
        y[len + 3] = y[len + 2] = y[len + 1];
        return new double[][]{x, y};
    }

    public boolean hasMore() {
        return pos < in.length() - 1;
    }
}
