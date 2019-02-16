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

import guru.nidi.graphviz.service.*;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static java.util.Locale.ENGLISH;

/**
 * Engine that tries to parse the dot file using the GraphvizEngine installed on the host.
 *
 * @author daank
 */
public class GraphvizCmdLineEngine extends AbstractGraphvizEngine {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractGraphvizEngine.class);

    private final String envPath;
    private final CommandRunner cmdRunner;

    @Nullable
    private String dotOutputFilePath;
    @Nullable
    private String dotOutputFileName;

    public GraphvizCmdLineEngine() {
        this(Optional.ofNullable(System.getenv("PATH")).orElse(""), new DefaultExecutor());
    }

    public GraphvizCmdLineEngine(String envPath, DefaultExecutor executor) {
        super(true);
        this.envPath = envPath;
        cmdRunner = new CommandBuilder()
                .withShellWrapper(true)
                .withCommandExecutor(executor)
                .build();
    }

    @Override
    protected void doInit() throws GraphvizException {
        getEngineExecutable(Engine.DOT);
    }

    @Override
    public String execute(String src, Options options) {
        final String engine = getEngineExecutable(options.engine);
        try {
            // Create a temporary file to save the svg file to.
            final Path tempDirPath = Files.createTempDirectory(getOrCreateTempDirectory().toPath(), "DotEngine");

            // Write the dot file to the output path or the temporary directory
            final File dotfile = getDotFile(tempDirPath.toString());
            try (final BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(dotfile), StandardCharsets.UTF_8))) {
                bw.write(preprocessCode(src, options));
            }
            final String command = engine
                    + (options.yInvert != null && options.yInvert ? " -y" : "")
                    + " -T" + getFormatName(options.format)
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

    protected String preprocessCode(String src, Options options) {
        final String imgReplaced = replacePaths(src, IMG_SRC, path -> replacePath(path, options.basedir));
        return replacePaths(imgReplaced, IMAGE_ATTR, path -> replacePath(path, options.basedir));
    }

    private String getEngineExecutable(@Nullable Engine engine) {
        final String exe = SystemUtils.executableName(engine == null ? "dot" : engine.toString().toLowerCase(ENGLISH));
        if (!CommandRunner.isExecutableFound(exe, envPath)) {
            final GraphvizException e = new GraphvizException(exe + " command not found");
            e.setStackTrace(new StackTraceElement[0]);
            throw e;
        }
        return exe;
    }

    private String getFormatName(@Nullable Format format) {
        return format == null ? "svg" : format.vizName;
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
