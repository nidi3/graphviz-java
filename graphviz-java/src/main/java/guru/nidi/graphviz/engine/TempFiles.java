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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

final class TempFiles {
    private static final long TIME_TO_LIVE = 1000 * 3600 * 24 * 3;
    private static final File TEMP_DIR = new File(
            System.getProperty("java.io.tmpdir") + File.separator + "GraphvizJava");

    static {
        mkdir();
        cleanup();
    }

    private TempFiles() {
    }

    static Path tempDir(String name) throws IOException {
        return Files.createTempDirectory(TEMP_DIR.toPath(), name);
    }


    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void mkdir() {
        TEMP_DIR.mkdirs();
    }

    private static void cleanup() {
        final File[] files = TEMP_DIR.listFiles();
        if (files != null) {
            for (final File file : files) {
                if (System.currentTimeMillis() - file.lastModified() > TIME_TO_LIVE) {
                    deleteDir(file);
                }
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void deleteDir(File f) {
        if (f.isFile()) {
            f.delete();
        } else if (f.isDirectory()) {
            final File[] files = f.listFiles();
            if (files != null) {
                for (final File file : files) {
                    deleteDir(file);
                }
            }
            f.delete();
        }
    }
}
