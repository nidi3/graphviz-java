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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.verify;

/**
 * @author mhgam
 */
class CommandRunnerTest {

    @Captor
    @Nullable
    private ArgumentCaptor<CommandLine> runEchoCaptor;

    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testRunEchoHelloWorld() throws IOException, InterruptedException {
        final CommandLine expected = System.getProperty("os.name").contains("Windows")
                ? CommandLine.parse("cmd /C echo hello world")
                : CommandLine.parse("/bin/sh -c").addArgument("echo hello world", false);

        final CommandLineExecutor cmdExecMock = Mockito.mock(CommandLineExecutor.class);
        final CommandRunner cmdRunner = new CommandBuilder()
                .withShellWrapper(true)
                .withCommandExecutor(cmdExecMock)
                .build();

        cmdRunner.exec(CommandLine.parse("echo hello world"), null, 5000);

        verify(cmdExecMock).execute(runEchoCaptor.capture(), (File) isNull(), eq(5000));
        assertEquals(expected.toString(), runEchoCaptor.getValue().toString());
    }

    @Test
    void testResultCode() throws IOException, InterruptedException {
        final CommandRunner cmdRunner = new CommandBuilder()
                .withShellWrapper(true)
                .build();

        cmdRunner.exec("echo", 5000, Arrays.asList("hello", "world"));
    }

    @Test
    void testEnv() throws IOException, InterruptedException {
        final CommandRunner cmdRunner = new CommandBuilder()
                .withShellWrapper(true)
                .build();

        cmdRunner.exec("env", 5000);
    }
}
