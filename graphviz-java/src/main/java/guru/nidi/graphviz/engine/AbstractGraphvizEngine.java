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

import java.util.function.Consumer;

public abstract class AbstractGraphvizEngine implements GraphvizEngine {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractGraphvizEngine.class);

    private final boolean sync;

    public AbstractGraphvizEngine(boolean sync) {
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
            release();
            onError.accept(this);
        }
    }

    @Override
    public void release() {
    }

    protected abstract void doInit() throws Exception;

    @Override
    public String toString() {
        return getClass().getName();
    }
}
