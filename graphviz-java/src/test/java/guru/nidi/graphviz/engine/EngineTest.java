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

import com.eclipsesource.v8.V8;
import guru.nidi.codeassert.config.AnalyzerConfig;
import guru.nidi.graphviz.service.CommandLineExecutor;
import guru.nidi.graphviz.service.SystemUtils;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;

import javax.annotation.Nullable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static guru.nidi.graphviz.engine.Format.SVG;
import static guru.nidi.graphviz.engine.Format.SVG_STANDALONE;
import static guru.nidi.graphviz.engine.FormatTest.START1_7;
import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Every.everyItem;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

class EngineTest {
    @Nullable
    private static File temp;

    @BeforeAll
    static void init() throws IOException {
        temp = new File(System.getProperty("java.io.tmpdir"), "engineTest");
        FileUtils.deleteDirectory(temp);
        temp.mkdir();
    }

    @AfterEach
    void end() {
        Graphviz.releaseEngine();
    }

    @Test
    void jdk() throws IOException, ScriptException {
        String script = new String(Files.readAllBytes(new File("../dist/bundle.js").toPath()));
        ScriptEngine js = new ScriptEngineManager().getEngineByExtension("js");
        //js.eval(script);
        V8 v8 = V8.createV8Runtime();
        v8.registerJavaMethod(System.out,"println","print",new Class[]{Object.class});
        v8.executeVoidScript("console={log:function(a){print(a);}};setTimeout=function(a){print(a);};clearTimeout=function(a){print(a);};"+
                "setInterval=function(a){print(a);};clearInterval=function(a){print(a);};"+script);
        Graphviz.useEngine(new GraphvizJdkEngine());
        assertThat(Graphviz.fromString("graph g {a--b}").render(SVG_STANDALONE).toString(), startsWith(START1_7));
    }

    @Test
    void server() {
        assumeFalse(System.getProperty("os.name").startsWith("Windows"), "I gave up fixing this");
        GraphvizServerEngine.stopServer();
        try {
            Graphviz.useEngine(new GraphvizServerEngine().useEngine(new GraphvizV8Engine()));
            assertThat(Graphviz.fromString("graph g {a--b}").render(SVG_STANDALONE).toString(), startsWith(START1_7));
        } finally {
            GraphvizServerEngine.stopServer();
        }
    }

    @Test
    void v8() {
        Graphviz.useEngine(new GraphvizV8Engine());
        assertThat(Graphviz.fromString("graph g {a--b}").render(SVG_STANDALONE).toString(), startsWith(START1_7));
    }

    @Test
    void v8WithoutPath() throws Exception {
        assertNativeLibs(System.getProperty("user.home"), () -> Graphviz.useEngine(new GraphvizV8Engine()));
    }

    @Test
    void v8WithPath() throws Exception {
        final String tmpDir = System.getProperty("java.io.tmpdir");
        assertNativeLibs(tmpDir, () -> Graphviz.useEngine(new GraphvizV8Engine(tmpDir)));
    }

    private void assertNativeLibs(String basedir, Runnable task) throws ReflectiveOperationException {
        final File[] libs = new File[]{
                new File(basedir, "libj2v8_linux_x86_64.so"),
                new File(basedir, "libj2v8_macosx_x86_64.dylib"),
                new File(basedir, "libj2v8_win32_x86.dll"),
                new File(basedir, "libj2v8_win32_x86_64.dll"),
        };
        for (final File lib : libs) {
            lib.delete();
        }
        final Field loaded = V8.class.getDeclaredField("nativeLibraryLoaded");
        loaded.setAccessible(true);
        loaded.setBoolean(null, false);
        task.run();
        for (final File lib : libs) {
            if (lib.exists()) {
                return;
            }
        }
        fail("No native library found");
    }

    @Test
    void multiV8() throws InterruptedException {
        Graphviz.useEngine(new GraphvizV8Engine());
        final ExecutorService executor = Executors.newFixedThreadPool(2);
        final List<String> res = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            executor.submit(() -> {
                res.add(Graphviz.fromString("graph g {a--b}").render(SVG).toString());
                Graphviz.releaseEngine();
            });
        }
        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.SECONDS);
        assertThat(res, everyItem(not(isEmptyOrNullString())));
    }

    @Test
    void cmdLine() throws IOException, InterruptedException {
        final File dotFile = setUpFakeDotFile();
        final CommandLineExecutor cmdExecutor = setUpFakeStubCommandExecutor();

        final String envPath = dotFile.getParent();
        Graphviz.useEngine(new GraphvizCmdLineEngine(envPath, cmdExecutor));

        final String actual = Graphviz.fromString("graph g {a--b}").render(SVG_STANDALONE).toString();
        assertThat(actual, startsWith(START1_7.replace("\n", System.lineSeparator())));
    }

    /**
     * Test to check if we can set the output path and name of the dot file
     */
    @Test
    void cmdLineOutputDotFile() throws IOException, InterruptedException {
        final File dotFile = setUpFakeDotFile();
        final CommandLineExecutor cmdExecutor = setUpFakeStubCommandExecutor();

        final String envPath = dotFile.getParent();

        final File dotOutputFolder = new File(temp, "out");
        dotOutputFolder.mkdir();
        final String dotOutputName = "test123";

        // Configure engine to output the dotFile to dotOutputFolder
        final GraphvizCmdLineEngine engine = new GraphvizCmdLineEngine(envPath, cmdExecutor);
        engine.setDotOutputFile(dotOutputFolder.getAbsolutePath(), dotOutputName);

        Graphviz.useEngine(engine);

        // Do execution
        Graphviz.fromString("graph g {a--b}").render(SVG_STANDALONE).toString();

        assertTrue(new File(dotOutputFolder.getAbsolutePath(), dotOutputName + ".dot").exists());
    }

    @Test
    void escapeAmpersand() {
        assertThat(Graphviz.fromGraph(graph().with(node("Z&bl;g"))).render(SVG).toString(), containsString(">Z&amp;bl;g<"));
    }

    @Test
    void escapeSubSpace() {
        assertThat(Graphviz.fromGraph(graph().with(node("Z\u0001a\u001fg"))).render(SVG).toString(), containsString(">Z a g<"));
    }

    private File setUpFakeDotFile() throws IOException {
        final String filename = SystemUtils.executableName("dot");
        final File dotFile = new File(temp, filename);
        dotFile.createNewFile();
        dotFile.setExecutable(true);
        return dotFile;
    }

    private CommandLineExecutor setUpFakeStubCommandExecutor() throws IOException, InterruptedException {
        final CommandLineExecutor cmdExecutor = mock(CommandLineExecutor.class);
        doAnswer(invocationOnMock -> {
            final File workingDirectory = invocationOnMock.getArgumentAt(1, File.class);
            final File svgInput = new File(getClass().getClassLoader().getResource("outfile1.svg").getFile());
            final File svgOutputFile = new File(workingDirectory.getAbsolutePath() + "/outfile.svg");
            Files.copy(svgInput.toPath(), svgOutputFile.toPath());
            return null;
        }).when(cmdExecutor).execute(any(CommandLine.class), any(File.class));
        return cmdExecutor;
    }
}
