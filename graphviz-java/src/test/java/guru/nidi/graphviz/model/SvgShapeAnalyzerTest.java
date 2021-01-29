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
package guru.nidi.graphviz.model;

import guru.nidi.graphviz.engine.Graphviz;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static guru.nidi.graphviz.engine.Format.SVG;
import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SvgShapeAnalyzerTest {
    @Test
    void base() {
        final Graph g = graph().with(node("a"));
        final String svg = Graphviz.fromGraph(g).render(SVG).toString();
        final SvgShapeAnalyzer analyzer = new SvgShapeAnalyzer(new SvgElementFinder(svg), 1000, 1000);
        final Rectangle box = analyzer.getBoundingBox(analyzer.findNode("a"));
        assertEquals(new Rectangle(65, 91, 871, 818), box);
    }

}
