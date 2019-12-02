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
package guru.nidi.graphviz.model.shape;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.nidi.graphviz.engine.GraphvizException;
import guru.nidi.graphviz.model.MutableGraph;

import java.util.*;

import static guru.nidi.graphviz.model.shape.JsonDraw.parseDraw;
import static guru.nidi.graphviz.model.shape.JsonShapesParser.SHAPE_ATTRIBUTE;
import static java.util.stream.Collectors.toList;

public class JsonShapesParser {
    public static final String SHAPE_ATTRIBUTE = "graphShape";
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    private JsonShapesParser() {
    }

    public static void applyShapesToGraph(String json, MutableGraph graph) {
        try {
            MAPPER.readValue(json, JsonGraph.class).applyTo(graph);
        } catch (JsonProcessingException e) {
            throw new GraphvizException("Could not read json", e);
        }
    }
}

class JsonGraph {
    public String name;
    public String pad;
    public List<JsonDraw> _draw_;
    public List<JsonNode> objects;
    public List<JsonEdge> edges;
    int padX;
    int padY;

    public void applyTo(MutableGraph graph) {
        calcPad();
        final GraphPolygon shape = (GraphPolygon) parseDraw(_draw_, padX, padY, 0);
        final Coordinate topRight = shape.coordinates.get(2);
        final int height = -(int) topRight.y;
        graph.graphAttrs().add(SHAPE_ATTRIBUTE, parseDraw(_draw_, padX, padY, height));
        graph.graphAttrs().add("graphWidth", (int) topRight.x);
        graph.graphAttrs().add("graphHeight", height);
        final Map<Integer, String> nodeById = new HashMap<>();
        for (final JsonNode object : objects) {
            object.applyTo(graph, padX, padY, height);
            nodeById.put(object._gvid, object.name);
        }
        for (final JsonEdge edge : edges) {
            edge.applyTo(graph, padX, padY, height, nodeById);
        }
    }

    private void calcPad() {
        if (pad == null) {
            padX = padY = 8;
        } else if (pad.contains(",")) {
            final String[] parts = pad.split(",");
            padX = (int) (Double.parseDouble(parts[0]) * 72);
            padY = (int) (Double.parseDouble(parts[1]) * 72);
        } else {
            padX = padY = (int) (Double.parseDouble(pad) * 72);
        }
    }
}

class JsonDraw {
    public String op;
    public List<List<Double>> points;
    public List<Double> rect;

    static GraphShape parseDraw(List<JsonDraw> draw, int padX, int padY, int height) {
        final List<GraphShape> shapes = draw.stream()
                .map(jsonDraw -> jsonDraw.toShape(padX, padY, height))
                .filter(Objects::nonNull)
                .collect(toList());
        if (shapes.size() != 1) {
            throw new GraphvizException("No or multiple draws found.");
        }
        return shapes.get(0);
    }

    private GraphShape toShape(int padX, int padY, int height) {
        switch (op.toLowerCase(Locale.ENGLISH)) {
            case "p":
                return new GraphPolygon(points.stream().map(c ->
                        new Coordinate(c.get(0) + padX, height - c.get(1) - padY)).collect(toList()));
            case "e":
                return new GraphEllipse(
                        new Coordinate(rect.get(0) - rect.get(2) + padX, height - (rect.get(1) + rect.get(3)) - padY),
                        new Coordinate(rect.get(2), rect.get(3)));
            case "b":
                return new GraphSpline(points.stream().map(c ->
                        new Coordinate(c.get(0) + padX, height - c.get(1) - padY)).collect(toList()));
            default:
                return null;
        }
    }
}

class JsonNode {
    public String name;
    public int _gvid;
    public List<JsonDraw> _draw_;

    public void applyTo(MutableGraph graph, int padX, int padY, int height) {
        final GraphShape shape = parseDraw(_draw_, padX, padY, height);
        if (name.startsWith("cluster_")) {
            graph.graphs().stream()
                    .filter(n -> ("cluster_" + n.name()).equals(name))
                    .forEach(n -> n.graphAttrs().add(SHAPE_ATTRIBUTE, shape));
        } else {
            graph.nodes().stream()
                    .filter(n -> n.name().value().equals(name))
                    .forEach(n -> n.add(SHAPE_ATTRIBUTE, shape));
        }
    }
}

class JsonEdge {
    public int head;
    public int tail;
    public List<JsonDraw> _draw_;

    public void applyTo(MutableGraph graph, int padX, int padY, int height, Map<Integer, String> nodeById) {
        final GraphShape shape = parseDraw(_draw_, padX, padY, height);
        graph.edges().stream()
                .filter(e -> e.from().name().value().equals(nodeById.get(tail))
                        && e.to().name().value().equals(nodeById.get(head)))
                .forEach(e -> e.add(SHAPE_ATTRIBUTE, shape));
    }
}