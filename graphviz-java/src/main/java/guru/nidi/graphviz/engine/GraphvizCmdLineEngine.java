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
    private String outputFilePath;
    @Nullable
    private String outputFileName;

    public GraphvizCmdLineEngine() {
        this(Optional.ofNullable(System.getenv("PATH")).orElse(""), new CommandLineExecutor());
    }

    public GraphvizCmdLineEngine(String envPath, CommandLineExecutor executor) {
        super(true);
        this.envPath = envPath;
        cmdRunner = new CommandBuilder()
                .withShellWrapper(true)
                .withCommandExecutor(executor)
                .build();
    }

    @Override
    protected void doInit() {
        getEngineExecutable(Engine.DOT);
    }

    @Override
    public EngineResult execute(String src, Options options, @Nullable Rasterizer rasterizer) {
        try {
            final Path path = Files.createTempDirectory(getOrCreateTempDirectory().toPath(), "DotEngine");
            final File dotFile = getDotFile(path);
            try (final BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(dotFile), StandardCharsets.UTF_8))) {
                bw.write(preprocessCode(src, options));
            }
            return doExecute(path, dotFile, options, rasterizer);
        } catch (IOException | InterruptedException e) {
            throw new GraphvizException(e.getMessage(), e);
        }
    }

    private EngineResult doExecute(Path path, File dotFile, Options options, @Nullable Rasterizer rasterizer)
            throws IOException, InterruptedException {
        final String engine = getEngineExecutable(options.engine);
        final String format = getFormatName(options.format, rasterizer);
        final String command = engine
                + (options.yInvert != null && options.yInvert ? " -y" : "")
                + " -T" + format
                + " " + dotFile.getAbsolutePath() + " -ooutfile." + format;
        cmdRunner.exec(command, path.toFile());
        final Path outFile = path.resolve("outfile." + format);
        if (rasterizer instanceof BuiltInRasterizer) {
            return EngineResult.fromFile(outFile.toFile());
        }
        final byte[] data = Files.readAllBytes(outFile);
        return EngineResult.fromString(new String(data, StandardCharsets.UTF_8));
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

    private String getFormatName(@Nullable Format format, @Nullable Rasterizer rasterizer) {
        if (rasterizer instanceof BuiltInRasterizer) {
            final BuiltInRasterizer natRast = (BuiltInRasterizer) rasterizer;
            String f = natRast.format;
            if (natRast.renderer != null) {
                f += ":" + natRast.renderer;
            }
            if (natRast.formatter != null) {
                f += ":" + natRast.formatter;
            }
            return f;
        }
        return format == null ? "svg" : format.vizName;
    }

    private File getOrCreateTempDirectory() {
        final File tempDir = new File(System.getProperty("java.io.tmpdir") + File.separator + "GraphvizJava");
        if (!tempDir.exists() && tempDir.mkdir()) {
            LOG.debug("Created GraphvizJava temporary directory");
        }
        return tempDir;
    }

    private File getDotFile(Path path) {
        final String dotFileName = outputFileName == null ? "dotfile.dot" : outputFileName + ".dot";
        final String baseDir = outputFilePath == null ? path.toString() : outputFilePath;
        return new File(baseDir, dotFileName);
    }

    public void setDotOutputFile(String path, String name) {
        outputFilePath = path;
        outputFileName = name;
    }
}
