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

import static java.util.Collections.unmodifiableList;

public class Polygon implements Figure {
    public final List<Coordinate> coordinates;
    private java.awt.Polygon shape;

    public Polygon(List<Coordinate> coordinates) {
        this.coordinates = unmodifiableList(coordinates);
    }

    @Override
    public Shape toShape() {
        if (shape == null) {
            final int[] xs = coordinates.stream().mapToInt(c -> (int) c.x).toArray();
            final int[] ys = coordinates.stream().mapToInt(c -> (int) c.y).toArray();
            shape = new java.awt.Polygon(xs, ys, xs.length);
        }
        return shape;
    }
}
