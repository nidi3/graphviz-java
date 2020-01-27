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

import java.awt.*;
import java.util.List;
import java.util.*;

import static guru.nidi.graphviz.model.layout.JsonDraw.parseDraw;
import static guru.nidi.graphviz.model.layout.LayoutAttributes.*;

class JsonGraph {
    String pad;
    String margin;
    @JsonProperty("_draw_")
    List<JsonDraw> draw;
    List<JsonNode> objects;
    List<JsonEdge> edges;

    public void applyTo(MutableGraph graph) {
        final Point padPx = parseLimit(pad, 4);
        final Point marginPx = parseLimit(margin, 0);
        final int adjust = pad == null ? 4 : 0;
        final Polygon shape = (Polygon) parseDraw(draw, padPx);
        final Coordinate topRight = shape.coordinates.get(2);
        final int width = adjust + (int) topRight.x + 2 * marginPx.x;
        final int height = adjust - (int) topRight.y + 2 * marginPx.y + 2 * padPx.y;
        graph.graphAttrs().add(WIDTH, width);
        graph.graphAttrs().add(HEIGHT, height);
        final Point offset = new Point(padPx.x + marginPx.x, height - (padPx.y + marginPx.y));
        graph.graphAttrs().add(OUTLINE, parseDraw(draw, offset));
        final Map<Integer, String> nodeById = new HashMap<>();
        for (final JsonNode object : objects) {
            object.applyTo(graph, offset);
            nodeById.put(object.id, object.name);
        }
        for (final JsonEdge edge : edges) {
            edge.applyTo(graph, offset, nodeById);
        }
    }

    private Point parseLimit(String limit, int defaultValue) {
        if (limit == null) {
            return new Point(defaultValue, defaultValue);
        }
        if (limit.contains(",")) {
            final String[] parts = limit.split(",");
            return new Point(dpiToPixel(parts[0]), dpiToPixel(parts[1]));
        }
        return new Point(dpiToPixel(limit), dpiToPixel(limit));
    }

    private int dpiToPixel(String pixel) {
        return (int) (Double.parseDouble(pixel) * 72);
    }
}
