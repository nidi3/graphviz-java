/*
 * Copyright (C) 2015 Stefan Niederhauser (nidin@gmx.ch)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package guru.nidi.graphviz.engine;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 */
class GraphvizServer {
    static final int PORT = 10234;

    public static void start() throws IOException {
        final boolean windows = System.getProperty("os.name").contains("windows");
        final String executable = windows ? "java.exe" : "java";
        final ProcessBuilder builder = new ProcessBuilder(System.getProperty("java.home") + "/bin/" + executable,
                "-cp", System.getProperty("java.class.path"), "guru.nidi.graphviz.engine.GraphvizServer").inheritIO();
        builder.start();
    }

    public static void main(String[] args) throws IOException {
        System.out.println("starting...");
        GraphvizEngine.initLocally();
        System.out.println("started.");
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
                            final String svg = Graphviz.fromString(s).createSvg();
                            com.writeStatus("ok");
                            com.writeContent(svg);
                        } catch (GraphvizException e) {
                            com.writeStatus("fail");
                            com.writeContent(e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("stopped.");
    }

}