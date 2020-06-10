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

package guru.nidi.graphviz.service;

import org.apache.commons.exec.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public final class SystemUtils {
    private static final Logger LOG = LoggerFactory.getLogger(SystemUtils.class);

    @Nullable
    private static final String OS_NAME = getSystemProperty("os.name");
    private static final boolean
            IS_OS_WINDOWS = getOsMatchesName("Windows"),
            IS_OS_MAC = getOsMatchesName("Mac"),
            IS_OS_LINUX = getOsMatchesName("Linux") || getOsMatchesName("LINUX");

    private SystemUtils() {
    }

    @Nullable
    private static String getSystemProperty(String property) {
        try {
            return System.getProperty(property);
        } catch (SecurityException e) {
            LOG.error("Caught a SecurityException reading the system property '{}'."
                    + "The SystemUtils property value will default to null.", property);
            return null;
        }
    }

    private static boolean getOsMatchesName(String osNamePrefix) {
        return isOsNameMatch(OS_NAME, osNamePrefix);
    }

    private static boolean isOsNameMatch(@Nullable String osName, String osNamePrefix) {
        return osName != null && osName.startsWith(osNamePrefix);
    }

    public static Path pathOf(String path) {
        return Paths.get(IS_OS_WINDOWS ? path.replace("\"", "") : path);
    }

    public static String uriPathOf(String path) {
        if (IS_OS_WINDOWS) {
            return (path.startsWith(":\\", 1) ? "/" : "") + path.replace('\\', '/');
        }
        return path;
    }

    public static String uriPathOf(File path) {
        return uriPathOf(path.getAbsolutePath());
    }

    public static List<String> executableNames(String filename) {
        return IS_OS_WINDOWS
                ? asList(filename + ".exe", filename + ".bat", filename + ".cmd")
                : singletonList(filename);
    }

    public static boolean fileNameEquals(String file1, String file2) {
        return IS_OS_WINDOWS ? file1.equalsIgnoreCase(file2) : file1.equals(file2);
    }

    public static Function<CommandLine, CommandLine> getShellWrapperOrDefault(boolean shellWrapper) {
        if (!shellWrapper) {
            return Function.identity();
        }
        if (IS_OS_WINDOWS) {
            return getWindowsShellWrapperFunc();
        }
        if (IS_OS_LINUX || IS_OS_MAC) {
            return getLinuxShellWrapperFunc();
        }
        throw new IllegalStateException("Unsupported OS");
    }

    private static Function<CommandLine, CommandLine> getWindowsShellWrapperFunc() {
        return (cmd) -> new CommandLine("cmd")
                .addArgument("/C")
                .addArguments(cmd.toStrings(), false);
    }

    private static Function<CommandLine, CommandLine> getLinuxShellWrapperFunc() {
        return (cmd) -> {
            final String originalCmd = Stream.concat(
                    Arrays.stream(new String[]{cmd.getExecutable()}),
                    Arrays.stream(cmd.getArguments())
            ).collect(Collectors.joining(" "));

            return new CommandLine("/bin/sh")
                    .addArgument("-c")
                    .addArgument(originalCmd, false);
        };
    }

}
