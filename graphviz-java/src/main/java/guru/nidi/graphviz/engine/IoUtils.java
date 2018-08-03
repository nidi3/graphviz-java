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

import java.io.*;
import java.nio.charset.StandardCharsets;

final class IoUtils {
    private IoUtils() {
    }

    public static String readStream(InputStream in) throws IOException {
        final byte[] buf = new byte[in.available()];
        int read, total = 0;
        while ((read = in.read(buf, total, Math.min(100000, buf.length - total))) > 0) {
            total += read;
        }
        return new String(buf, StandardCharsets.UTF_8);
    }

    public static boolean isOnClasspath(String clazz) {
        try {
            Class.forName(clazz);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static void closeQuietly(Closeable c) {
        try {
            c.close();
        } catch (IOException e) {
            //ignore
        }
    }
}
