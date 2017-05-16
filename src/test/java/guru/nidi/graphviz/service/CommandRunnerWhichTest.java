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

import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by toon on 08/02/17.
 */
public class CommandRunnerWhichTest {

    private final static String CMD_EXISTS = SystemUtils.IS_OS_WINDOWS ? "notepad.exe" : "ls";
    private final static String CMD_NOT_EXISTS = "foobeebaabeloo"; // <me> crosses fingers

    @Test
    public void whichTest() {
        final List<Path> paths = CommandRunner.which(CMD_EXISTS).collect(Collectors.toList());
        assertThat(paths, is(not(empty())));
    }

    @Test
    public void whichTestEmpty() {
        final List<Path> paths = CommandRunner.which(CMD_NOT_EXISTS).collect(Collectors.toList());
        assertThat(paths, is(empty()));
    }

    @Test
    public void isExecutableFound() throws Exception {
        assertThat(CommandRunner.isExecutableFound(CMD_EXISTS), is(true));
    }

    @Test
    public void isExecutableNotFound() {
        assertThat(CommandRunner.isExecutableFound(CMD_NOT_EXISTS), is(false));
    }

}
