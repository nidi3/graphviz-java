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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandRunner {
    private static final Logger LOG = LoggerFactory.getLogger(CommandRunner.class);

    private final Function<CommandLine, CommandLine> wrapperFunc;
    private final DefaultExecutor cmdExec;

    CommandRunner(Function<CommandLine, CommandLine> wrapperFunc, DefaultExecutor cmdExec) {
        this.wrapperFunc = wrapperFunc;
        this.cmdExec = cmdExec;
    }

    void exec(CommandLine cmd, @Nullable File workDir) throws IOException, InterruptedException {
        final CommandLine wrappedCmd = wrapperFunc.apply(cmd);
        cmdExec.execute(wrappedCmd, workDir);
    }

    public void exec(String cmd, @Nullable File workDir, String... args) throws IOException, InterruptedException {
        exec(cmd, workDir, args, true);
    }

    private void exec(String cmd, @Nullable File workDir, String[] args, boolean quote)
            throws IOException, InterruptedException {
        exec(new CommandLine(cmd).addArguments(args, quote), workDir);
    }

    private void exec(String cmd, @Nullable File workDir, List<String> args) throws IOException, InterruptedException {
        exec(cmd, workDir, args.toArray(new String[args.size()]));
    }

    void exec(String cmd, List<String> args) throws IOException, InterruptedException {
        exec(cmd, null, args);
    }

    void exec(String cmd) throws IOException, InterruptedException {
        exec(cmd, null, new String[0]);
    }

    // Cross-platform way of finding an executable in the $PATH.
    static Stream<Path> which(String program) {
        return which(program, Optional.ofNullable(System.getenv("PATH")).orElse(""));
    }

    private static Stream<Path> which(@Nullable String optProgram, @Nullable String pathEnvVar) {
        if (optProgram == null || "".equals(optProgram.trim()) || pathEnvVar == null || "".equals(pathEnvVar)) {
            return Stream.empty();
        }
        final String program = optProgram; //help code analysis
        return Arrays
                .stream(pathEnvVar.split(File.pathSeparator))
                .map(SystemUtils::pathOf)
                .filter(path -> Files.exists(path))
                .map(path -> {
                    try (Stream<Path> entries = Files.list(path)) {
                        return entries
                                // Filter on the filename
                                // Doing a case-sensitive compare here, that's not correct on windows ?
                                .filter(filePath -> program.equals(filePath.getFileName().toString()))

                                // Filter out folders
                                .filter(filePath -> Files.isRegularFile(filePath))

                                // Check if the file is executable
                                // Does this check work on Windows this way ?
                                .filter(Files::isExecutable)

                                // Consume the stream here - we're inside a try-with-resources
                                .collect(Collectors.toList())
                                .stream();
                    } catch (IOException e) {
                        LOG.error("Problem finding path for {}", program, e);
                        return Stream.<Path>empty();
                    }
                })
                .flatMap(stream -> stream);
    }

    static boolean isExecutableFound(String program) {
        return which(program).anyMatch(path -> true);
    }

    public static boolean isExecutableFound(String program, String envPath) {
        return which(program, envPath).anyMatch(path -> true);
    }

}