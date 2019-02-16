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

import java.io.File;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractGraphvizEngine implements GraphvizEngine {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractGraphvizEngine.class);
    protected static final Pattern IMG_SRC = Pattern.compile("<img .*?src\\s*=\\s*['\"]([^'\"]*)");
    protected static final Pattern IMAGE_ATTR = Pattern.compile("\"?image\"?\\s*=\\s*\"(.*?)\"");

    private final boolean sync;

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

    protected String replacePaths(String src, Pattern pattern, Function<String, String> replacer) {
        final Matcher matcher = pattern.matcher(src);
        final StringBuilder s = new StringBuilder();
        int last = 0;
        while (matcher.find()) {
            final String attr = matcher.group(1);
            s.append(src, last, matcher.start(1));
            s.append(replacer.apply(attr));
            last = matcher.end(1);
        }
        return s.append(src.substring(last)).toString();
    }

    protected String replacePath(String path, File basedir) {
        return path.startsWith("http://") || path.startsWith("https://") || new File(path).isAbsolute()
                ? path
                : new File(basedir, path).getAbsolutePath();
    }

    @Override
    public void close() {
    }

    protected abstract void doInit() throws Exception;

    @Override
    public String toString() {
        return getClass().getName();
    }
}
