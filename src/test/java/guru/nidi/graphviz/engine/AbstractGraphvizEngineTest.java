package guru.nidi.graphviz.engine;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;


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
            return null;
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
    public void vizExecVizjsIsNull() {
        GraphvizEngineDummmy engineUnderTest = new GraphvizEngineDummmy(true, null);

        final String vizResult = engineUnderTest.vizExec("digraph{ a -> b}", Engine.DOT, Format.SVG, null);

        assertThat(vizResult , is("Viz('digraph{ a -> b}',{format:'svg',engine:'dot'});"));
    }

}