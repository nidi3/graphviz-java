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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

//public because of nashorn
public class ResultHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ResultHandler.class);
    private final BlockingQueue<String> value = new ArrayBlockingQueue<>(1);
    private boolean ok;

    public void log(String msg) {
        LOG.warn(msg);
    }

    public void setResult(String result) {
        ok = true;
        value.add(result);
    }

    public void setError(String error) {
        ok = false;
        value.add(error);
    }

    public String waitFor() {
        try {
            final String v = value.take();
            if (ok) {
                return v;
            }
            throw new GraphvizException(v);
        } catch (InterruptedException e) {
            throw new GraphvizException("Waiting for result interrupted", e);
        }
    }
}
