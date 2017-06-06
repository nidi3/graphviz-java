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
import guru.nidi.graphviz.service.DefaultExecutor;
import guru.nidi.graphviz.service.SystemUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Created by daank on 16-May-17.
 */
public class GraphvizCmdLineEngine extends AbstractGraphvizEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractGraphvizEngine.class);

    public static final String CMD_DOT = SystemUtils.IS_OS_WINDOWS ? "dot.exe" : "dot";

    private CommandRunner cmdRunner;
    private String envPath;
    private DefaultExecutor executor;

    private String dotOutputFilePath;
    private String dotOutputFileName;

    public GraphvizCmdLineEngine() {
        this(null);
    }

    public GraphvizCmdLineEngine(EngineInitListener engineInitListener) {

        this(engineInitListener, Optional.ofNullable(System.getenv("PATH")).orElse(""), new DefaultExecutor());
    }

    public GraphvizCmdLineEngine(EngineInitListener engineInitListener, String envPath, DefaultExecutor executor) {
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
            // Create a temporary file to save the svg file to.
            final Path tempDirPath = Files.createTempDirectory(getOrCreateTempDirectory().toPath(), "DotEngine");

            // Write the dot file to the output path or the temporary directory
            final File dotfile = this.getDotFile(tempDirPath.toString());
            final BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(dotfile), "UTF-8"));
            bw.write(call);
            bw.close();

            // Run the command
            final int status = this.cmdRunner.exec(CMD_DOT + " -Tsvg " + dotfile.getAbsolutePath() + " -ooutfile.svg",
                    new File(tempDirPath.toString()));
            if (status != 0) {
                throw new GraphvizException("Dot command didn't succeed");
            }

            // Read output file from temp folder
            final byte[] encoded = Files.readAllBytes(Paths.get(tempDirPath.toString() + "/outfile.svg"));

            FileUtils.deleteDirectory(tempDirPath.toFile());

            return new String(encoded, "UTF-8");

        } catch (IOException e) {
            throw new GraphvizException("Failed to execute dot command", e);
        }

    }

    private File getOrCreateTempDirectory() {
        File tempDir;
        tempDir = new File(System.getProperty("java.io.tmpdir") + File.separator + "GraphvizJava");
        if (!tempDir.exists() && tempDir.mkdir()) {
            LOGGER.debug("Created GraphvizJava temporary directory");
        }
        return tempDir;
    }

    private File getDotFile(String tempDirPath) {
        String dotFileName;
        if (this.dotOutputFileName == null) {
            dotFileName = "dotfile.dot";
        } else {
            dotFileName = this.dotOutputFileName + ".dot";
        }

        if (this.dotOutputFilePath == null) {
            return new File(tempDirPath + "/" + dotFileName);
        } else {
            return new File(dotOutputFilePath + "/" + dotFileName);
        }
    }

    public void setDotOutputFile(String path, String name) {
        this.dotOutputFilePath = path;
        this.dotOutputFileName = name;
    }
}
