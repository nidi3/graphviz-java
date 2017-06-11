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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static guru.nidi.graphviz.engine.Format.SVG_STANDALONE;

final class GraphvizServer {
    private static final Logger LOG = LoggerFactory.getLogger(GraphvizServer.class);
    static final int PORT = 10234;

    private GraphvizServer() {
    }

    public static void start() throws IOException {
        final boolean windows = System.getProperty("os.name").contains("windows");
        final String executable = windows ? "java.exe" : "java";
        final Process process = new ProcessBuilder(System.getProperty("java.home") + "/bin/" + executable,
                "-cp", System.getProperty("java.class.path"), GraphvizServer.class.getName())
                .inheritIO()
                .start();
    }

    public static void main(String... args) throws IOException {
        System.out.println("WWWWWWWWWW");
        LOG.info("starting graphviz server...");
        Graphviz.useEngine(new GraphvizV8Engine(), new GraphvizJdkEngine());
        LOG.info("started.");
        Graphviz.initEngine();
        LOG.info("inited.");
        try (final ServerSocket ss = new ServerSocket(PORT)) {
            while (true) {
                try (final Socket socket = ss.accept();
                     final Communicator com = new Communicator(socket.getInputStream(), socket.getOutputStream())) {
                    final int len = com.readLen();
                    if (len != 0) {
                        if (len == -1) {
                            break;
                        }
                        final String s = com.readContent(len);
                        try {
                            final String svg = render(s);
                            com.writeStatus("ok");
                            com.writeContent(svg);
                        } catch (GraphvizException e) {
                            com.writeStatus("fail");
                            com.writeContent(e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    LOG.warn("Problem in communication", e);
                }
            }
        }
        LOG.info("graphviz server stopped.");
    }

    private static String render(String raw) {
        final int pos = raw.indexOf("@@@");
        final Options options;
        final String src;
        if (pos < 0) {
            options = Options.create().format(SVG_STANDALONE);
            src = raw;
        } else {
            options = Options.fromJson(raw.substring(0, pos));
            src = raw.substring(pos + 3);
        }
        return Graphviz.fromString(src)
                .engine(options.engine).totalMemory(options.totalMemory)
                .render(options.format).toString();
    }

}