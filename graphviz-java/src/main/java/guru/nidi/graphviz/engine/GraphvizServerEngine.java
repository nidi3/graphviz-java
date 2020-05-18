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

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class GraphvizServerEngine extends AbstractGraphvizEngine {
    private final List<GraphvizEngine> engines = new ArrayList<>();
    private String host = "localhost";
    private int port = GraphvizServer.DEFAULT_PORT;

    public GraphvizServerEngine() {
        super(false);
    }

    public GraphvizServerEngine useEngine(GraphvizEngine first, GraphvizEngine... rest) {
        engines.clear();
        engines.add(first);
        engines.addAll(Arrays.asList(rest));
        return this;
    }

    public GraphvizServerEngine port(int port) {
        this.port = port;
        return this;
    }

    public GraphvizServerEngine host(String host) {
        this.host = host;
        return this;
    }

    @SuppressWarnings("unchecked")
    public GraphvizServerEngine timeout(int amount, TimeUnit unit) {
        return super.timeout(amount, unit);
    }

    @Override
    public EngineResult execute(String src, Options options, Rasterizer rasterizer) {
        try {
            return EngineResult.fromString(createSvg(src, options));
        } catch (SocketTimeoutException e) {
            throw new GraphvizException("Engine took too long to respond, try setting a higher timout");
        } catch (IOException e) {
            throw new GraphvizException("Problem in communication with GraphvizServer at " + host + ":" + port, e);
        }
    }

    @Override
    protected void doInit() throws IOException {
        if (!canConnect()) {
            if (!InetAddress.getByName(host).isLoopbackAddress()) {
                throw new IOException("Could not connect to GraphvizServer at " + host + ":" + port);
            }
            GraphvizServer.start(engines, port);
            for (int i = 0; i < 100 && !canConnect(); i++) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    //ignore
                }
            }
            if (!canConnect()) {
                throw new IOException("Could not connect to GraphvizServer at " + host + ":" + port);
            }
        }
    }

    public boolean canConnect() {
        try {
            try (final Socket socket = socket(host, port)) {
                return true;
            }
        } catch (IOException e) {
            return false;
        }
    }

    private String createSvg(String src, Options options) throws IOException {
        return communicating(host, port, timeout, com -> {
            com.writeContent(options.toJson(true) + "@@@" + src);
            final String status = com.readStatus();
            final int len = com.readLen();
            final String content = com.readContent(len);
            if (!"ok".equals(status)) {
                throw new GraphvizException(content);
            }
            return content;
        });
    }

    public void stopThisServer() {
        stopServer(host, port);
    }

    public static void stopServer() {
        stopServer(GraphvizServer.DEFAULT_PORT);
    }

    public static void stopServer(int port) {
        stopServer("localhost", port);
    }

    public static void stopServer(String host, int port) {
        try {
            communicating(host, port, 5000, com -> {
                com.writeLen(-1);
                return "";
            });
        } catch (IOException e) {
            //ignore
        }
    }

    private interface ComFunc<T> {
        T apply(Communicator c) throws IOException;
    }

    private static <T> T communicating(String host, int port, int timeout, ComFunc<T> action) throws IOException {
        try (final Socket socket = socket(host, port);
             final Communicator com = new Communicator(socket, timeout)) {
            return action.apply(com);
        }
    }

    private static Socket socket(String host, int port) throws IOException {
        final Socket socket = new Socket();
        socket.setSoTimeout(500);
        socket.connect(new InetSocketAddress(host, port), 500);
        return socket;
    }
}
