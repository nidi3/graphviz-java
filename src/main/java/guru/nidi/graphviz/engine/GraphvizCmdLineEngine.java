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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Engine that tries to parse the dot file using the GraphvizEngine installed on the host.
 *
 * @author daank
 */
public class GraphvizCmdLineEngine extends AbstractGraphvizEngine {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractGraphvizEngine.class);

    private final String envPath;
    private final CommandRunner cmdRunner;

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
        cmdRunner = new CommandBuilder()
                .withShellWrapper(true)
                .withCommandExecutor(executor)
                .build();
    }

    @Override
    protected void doInit() throws GraphvizException {
        if (!CommandRunner.isExecutableFound("dot", envPath)) {
            throw new GraphvizException("'dot' command not found");
        }
    }

    @Override
    protected String doExecute(String src, Options options) {
        final String engine = getEngineFromOptions(options);
        if (!CommandRunner.isExecutableFound(engine, envPath)) {
            throw new GraphvizException(engine + " command not found");
        }

        try {
            // Create a temporary file to save the svg file to.
            final Path tempDirPath = Files.createTempDirectory(getOrCreateTempDirectory().toPath(), "DotEngine");

            // Write the dot file to the output path or the temporary directory
            final File dotfile = getDotFile(tempDirPath.toString());
            try (final BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(dotfile), StandardCharsets.UTF_8))) {
                bw.write(src);
            }

            // Run the command
            final String command = engine + " -T" + getFormatFromOptions(options)
                    + " " + dotfile.getAbsolutePath() + " -ooutfile.svg";
            cmdRunner.exec(command, tempDirPath.toFile());

            // Read output file from temp folder
            final byte[] encoded = Files.readAllBytes(tempDirPath.resolve("outfile.svg"));

            FileUtils.deleteDirectory(tempDirPath.toFile());

            return new String(encoded, StandardCharsets.UTF_8);
        } catch (IOException | InterruptedException e) {
            throw new GraphvizException(e.getMessage(), e);
        }
    }

    private String getEngineFromOptions(Options options) {
        String engine = "dot";
        if (options.engine != null) {
            engine = options.engine.toString().toLowerCase();
        }
        if (SystemUtils.IS_OS_WINDOWS) {
            engine = engine + ".exe";
        }
        return engine;
    }

    private String getFormatFromOptions(Options options) {
        String format = "svg";
        if (options.format != null && options.format.vizName != null) {
            format = options.format.vizName;
        }
        return format;
    }

    private File getOrCreateTempDirectory() {
        final File tempDir = new File(System.getProperty("java.io.tmpdir") + File.separator + "GraphvizJava");
        if (!tempDir.exists() && tempDir.mkdir()) {
            LOG.debug("Created GraphvizJava temporary directory");
        }
        return tempDir;
    }

    private File getDotFile(String tempDirPath) {
        final String dotFileName = dotOutputFileName == null ? "dotfile.dot" : dotOutputFileName + ".dot";
        final String baseDir = dotOutputFilePath == null ? tempDirPath : dotOutputFilePath;
        return new File(baseDir, dotFileName);
    }

    public void setDotOutputFile(String path, String name) {
        dotOutputFilePath = path;
        dotOutputFileName = name;
    }
}
