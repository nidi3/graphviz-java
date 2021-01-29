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
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class EngineResult {
    @Nullable
    private final File file;
    @Nullable
    private final String string;

    private EngineResult(@Nullable File file, @Nullable String string) {
        this.file = file;
        this.string = string;
    }

    public static EngineResult fromFile(File file) {
        return new EngineResult(file, null);
    }

    public static EngineResult fromString(String string) {
        return new EngineResult(null, string);
    }

    public EngineResult mapString(Function<String, String> mapper) {
        return string == null ? this : fromString(mapper.apply(string));
    }

    public void consume(Consumer<File> fileConsumer, Consumer<String> stringConsumer) {
        if (string == null) {
            fileConsumer.accept(file);
        } else {
            stringConsumer.accept(string);
        }
        close();
    }

    public <T> T map(Function<File, T> fileMapper, Function<String, T> stringMapper) {
        final T res = string == null ? fileMapper.apply(file) : stringMapper.apply(string);
        close();
        return res;
    }

    public String asString() throws IOException {
        return mapIO(EngineResult::readFile, string -> string);
    }

    <T> T mapIO(IOFunction<File, T> fileMapper, IOFunction<String, T> stringMapper) throws IOException {
        final T res = string == null ? fileMapper.apply(file) : stringMapper.apply(string);
        close();
        return res;
    }

    private void close() {
        if (file != null) {
            file.delete();
        }
    }

    private static String readFile(File file) throws IOException {
        final StringBuilder s = new StringBuilder();
        try (Stream<String> stream = Files.lines(file.toPath(), UTF_8)) {
            stream.forEach(line -> s.append(line).append("\n"));
        }
        return s.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EngineResult that = (EngineResult) o;
        return Objects.equals(file, that.file) && Objects.equals(string, that.string);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, string);
    }

    @Override
    public String toString() {
        return "EngineResult{"
                + "file=" + file
                + ", string='" + string + '\''
                + '}';
    }
}
