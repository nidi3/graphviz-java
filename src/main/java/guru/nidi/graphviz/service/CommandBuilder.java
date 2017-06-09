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

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by toon on 07/02/17.
 */
public class CommandBuilder {


    private boolean shellWrapper;
    private DefaultExecutor cmdExec;

    public CommandBuilder withShellWrapper(boolean shellWrapper) {
        this.shellWrapper = shellWrapper;
        return this;
    }

    public CommandBuilder withCommandExecutor(DefaultExecutor cmdExec) {
        this.cmdExec = cmdExec;
        return this;
    }

    public CommandRunner build() {
        return new CommandRunner(
                this.getShellWrapperOrDefault(this.shellWrapper),
                this.getCmdExecutorOrDefault(this.cmdExec));
    }

    public static DefaultExecutor getCmdExecutorOrDefault(DefaultExecutor cmdExec) {
        if (cmdExec != null) {
            return cmdExec;
        }
        return new DefaultExecutor();
    }


    public static Function<CommandLine, CommandLine> getShellWrapperOrDefault(boolean shellWrapper) {

        if (!shellWrapper) {
            return Function.identity();
        }

        if (SystemUtils.IS_OS_WINDOWS) {
            return getWindowsShellWrapperFunc();
        }
        if (SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC) {
            return getLinuxShellWrapperFunc();
        } else {
            throw new IllegalStateException("Unsupported OS");
        }
    }

    static Function<CommandLine, CommandLine> getWindowsShellWrapperFunc() {
        return (cmd) -> {
            final CommandLine res = new CommandLine("cmd")
                    .addArgument("/C")
                    .addArguments(cmd.toStrings(), false);

            return res;
        };
    }

    static Function<CommandLine, CommandLine> getLinuxShellWrapperFunc() {
        return (cmd) -> {
            final String originalCmd = Stream.concat(
                    Arrays.stream(new String[]{cmd.getExecutable()}),
                    Arrays.stream(cmd.getArguments())
            ).collect(Collectors.joining(" "));

            final CommandLine ret = new CommandLine("/bin/sh")
                    .addArgument("-c")
                    .addArgument(originalCmd, false);
            return ret;
        };
    }


}