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
import java.util.stream.Stream;

import static guru.nidi.graphviz.service.SystemUtils.fileNameEquals;
import static java.util.stream.Collectors.toList;

public class CommandRunner {
    private static final Logger LOG = LoggerFactory.getLogger(CommandRunner.class);

    private final Function<CommandLine, CommandLine> wrapperFunc;
    private final CommandLineExecutor cmdExec;

    CommandRunner(Function<CommandLine, CommandLine> wrapperFunc, CommandLineExecutor cmdExec) {
        this.wrapperFunc = wrapperFunc;
        this.cmdExec = cmdExec;
    }

    void exec(String cmd, int timeout, List<String> args) throws IOException, InterruptedException {
        exec(cmd, null, timeout, args);
    }

    private void exec(String cmd, @Nullable File workDir, int timeout, List<String> args)
            throws IOException, InterruptedException {
        exec(cmd, workDir, timeout, args.toArray(new String[0]));
    }

    void exec(String cmd, int timeout) throws IOException, InterruptedException {
        exec(cmd, null, timeout);
    }

    public void exec(String cmd, @Nullable File workDir, int timeout, String... args)
            throws IOException, InterruptedException {
        exec(cmd, workDir, args, true, timeout);
    }

    private void exec(String cmd, @Nullable File workDir, String[] args, boolean quote, int timeout)
            throws IOException, InterruptedException {
        exec(new CommandLine(cmd).addArguments(args, quote), workDir, timeout);
    }

    void exec(CommandLine cmd, @Nullable File workDir, int timeout) throws IOException, InterruptedException {
        final CommandLine wrappedCmd = wrapperFunc.apply(cmd);
        cmdExec.execute(wrappedCmd, workDir, timeout);
    }

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
                                .filter(filePath -> fileNameEquals(program, filePath.getFileName().toString()))
                                .filter(CommandRunner::isExecutableFile)
                                .collect(toList())
                                .stream();
                    } catch (IOException e) {
                        LOG.error("Problem finding path for {}", program, e);
                        return Stream.<Path>empty();
                    }
                })
                .flatMap(stream -> stream);
    }

    public static boolean isExecutableFile(Path path) {
        return Files.isRegularFile(path) && Files.isExecutable(path); //TODO Does this check work on Windows this way ?
    }

    static boolean isExecutableFound(String program) {
        return which(program).anyMatch(path -> true);
    }

    public static boolean isExecutableFound(String program, String envPath) {
        return which(program, envPath).anyMatch(path -> true);
    }

}
