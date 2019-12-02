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

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class GraphEllipse implements GraphShape {
    public final Coordinate center;
    public final Coordinate radius;

    public GraphEllipse(Coordinate center, Coordinate radius) {
        this.center = center;
        this.radius = radius;
    }

    @Override
    public Shape toShape() {
        return new Ellipse2D.Double(center.x, center.y, 2 * radius.x, 2 * radius.y);
    }
}
