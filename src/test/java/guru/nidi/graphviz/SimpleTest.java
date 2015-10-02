package guru.nidi.graphviz;

import com.kitfox.svg.SVGException;
import org.junit.Test;

import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 *
 */
public class SimpleTest {
    @Test
    public void simple() throws IOException, ScriptException, SVGException {
        final GraphViz viz = new GraphViz("digraph g { a -> b; }");
        System.out.println(viz.createSvg());
        viz.renderToFile(new File("g2.png"), "png", 200, 200);
    }

    @Test
    public void dotError() throws IOException, ScriptException, SVGException {
        try {
            new GraphViz("g { a -> b; }").createSvg();
            fail();
        } catch (GraphvizException e) {
            assertThat(e.getMessage(), startsWith("Error: syntax error in line 1 near 'g'"));
        }
    }
}
