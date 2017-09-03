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

import com.eclipsesource.v8.*;
import com.eclipsesource.v8.utils.V8ObjectUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GraphvizV8Engine extends AbstractJsGraphvizEngine {
    private static final Pattern ABORT = Pattern.compile("^undefined:\\d+: abort");
    private static final Pattern ERROR = Pattern.compile("^undefined:\\d+: (.*?)\n");
    private V8 v8;
    private V8Array messages;

    public GraphvizV8Engine() {
        super(true);
    }

    @Override
    public void release() {
        messages.release();
        v8.release(true);
    }

    @Override
    protected void doInit() throws Exception {
        v8 = V8.createV8Runtime();
        v8.executeVoidScript(jsInitEnv());
        messages = v8.getArray("$$prints");
        v8.executeVoidScript(jsVizCode("1.8.0"));
    }

    @Override
    protected String jsExecute(String call) {
        try {
            return v8.executeStringScript(call);
        } catch (V8RuntimeException e) {
            if (ABORT.matcher(e.getMessage()).find()) {
                throw new GraphvizException(IntStream.range(0, messages.length())
                        .mapToObj(i -> V8ObjectUtils.getValue(messages, i).toString())
                        .collect(Collectors.joining("\n")));
            }
            final Matcher em = ERROR.matcher(e.getMessage());
            if (em.find()) {
                throw new GraphvizException(em.group(1));
            }
            throw new GraphvizException("Problem executing graphviz", e);
        }
    }
}
