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
package guru.nidi.graphviz.executor;

import org.apache.commons.exec.CommandLine;

import java.io.*;
import java.nio.file.Files;

/**
 * Created by daank on 23-May-17.
 */
public class MockDotExecutor implements ICommandExecutor {

    @Override
    public int execute(CommandLine cmd, File workingDirectory) throws InterruptedException, IOException {
        System.out.println("MockDotExecutor, CMD: " + cmd.toString());

        if ("[cmd, /C, dot.exe -Tsvg dotfile.dot -ooutfile.svg]".equalsIgnoreCase(cmd.toString())) {

            final File svgInput = new File(getClass().getClassLoader().getResource("outfile1.svg").getFile());
            final File svgOutputFile = new File(workingDirectory.getAbsolutePath() + "/outfile.svg");
            Files.copy(svgInput.toPath(), svgOutputFile.toPath());

            return 0;
        }

        return 1;
    }
}
