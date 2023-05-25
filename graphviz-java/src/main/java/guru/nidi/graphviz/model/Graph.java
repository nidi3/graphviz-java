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

import guru.nidi.graphviz.attribute.*;

import java.util.List;

/**
 * A Graphviz graph.
 * 
 * @see <a href="https://graphviz.org/docs/graph/">Graph</a>
 */
public interface Graph extends LinkSource, LinkTarget {
    /**
     * Makes a copy of this graph as a strict graph.
     * 
     * @return a copy of this graph, as a strict graph.
     * 
     * @see <a href=
     *      "https://graphviz.org/doc/info/lang.html#lexical-and-semantic-notes">Lexical
     *      and Semantic Notes</a>
     */
    Graph strict();

    /**
     * Makes a copy of this graph as a directed graph.
     * 
     * @return a copy of this graph, as a directed graph.
     * 
     * @see <a href=
     *      "https://graphviz.org/doc/info/lang.html#lexical-and-semantic-notes">Lexical
     *      and Semantic Notes</a>
     */
    Graph directed();

    /**
     * Makes a copy of this graph as a cluster.
     * 
     * @return a copy of this graph, as a cluster
     * 
     * @see <a href=
     *      "https://graphviz.org/doc/info/lang.html#subgraphs-and-clusters">Subgraphs
     *      and Clusters</a>
     */
    Graph cluster();

    /**
     * Makes a copy of this graph with the specified name.
     * 
     * @param name a name for the copied graph
     * @return a copy of this graph with the specified name
     */
    Graph named(String name);

    /**
     * Creates links to the specified elements
     * 
     * @param targets elements to link this graph
     * @return a graph
     */
    Graph link(LinkTarget... targets);

    /**
     * Creates a graph with the specified elements.
     * 
     * @param sources elements to add to the graph
     * @return a graph with the specified elements
     */
    Graph with(LinkSource... sources);

    /**
     * Creates a graph with the specified elements.
     * 
     * @param sources list of elements to add to the graph
     * @return a graph with the specified elements
     */
    Graph with(List<? extends LinkSource> sources);

    /**
     * Sets global node attributes.
     * 
     * @return node attributes
     */
    Attributed<Graph, ForNode> nodeAttr();

    /**
     * Sets global link attributes.
     * 
     * @return link attributes
     */
    Attributed<Graph, ForLink> linkAttr();

    /**
     * Sets global link attributes.
     * 
     * @return link attributes
     */
    Attributed<Graph, ForGraph> graphAttr();

    /**
     * Returns this graph as a mutable graph.
     * 
     * @return a mutable graph
     */
    MutableGraph toMutable();
}
