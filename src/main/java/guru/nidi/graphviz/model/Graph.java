package guru.nidi.graphviz.model;

import guru.nidi.graphviz.attribute.Attributed;

public interface Graph extends Linkable,LinkSource, LinkTarget {
    Graph strict();

    Graph directed();

    Graph cluster();

    Graph labeled(Label label);

    Graph nodes(Node... nodes);

    Graph nodes(String... nodes);

    Graph graphs(Graph... subgraphs);

    Graph link(LinkTarget... targets);

    Graph with(LinkSource... sources);

    Attributed<Graph> nodes();

    Attributed<Graph> links();

    Attributed<Graph> graphs();

    Attributed<Graph> general();

}
