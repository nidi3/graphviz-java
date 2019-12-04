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

import java.awt.*;
import java.util.List;
import java.util.*;

import static java.util.stream.Collectors.toList;

class JsonDraw {
    String op;
    List<List<Double>> points;
    List<Double> rect;

    static Figure parseDraw(List<JsonDraw> draw, Point offset) {
        final List<Figure> shapes = draw.stream()
                .map(jsonDraw -> jsonDraw.toShape(offset))
                .filter(Objects::nonNull)
                .collect(toList());
        if (shapes.size() != 1) {
            throw new LayoutParserException("No or multiple draws found.");
        }
        return shapes.get(0);
    }

    private Figure toShape(Point offset) {
        switch (op.toLowerCase(Locale.ENGLISH)) {
            case "p":
                return new Polygon(pointCoordinates(offset));
            case "e":
                return new Ellipse(
                        new Coordinate(rect.get(0) - rect.get(2) + offset.x, offset.y - (rect.get(1) + rect.get(3))),
                        new Coordinate(rect.get(2), rect.get(3)));
            case "b":
                return new Spline(pointCoordinates(offset));
            default:
                return null;
        }
    }

    private List<Coordinate> pointCoordinates(Point offset) {
        return points.stream().map(c -> new Coordinate(c.get(0) + offset.x, offset.y - c.get(1))).collect(toList());
    }
}
