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
import java.awt.geom.Path2D;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class Spline implements Figure {
    public final List<Coordinate> points;

    public Spline(List<Coordinate> points) {
        this.points = unmodifiableList(points);
    }

    @Override
    public Shape toShape() {
        final Path2D.Double res = new Path2D.Double();
        res.moveTo(points.get(0).x, points.get(0).y);
        for (int i = 1; i < points.size() - 2; i += 3) {
            res.curveTo(
                    points.get(i).x, points.get(i).y,
                    points.get(i + 1).x, points.get(i + 1).y,
                    points.get(i + 2).x, points.get(i + 2).y);
        }
        return res;
    }
}
