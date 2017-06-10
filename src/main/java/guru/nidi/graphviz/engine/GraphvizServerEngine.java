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
import java.net.Socket;

public class GraphvizServerEngine extends AbstractGraphvizEngine {
    public GraphvizServerEngine() {
        super(false);
    }

    @Override
    public String execute(String src, Options options) {
        try {
            return createSvg(src); //TODO handle options
        } catch (IOException e) {
            throw new GraphvizException("Problem in communication with server", e);
        }
    }

    @Override
    protected void doInit() throws Exception {
        if (!canConnect()) {
            GraphvizServer.start();
            for (int i = 0; i < 50 && !canConnect(); i++) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    //ignore
                }
            }
            if (!canConnect()) {
                throw new IOException("Could not connect to server");
            }
        }
    }

    public boolean canConnect() {
        try {
            try (final Socket socket = new Socket("localhost", GraphvizServer.PORT)) {
                return true;
            }
        } catch (IOException e) {
            return false;
        }
    }

    private String createSvg(String src) throws IOException {
        return communicating(com -> {
            com.writeContent(src);
            final String status = com.readStatus();
            final int len = com.readLen();
            final String content = com.readContent(len);
            if (!"ok".equals(status)) {
                throw new GraphvizException(content);
            }
            return content;
        });
    }

    public static void stopServer() {
        try {
            communicating(com -> {
                com.writeLen(-1);
                return null;
            });
        } catch (IOException e) {
            //ignore
        }
    }

    private interface ComFunc<T> {
        T apply(Communicator c) throws IOException;
    }

    private static <T> T communicating(ComFunc<T> action) throws IOException {
        try (final Socket socket = new Socket("localhost", GraphvizServer.PORT);
             final Communicator com = new Communicator(socket.getInputStream(), socket.getOutputStream())) {
            return action.apply(com);
        }
    }
}
