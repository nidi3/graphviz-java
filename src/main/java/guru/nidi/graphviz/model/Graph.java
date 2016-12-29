package guru.nidi.graphviz.model;

import guru.nidi.graphviz.attribute.Attributed;

/**
 *
 */
public interface Graph extends Linkable, LinkSource<Graph>, LinkTarget {
    Graph strict();

    Graph directed();

    Graph cluster();

    Graph labeled(Label label);

    Graph nodes(Node... nodes);

    Graph nodes(String... nodes);

    Graph graphs(Graph... subgraphs);

    Graph with(LinkSource source);

    Graph link(LinkTarget... targets);

    Attributed<Graph> nodes();

    Attributed<Graph> links();

    Attributed<Graph> graphs();

    Attributed<Graph> general();

}
