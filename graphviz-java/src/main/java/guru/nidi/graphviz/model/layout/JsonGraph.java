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

import java.util.*;

import static guru.nidi.graphviz.model.layout.JsonDraw.parseDraw;
import static guru.nidi.graphviz.model.layout.LayoutAttributes.*;

class JsonGraph {
    String name;
    String pad;
    @JsonProperty("_draw_")
    List<JsonDraw> draw;
    List<JsonNode> objects;
    List<JsonEdge> edges;
    int padX;
    int padY;

    public void applyTo(MutableGraph graph) {
        calcPad();
        int adjust = pad == null ? 4 : 0;
        final Polygon shape = (Polygon) parseDraw(draw, padX, padY, 0);
        final Coordinate topRight = shape.coordinates.get(2);
        final int height = -(int) topRight.y + adjust;
        final int width = (int) topRight.x + adjust;
        graph.graphAttrs().add(OUTLINE, parseDraw(draw, padX, padY, height));
        graph.graphAttrs().add(WIDTH, width);
        graph.graphAttrs().add(HEIGHT, height);
        final Map<Integer, String> nodeById = new HashMap<>();
        for (final JsonNode object : objects) {
            object.applyTo(graph, padX, padY, height);
            nodeById.put(object.id, object.name);
        }
        for (final JsonEdge edge : edges) {
            edge.applyTo(graph, padX, padY, height, nodeById);
        }
    }

    private void calcPad() {
        if (pad == null) {
            padX = padY = 4;
        } else if (pad.contains(",")) {
            final String[] parts = pad.split(",");
            padX = dpiToPixel(parts[0]);
            padY = dpiToPixel(parts[1]);
        } else {
            padX = padY = dpiToPixel(pad);
        }
    }

    private int dpiToPixel(String pixel) {
        return (int) (Double.parseDouble(pixel) * 72);
    }
}
