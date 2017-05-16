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

import guru.nidi.graphviz.service.CommandBuilder;
import guru.nidi.graphviz.service.CommandRunner;
import guru.nidi.graphviz.executor.DefaultExecutor;
import org.apache.commons.lang3.SystemUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by daank on 16-May-17.
 */
public class GraphvizCmdLineEngine extends AbstractGraphvizEngine {

    private CommandRunner cmdRunner;
    private static final String CMD_DOT = SystemUtils.IS_OS_WINDOWS ? "dot.exe" : "dot";

    public GraphvizCmdLineEngine() {
        this(null);
    }

    public GraphvizCmdLineEngine(EngineInitListener engineInitListener) {
        super(true, engineInitListener);
    }

    @Override
    protected String vizExec(String src, VizjsOptions vizjsOptions) {
        return src;
    }

    @Override
    protected void doInit() throws GraphvizException {
        this.cmdRunner = new CommandBuilder()
                .withShellWrapper(true)
                .withCommandExecutor(new DefaultExecutor())
                .build();

        if (!CommandRunner.isExecutableFound(CMD_DOT)) {
            throw new GraphvizException(CMD_DOT + " command not found");
        }


    }

    @Override
    protected String doExecute(String call) {
        try {
            // Write dot file to temp folder
            final Path tempDirPath = Files.createTempDirectory("temp");
            final File dotfile = new File(tempDirPath.toString() + "/dotfile.dot");
            final BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(dotfile), "UTF-8"));
            bw.write(call);
            bw.close();
            final int status = this.cmdRunner.exec(CMD_DOT + " -Tsvg dotfile.dot -ooutfile.svg",
                    new File(tempDirPath.toString()));
            if (status != 0) {
                throw new GraphvizException("Unable to parse dot request from command line");
            }

            // Read output file from temp folder
            final byte[] encoded = Files.readAllBytes(Paths.get(tempDirPath.toString() + "/outfile.svg"));

            return new String(encoded, "UTF-8");

        } catch (IOException e) {
            throw new GraphvizException("Failed to execute dot command", e);
        }

    }
}
