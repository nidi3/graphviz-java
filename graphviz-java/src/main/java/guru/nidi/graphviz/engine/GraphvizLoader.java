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
import java.io.*;
import java.util.function.Supplier;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class GraphvizLoader {
    private static Supplier<ClassLoader> classLoaderSupplier = GraphvizLoader.class::getClassLoader;

    private GraphvizLoader() {
    }

    public static void useClassLoader(Supplier<ClassLoader> supplier) {
        classLoaderSupplier = supplier;
    }

    public static String loadAsString(String name) {
        try (final InputStream in = load(name)) {
            if (in == null) {
                throw new GraphvizException("Could not find resource '" + name + "' on the classpath.");
            }
            return readAsString(in);
        } catch (IOException e) {
            throw new GraphvizException("Problem reading resource '" + name + "' from the classpath.", e);
        }
    }

    static String readAsString(InputStream in) throws IOException {
        return new String(readAsBytes(in), UTF_8);
    }

    static byte[] readAsBytes(InputStream in) throws IOException {
        final ByteArrayOutputStream res = new ByteArrayOutputStream();
        final byte[] buf = new byte[100000];
        int read;
        while ((read = in.read(buf)) > 0) {
            res.write(buf, 0, read);
        }
        res.flush();
        return res.toByteArray();
    }

    static boolean isOnClasspath(String resource) {
        return classLoader().getResource(resource) != null;
    }

    static ClassLoader classLoader() {
        return classLoaderSupplier.get();
    }

    static void closeQuietly(AutoCloseable c) {
        try {
            c.close();
        } catch (Exception e) {
            //ignore
        }
    }

    @Nullable
    private static InputStream load(String name) {
        return classLoader().getResourceAsStream(name);
    }
}
