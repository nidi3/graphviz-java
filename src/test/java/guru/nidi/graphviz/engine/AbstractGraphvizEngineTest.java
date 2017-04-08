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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;


public class AbstractGraphvizEngineTest {

    private class GraphvizEngineDummmy extends AbstractGraphvizEngine {

        public GraphvizEngineDummmy(boolean sync, EngineInitListener engineInitListener) {
            super(sync, engineInitListener);
        }

        @Override
        protected void doInit() throws Exception {
            // nothing
        }

        @Override
        protected String doExecute(String call) {
            return call;
        }
    }

    @Test
    public void vizExecTotalMemoryIsSet() {
        GraphvizEngineDummmy engineUnderTest = new GraphvizEngineDummmy(true, null);
        VizjsOptions vizjsOptions = new VizjsOptions();
        vizjsOptions.totalMemory=320000;

        final String vizResult = engineUnderTest.vizExec("digraph{ a -> b}", Engine.DOT, Format.SVG, vizjsOptions);

        assertThat(vizResult , is("Viz('digraph{ a -> b}',{format:'svg',engine:'dot',totalMemory:'320000'});"));
    }

    @Test
    public void vizExecTotalMemoryIsNotSet() {
        GraphvizEngineDummmy engineUnderTest = new GraphvizEngineDummmy(true, null);
        VizjsOptions vizjsOptions = new VizjsOptions();

        final String vizResult = engineUnderTest.vizExec("digraph{ a -> b}", Engine.DOT, Format.SVG, vizjsOptions);

        assertThat(vizResult , is("Viz('digraph{ a -> b}',{format:'svg',engine:'dot'});"));
    }

    @Test
    public void vizExecVizjsVizJsOptionIsNull() {
        GraphvizEngineDummmy engineUnderTest = new GraphvizEngineDummmy(true, null);

        final String vizResult = engineUnderTest.vizExec("digraph{ a -> b}", Engine.DOT, Format.SVG, null);

        assertThat(vizResult , is("Viz('digraph{ a -> b}',{format:'svg',engine:'dot'});"));
    }

    @Test
    public void executeTotalMemoryIsNull(){
        GraphvizEngineDummmy engineUnderTest = new GraphvizEngineDummmy(true, null);

        String result = engineUnderTest.execute("digraph{ a -> b}", Engine.DOT, Format.SVG, new VizjsOptions());

        assertThat(result, is("Viz('digraph{ a -> b}',{format:'svg',engine:'dot'});"));
    }

    @Test
    public void executeTotalMemoryIsSet(){
        GraphvizEngineDummmy engineUnderTest = new GraphvizEngineDummmy(true, null);

        String result = engineUnderTest.execute("digraph{ a -> b}", Engine.DOT, Format.SVG, new VizjsOptions(32000));

        assertThat(result, is("Viz('digraph{ a -> b}',{format:'svg',engine:'dot',totalMemory:'32000'});"));
    }

}