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
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public abstract class AbstractGraphvizEngine implements GraphvizEngine {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractGraphvizEngine.class);
    protected static final Pattern IMG_SRC = Pattern.compile("<img .*?src\\s*=\\s*['\"]([^'\"]*)");
    protected static final Pattern IMAGE_ATTR = Pattern.compile("\"?image\"?\\s*=\\s*\"(.*?)\"");

    private final boolean sync;
    protected int timeout = 10000;

    protected AbstractGraphvizEngine(boolean sync) {
        this.sync = sync;
    }

    public void init(Consumer<GraphvizEngine> onOk, Consumer<GraphvizEngine> onError) {
        if (sync) {
            initTask(onOk, onError);
        } else {
            new Thread(() -> initTask(onOk, onError)).start();
        }
    }

    private void initTask(Consumer<GraphvizEngine> onOk, Consumer<GraphvizEngine> onError) {
        try {
            doInit();
            onOk.accept(this);
        } catch (Exception e) {
            LOG.info("Could not initialize {}", this, e);
            close();
            onError.accept(this);
        }
    }

    @SuppressWarnings("unchecked")
    protected <T extends AbstractGraphvizEngine> T timeout(int amount, TimeUnit unit) {
        this.timeout = (int) MILLISECONDS.convert(amount, unit);
        return (T) this;
    }

    @Override
    public void close() {
    }

    protected abstract void doInit() throws IOException;

    protected void throwingInit() {
        try {
            doInit();
        } catch (Exception e) {
            LOG.info("Could not initialize {}", this, e);
        }
    }

    @Override
    public String toString() {
        return getClass().getName();
    }
}
