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
import java.net.Socket;

/**
 *
 */
class GraphvizClient {
    public static boolean canConnect() {
        try {
            try (final Socket socket = new Socket("localhost", GraphvizServer.PORT)) {
                return true;
            }
        } catch (IOException e) {
            return false;
        }
    }

    public static String createSvg(String dot) throws IOException {
        try (final Socket socket = new Socket("localhost", GraphvizServer.PORT);
             final Communicator com = new Communicator(socket.getInputStream(), socket.getOutputStream())) {
            com.writeContent(dot);
            final String status = com.readStatus();
            final int len = com.readLen();
            final String content = com.readContent(len);
            if (!status.equals("ok")) {
                throw new GraphvizException(content);
            }
            return content;
        }
    }

    public static void stopServer() {
        try (final Socket socket = new Socket("localhost", GraphvizServer.PORT);
             final Communicator com = new Communicator(socket.getInputStream(), socket.getOutputStream())) {
            com.writeLen(-1);
        } catch (IOException e) {
            //ignore
        }
    }
}
