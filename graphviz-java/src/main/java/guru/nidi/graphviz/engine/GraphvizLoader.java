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
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

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
        final byte[] buf = new byte[in.available()];
        int read, total = 0;
        while ((read = in.read(buf, total, Math.min(100000, buf.length - total))) > 0) {
            total += read;
        }
        return new String(buf, StandardCharsets.UTF_8);
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
