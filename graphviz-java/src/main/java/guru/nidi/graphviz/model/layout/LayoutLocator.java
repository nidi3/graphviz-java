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

import guru.nidi.graphviz.model.*;

import java.awt.geom.Rectangle2D;
import java.util.function.*;

import static guru.nidi.graphviz.model.layout.LayoutAttributes.outlineOf;

public class LayoutLocator {
    private final MutableGraph graph;
    private final int radius;

    public LayoutLocator(MutableGraph graph, int radius) {
        this.graph = graph;
        this.radius = radius;
    }

    /**
     * Find the layout element at the given position.
     *
     * @param x x coordinate
     * @param y y coordinate
     * @return A MutableGraph, MutableNode, Link or null.
     */
    public Object elementAt(int x, int y) {
        final Rectangle2D test = new Rectangle2D.Double(x - radius, y - radius, 2 * radius, 2 * radius);
        Object res = null;
        if (outlineOf(graph).toShape().intersects(test)) {
            res = graph;
        }
        for (final MutableGraph sub : graph.graphs()) {
            if (outlineOf(sub).toShape().intersects(test)) {
                res = sub;
            }
        }
        for (final MutableNode node : graph.nodes()) {
            if (outlineOf(node).toShape().intersects(test)) {
                res = node;
            }
        }
        for (final Link link : graph.edges()) {
            if (outlineOf(link).toShape().intersects(test)) {
                res = link;
            }
        }
        return res;
    }

    void useElementAt(int x, int y, Consumer<MutableGraph> graphConsumer, Consumer<MutableNode> nodeConsumer,
                      Consumer<Link> linkConsumer, Runnable nullConsumer) {
        final Object res = elementAt(x, y);
        if (res instanceof Link) {
            linkConsumer.accept((Link) res);
        } else if (res instanceof MutableNode) {
            nodeConsumer.accept((MutableNode) res);
        } else if (res instanceof MutableGraph) {
            graphConsumer.accept((MutableGraph) res);
        } else {
            nullConsumer.run();
        }
    }

    <T> T mapElementAt(int x, int y, Function<MutableGraph, T> graphMapper, Function<MutableNode, T> nodeMapper,
                       Function<Link, T> linkMapper, Supplier<T> nullMapper) {
        final Object res = elementAt(x, y);
        if (res instanceof Link) {
            return linkMapper.apply((Link) res);
        }
        if (res instanceof MutableNode) {
            return nodeMapper.apply((MutableNode) res);
        }
        if (res instanceof MutableGraph) {
            return graphMapper.apply((MutableGraph) res);
        }
        return nullMapper.get();
    }
}
