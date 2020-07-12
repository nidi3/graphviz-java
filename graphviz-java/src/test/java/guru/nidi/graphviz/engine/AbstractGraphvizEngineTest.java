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

import org.junit.jupiter.api.Test;

import java.io.File;

import static guru.nidi.graphviz.service.SystemUtils.uriPathOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

class AbstractGraphvizEngineTest {

    static class GraphvizEngineDummy extends AbstractJsGraphvizEngine {
        GraphvizEngineDummy() {
            super(true, DummyJavascriptEngine::new);
        }
    }

    static class DummyJavascriptEngine extends AbstractJavascriptEngine {
        @Override
        protected String execute(String js) {
            return js;
        }
    }

    @Test
    void vizExecWithOptions() {
        final GraphvizEngineDummy engineUnderTest = new GraphvizEngineDummy();
        final Options options = Options.create().format(Format.SVG).totalMemory(320000).yInvert(true);

        final String vizResult = engineUnderTest.jsVizExec("digraph{ a -> b}", options);

        assertThat(vizResult, is("totalMemory=320000;render('digraph{ a -> b}',{format:'svg',engine:'dot',totalMemory:'320000',"
                + "yInvert:true,basedir:'" + uriPathOf(new File(".")) + "',images:[]});"));
    }

    @Test
    void vizExecWithoutOptions() {
        final GraphvizEngineDummy engineUnderTest = new GraphvizEngineDummy();
        final Options options = Options.create().format(Format.SVG);

        final String vizResult = engineUnderTest.jsVizExec("digraph{ a -> b}", options);

        assertThat(vizResult, is("render('digraph{ a -> b}',{format:'svg',engine:'dot',"
                + "basedir:'" + uriPathOf(new File(".")) + "',images:[]});"));
    }
}
