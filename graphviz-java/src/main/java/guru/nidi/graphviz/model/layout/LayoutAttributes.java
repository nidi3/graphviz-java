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
package guru.nidi.graphviz.model.layout;

import guru.nidi.graphviz.attribute.Attributes;
import guru.nidi.graphviz.model.*;

public final class LayoutAttributes {
    static final String
            FIGURE = "layoutFigure",
            WIDTH = "layoutWidth",
            HEIGHT = "layoutHeight";

    private LayoutAttributes() {
    }

    public static Integer widthOf(MutableGraph graph) {
        return (Integer) graph.graphAttrs().get(WIDTH);
    }

    public static Integer heightOf(MutableGraph graph) {
        return (Integer) graph.graphAttrs().get(HEIGHT);
    }

    public static Figure figureOf(MutableGraph graph) {
        return figureOf(graph.graphAttrs());
    }

    public static Figure figureOf(MutableNode node) {
        return figureOf(node.attrs());
    }

    public static Figure figureOf(Link link) {
        return figureOf(link.attrs());
    }

    public static Figure figureOf(Attributes<?> attributes) {
        return (Figure) attributes.get(FIGURE);
    }
}
