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
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static guru.nidi.graphviz.engine.GraphvizLoader.isOnClasspath;
import static guru.nidi.graphviz.engine.StringFunctions.replaceRegex;
import static guru.nidi.graphviz.engine.TempFiles.tempDir;
import static guru.nidi.graphviz.service.CommandRunner.isExecutableFile;
import static guru.nidi.graphviz.service.CommandRunner.isExecutableFound;
import static guru.nidi.graphviz.service.SystemUtils.pathOf;
import static guru.nidi.graphviz.service.SystemUtils.uriPathOf;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Locale.ENGLISH;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * Engine that tries to parse the dot file using the GraphvizEngine installed on the host.
 *
 * @author daank
 */
public class GraphvizCmdLineEngine extends AbstractGraphvizEngine {
    private static final Logger LOG = LoggerFactory.getLogger(GraphvizCmdLineEngine.class);
    static final boolean AVAILABLE = isOnClasspath("org/apache/commons/exec/CommandLine.class");

    @Nullable
    private final String executable;
    private final List<Option> cmdOptions;
    private final String envPath;
    private final CommandRunner cmdRunner;

    @Nullable
    private String outputFilePath;
    @Nullable
    private String outputFileName;

    public GraphvizCmdLineEngine() {
        this(null, emptyList(), Optional.ofNullable(System.getenv("PATH")).orElse(""),
                runner(defaultExecutor()));
    }

    public GraphvizCmdLineEngine(String executable, Option... options) {
        this(executable, asList(options), Optional.ofNullable(System.getenv("PATH")).orElse(""),
                runner(defaultExecutor()));
    }

    private GraphvizCmdLineEngine(@Nullable String executable, List<Option> options,
                                  String envPath, CommandRunner cmdRunner) {
        super(true);
        this.executable = executable;
        this.cmdOptions = options;
        this.envPath = envPath;
        this.cmdRunner = cmdRunner;
    }

    public GraphvizCmdLineEngine searchPath(String path) {
        return new GraphvizCmdLineEngine(executable, cmdOptions, path, cmdRunner);
    }

    public GraphvizCmdLineEngine executor(CommandLineExecutor executor) {
        return new GraphvizCmdLineEngine(executable, cmdOptions, envPath, runner(executor));
    }

    private static CommandRunner runner(CommandLineExecutor executor) {
        return new CommandBuilder()
                .withShellWrapper(true)
                .withCommandExecutor(executor)
                .build();
    }

    private static CommandLineExecutor defaultExecutor() {
        if (!AVAILABLE) {
            throw new MissingDependencyException("Command line engine is not available.",
                    "org.apache.commons:commons-exec");
        }
        return new CommandLineExecutor();
    }

    @SuppressWarnings("unchecked")
    public GraphvizCmdLineEngine timeout(int amount, TimeUnit unit) {
        return super.timeout(amount, unit);
    }

    @Override
    protected void doInit() {
        getEngineExecutable();
    }

    @Override
    public EngineResult execute(String src, Options options, Rasterizer rasterizer) {
        try {
            final Path path = tempDir("DotEngine");
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

    private EngineResult doExecute(Path path, File dotFile, Options options, Rasterizer rasterizer)
            throws IOException, InterruptedException {
        final List<Option> nonMatchingOptions = cmdOptions.stream()
                .filter(o -> (o instanceof NeatoOption && options.engine != Engine.NEATO)
                        || (o instanceof FdpOption && options.engine != Engine.FDP))
                .collect(toList());
        if (!nonMatchingOptions.isEmpty()) {
            LOG.warn("Option(s) '" + nonMatchingOptions.stream().map(o -> o.name).collect(joining(", "))
                    + "' are not supported by engine " + options.engine);
        }
        final String simpleFormat = simpleFormat(options.format, rasterizer);
        final String command = getEngineExecutable()
                + (options.yInvert != null && options.yInvert ? " -y" : "")
                + " -K" + options.engine.toString().toLowerCase(ENGLISH)
                + " -T" + completeFormat(options.format, rasterizer)
                + " " + cmdOptions.stream().map(o -> o.name).collect(joining(" "))
                + " " + dotFile.getAbsolutePath() + " -ooutfile." + simpleFormat;
        LOG.info("input  file://{}", uriPathOf(dotFile));
        cmdRunner.exec(command, path.toFile(), timeout);
        final Path outFile = path.resolve("outfile." + simpleFormat);
        LOG.info("output file://{}", uriPathOf(outFile.toFile()));
        if (rasterizer instanceof BuiltInRasterizer) {
            return EngineResult.fromFile(outFile.toFile());
        }
        final byte[] data = Files.readAllBytes(outFile);
        return EngineResult.fromString(new String(data, StandardCharsets.UTF_8));
    }

    protected String preprocessCode(String src, Options options) {
        return replaceRegex(replaceRegex(src,
                IMG_SRC, path -> options.image(path).processImagePath(path)),
                IMAGE_ATTR, path -> options.image(path).processImagePath(path));
    }

    private String getEngineExecutable() {
        if (executable != null) {
            if (isExecutableFile(pathOf(executable)) || isExecutableFound(executable, envPath)) {
                return executable;
            }
            LOG.warn("Executable '" + executable + "' not found directly and not on PATH. Trying with 'dot'.");
        }
        final List<String> exes = SystemUtils.executableNames("dot");
        for (final String exe : exes) {
            if (isExecutableFound(exe, envPath)) {
                return exe;
            }
        }
        final GraphvizException e = new GraphvizException(exes + " command not found");
        e.setStackTrace(new StackTraceElement[0]);
        throw e;
    }

    private String simpleFormat(Format format, Rasterizer rasterizer) {
        return rasterizer instanceof BuiltInRasterizer
                ? ((BuiltInRasterizer) rasterizer).format
                : format.vizName;
    }

    private String completeFormat(Format format, Rasterizer rasterizer) {
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
        return format.vizName;
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

    public static class Option {
        final String name;

        protected Option(String name) {
            this.name = name;
        }
    }

    public static final class NeatoOption extends Option {
        public static final NeatoOption REDUCE_GRAPH = new NeatoOption("-x");
        public static final NeatoOption NO_LAYOUT_AVOID_OVERLAP = new NeatoOption("-n");
        public static final NeatoOption NO_LAYOUT_ALLOW_OVERLAP = new NeatoOption("-n2");

        private NeatoOption(String name) {
            super(name);
        }
    }

    public static final class FdpOption extends Option {
        public static final FdpOption NO_GRID = new FdpOption("-Lg");
        public static final FdpOption OLD_FORCE = new FdpOption("-LO");

        private FdpOption(String name) {
            super(name);
        }

        public static FdpOption iterations(int n) {
            return new FdpOption("-Ln" + n);
        }

        public static FdpOption unscaledFactor(double v) {
            return new FdpOption("-LU" + v);
        }

        public static FdpOption overlapExpansionFactor(double v) {
            return new FdpOption("-LC" + v);
        }

        public static FdpOption temperature(double v) {
            return new FdpOption("-LT" + v);
        }

        public static FdpOption temperatureFactor(double v) {
            return new FdpOption("-LT*" + v);
        }
    }
}
