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
package guru.nidi.graphviz.model;

import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.GraphvizException;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 *
 */
public class SimpleTest {
    @Test
    public void simple() {
        final Graphviz viz = Graphviz.fromString("digraph g { \"a\\b'c\" -> b; }");
        viz.renderToFile(new File("target/g2.png"), "png", 200, 200);
        System.out.println(viz.createSvg());
    }

    @Test
    public void dotError() {
        try {
            System.out.println("Try error...");
            Graphviz.fromString("g { a -> b; }").createSvg();
            fail();
        } catch (GraphvizException e) {
            assertThat(e.getMessage(), startsWith("Error: syntax error in line 1 near 'g'"));
        }
    }
}
