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
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.verify;

/**
 * @author mhgam
 */
public class CommandRunnerTest {

    @Captor
    private ArgumentCaptor<CommandLine> runEchoCaptor;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRunEchoHelloWorld() throws IOException, InterruptedException {
        final CommandLine expected = SystemUtils.IS_OS_WINDOWS
                ? CommandLine.parse("cmd /C echo hello world")
                : CommandLine.parse("/bin/sh -c").addArgument("echo hello world", false);

        final DefaultExecutor cmdExecMock = Mockito.mock(DefaultExecutor.class);
        final CommandRunner cmdRunner = new CommandBuilder()
                .withShellWrapper(true)
                .withCommandExecutor(cmdExecMock)
                .build();

        cmdRunner.exec(CommandLine.parse("echo hello world"), null);

        verify(cmdExecMock).execute(runEchoCaptor.capture(), (File) isNull());
        assertEquals(expected.toString(), runEchoCaptor.getValue().toString());
    }

    @Test
    public void testResultCode() throws IOException, InterruptedException {
        final CommandRunner cmdRunner = new CommandBuilder()
                .withShellWrapper(true)
                .build();

        final int result = cmdRunner.exec("echo", Arrays.asList("hello", "world"));
        assertEquals(0, result);
    }

    @Test
    public void testEnv() throws IOException, InterruptedException {
        final CommandRunner cmdRunner = new CommandBuilder()
                .withShellWrapper(true)
                .build();

        final int result = cmdRunner.exec("env");
        assertEquals(0, result);
    }
}
