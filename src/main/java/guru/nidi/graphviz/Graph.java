/*
 * Copyright (C) 2015 Stefan Niederhauser (nidin@gmx.ch)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package guru.nidi.graphviz;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class Graph {
    final boolean strict;
    final boolean directed;
    final Name name;
    final Map<String, Object> attributes = new HashMap<>();
    final Set<Node> nodes = new LinkedHashSet<>();
    final Set<Graph> subgraphs = new LinkedHashSet<>();

    private Graph(boolean strict, boolean directed, Name name) {
        this.strict = strict;
        this.directed = directed;
        this.name = name;
    }

    public static Graph named(Name name) {
        return new Graph(false, false, name);
    }

    public static Graph named(String name) {
        return named(Name.of(name));
    }

    public Graph strict() {
        return new Graph(true, directed, name);
    }

    public Graph directed() {
        return new Graph(strict, true, name);
    }

    public Graph attr(String name, Object value) {
        attributes.put(name, value);
        return this;
    }

    public Graph attrs(Map<String, Object> attrs) {
        attributes.putAll(attrs);
        return this;
    }

    public Graph with(Node node) {
        nodes.add(node);
        return this;
    }

    public Graph with(Graph subgraph) {
        subgraphs.add(subgraph);
        return this;
    }

}
