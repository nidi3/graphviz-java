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
            OUTLINE = "__layoutOutline",
            WIDTH = "__layoutWidth",
            HEIGHT = "__layoutHeight";

    private LayoutAttributes() {
    }

    public static Integer widthOf(MutableGraph graph) {
        return (Integer) graph.graphAttrs().get(WIDTH);
    }

    public static Integer heightOf(MutableGraph graph) {
        return (Integer) graph.graphAttrs().get(HEIGHT);
    }

    public static Figure outlineOf(MutableGraph graph) {
        return outlineOf(graph.graphAttrs());
    }

    public static Figure outlineOf(MutableNode node) {
        return outlineOf(node.attrs());
    }

    public static Figure outlineOf(Link link) {
        return outlineOf(link.attrs());
    }

    public static Figure outlineOf(Attributes<?> attributes) {
        return (Figure) attributes.get(OUTLINE);
    }
}
