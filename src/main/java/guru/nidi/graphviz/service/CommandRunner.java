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

import guru.nidi.graphviz.executor.ICommandExecutor;
import org.apache.commons.exec.CommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandRunner {

    private final Function<CommandLine, CommandLine> wrapperFunc;
    private final ICommandExecutor cmdExec;

    CommandRunner(Function<CommandLine, CommandLine> wrapperFunc, ICommandExecutor cmdExec) {
        this.wrapperFunc = wrapperFunc;
        this.cmdExec = cmdExec;
    }


    public int exec(CommandLine cmd, File workingDirectory) {
        try {
            final CommandLine wrappedCmd = this.wrapperFunc.apply(cmd);
            return this.cmdExec.execute(wrappedCmd, workingDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public int exec(CommandLine cmd) {
        return this.exec(cmd, null);
    }


    public int exec(String cmd, File workingDirectory, String... args) {
        return this.exec(cmd, workingDirectory, args, true);
    }

    public int exec(String cmd, File workingDirectory, String[] args, boolean handleQuoting) {
        return this.exec(new CommandLine(cmd).addArguments(args, handleQuoting), workingDirectory);
    }


    public int exec(String cmd, File workingDirectory, List<String> args) {
        return exec(cmd, workingDirectory, args.toArray(new String[args.size()]));
    }

    public int exec(String cmd, List<String> args) {
        return exec(cmd, null, args);
    }

    public int exec(String cmd) {
        return exec(cmd, null, new String[0]);
    }

    // Cross-platform way of finding an executable in the $PATH.
    public static Stream<Path> which(String program) {
        if (program == null || "".equals(program.trim())) {
            return Stream.empty();
        }

        return Arrays
                .stream(Optional.ofNullable(System.getenv("PATH")).orElse("").split(File.pathSeparator))
                .map(path -> Paths.get(path))

                // Env PATH could contain paths that do not exist
                .filter(path -> Files.exists(path))
                .map(path -> {
                    try (Stream<Path> entries = Files.list(path)) {

                        return entries
                                // Filter on the filename
                                // Doing a case-senstive compare here, thats not correct on windows ?
                                .filter(filePath -> program.equals(filePath.getFileName().toString()))

                                // Filter out folders
                                .filter(filePath -> Files.isRegularFile(filePath))

                                // Check if the file is executable
                                // Does this check work on Windows this way ?
                                .filter(filePath -> Files.isExecutable(filePath))

                                // Consume the stream here - we're inside a try-with-resources
                                .collect(Collectors.toList())
                                .stream();
                    } catch (IOException e) {
                        e.printStackTrace();
                        // Skip
                        return Stream.<Path>empty();
                    }
                })
                .flatMap(stream -> stream);
    }

    public static boolean isExecutableFound(String program) {
        return CommandRunner.which(program).anyMatch(path -> true);
    }

}