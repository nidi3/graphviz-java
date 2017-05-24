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

import guru.nidi.graphviz.executor.ICommandExecutor;
import guru.nidi.graphviz.service.CommandBuilder;
import guru.nidi.graphviz.service.CommandRunner;
import guru.nidi.graphviz.executor.DefaultExecutor;
import guru.nidi.graphviz.service.SystemUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Created by daank on 16-May-17.
 */
public class GraphvizCmdLineEngine extends AbstractGraphvizEngine {

    public static final String CMD_DOT = SystemUtils.IS_OS_WINDOWS ? "dot.exe" : "dot";

    private CommandRunner cmdRunner;
    private String envPath;
    private ICommandExecutor executor;

    public GraphvizCmdLineEngine() {
        this(null);
    }

    public GraphvizCmdLineEngine(EngineInitListener engineInitListener) {

        this(engineInitListener, Optional.ofNullable(System.getenv("PATH")).orElse(""), new DefaultExecutor());
    }

    public GraphvizCmdLineEngine(EngineInitListener engineInitListener, String envPath, ICommandExecutor executor) {
        super(false, engineInitListener);
        this.envPath = envPath;
        this.executor = executor;
    }


    @Override
    protected String vizExec(String src, VizjsOptions vizjsOptions) {
        return src;
    }

    @Override
    protected void doInit() throws GraphvizException {
        this.cmdRunner = new CommandBuilder()
                .withShellWrapper(true)
                .withCommandExecutor(this.executor)
                .build();
    }

    @Override
    protected String doExecute(String call) {

        if (!CommandRunner.isExecutableFound(CMD_DOT, this.envPath)) {
            throw new GraphvizException(CMD_DOT + " command not found");
        }

        try {
            // Write dot file to temp folder
            final Path tempDirPath = Files.createTempDirectory(getOrCreateTempDirectory().toPath(), "DotEngine");
            final File dotfile = new File(tempDirPath.toString() + "/dotfile.dot");
            final BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(dotfile), "UTF-8"));
            bw.write(call);
            bw.close();
            final int status = this.cmdRunner.exec(CMD_DOT + " -Tsvg dotfile.dot -ooutfile.svg",
                    new File(tempDirPath.toString()));
            if (status != 0) {
                throw new GraphvizException("Dot command didn't succeed");
            }

            // Read output file from temp folder
            final byte[] encoded = Files.readAllBytes(Paths.get(tempDirPath.toString() + "/outfile.svg"));

            return new String(encoded, "UTF-8");

        } catch (IOException e) {
            throw new GraphvizException("Failed to execute dot command", e);
        }

    }

    private File getOrCreateTempDirectory() {
        File tempDir;
        tempDir = new File(System.getProperty("java.io.tmpdir") + File.separator + "GraphvizJava");
        if (!tempDir.exists() && tempDir.mkdir()) {
            System.out.println("Created GraphvizJava temporary directory");
        }
        return tempDir;
    }
}
