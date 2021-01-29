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
package guru.nidi.graphviz.model;

import guru.nidi.graphviz.attribute.Named;
import org.w3c.dom.Element;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toMap;

public class GraphElementFinder extends SvgElementFinder {
    private final Map<String, MutableNode> nodes;
    private final Map<String, Link> links;
    private final Map<String, MutableGraph> graphs;

    public static String use(String svg, Graph g, Consumer<GraphElementFinder> actions) {
        final GraphElementFinder finder = new SvgElementFinder(svg).fromGraph(g);
        actions.accept(finder);
        return finder.getSvg();
    }

    GraphElementFinder(SvgElementFinder finder, MutableGraph graph) {
        super(finder);
        nodes = graph.nodes().stream().collect(toMap(n -> name(n), n -> n));
        links = graph.edges().stream().collect(toMap(e -> name(e.from()) + "--" + name(e.to()), e -> e));
        graphs = graph.graphs().stream().collect(toMap(g -> name(g), g -> g));
    }

    private static String name(Named named) {
        return named.name().simpleSerialized();
    }

    public MutableNode nodeOf(Element e) {
        return nodes.get(SvgElementFinder.nodeNameOf(e));
    }

    public Link linkOf(Element e) {
        final List<String> fromTo = SvgElementFinder.linkedNodeNamesOf(e);
        return links.get(fromTo.get(0) + "--" + fromTo.get(1));
    }

    public MutableGraph clusterOf(Element e) {
        return graphs.get(SvgElementFinder.clusterNameOf(e));
    }
}
