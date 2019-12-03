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

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.nidi.graphviz.model.MutableGraph;

import java.util.List;

import static guru.nidi.graphviz.model.layout.JsonDraw.parseDraw;
import static guru.nidi.graphviz.model.layout.LayoutAttributes.OUTLINE;

class JsonNode {
    String name;
    @JsonProperty("_gvid")
    int id;
    @JsonProperty("_draw_")
    List<JsonDraw> draw;

    public void applyTo(MutableGraph graph, int padX, int padY, int height) {
        final Figure shape = parseDraw(draw, padX, padY, height);
        if (name.startsWith("cluster_")) {
            graph.graphs().stream()
                    .filter(n -> ("cluster_" + n.name()).equals(name))
                    .forEach(n -> n.graphAttrs().add(OUTLINE, shape));
        } else {
            graph.nodes().stream()
                    .filter(n -> n.name().contentEquals(name))
                    .forEach(n -> n.add(OUTLINE, shape));
        }
    }
}
