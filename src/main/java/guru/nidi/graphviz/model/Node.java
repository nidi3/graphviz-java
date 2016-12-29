package guru.nidi.graphviz.model;

import guru.nidi.graphviz.attribute.Attributed;

/**
 *
 */
public interface Node extends Linkable, Attributed<Node>, LinkTarget, LinkSource<Node> {
    NodePoint loc();

    NodePoint loc(String record);

    NodePoint loc(Compass compass);

    NodePoint loc(String record, Compass compass);

    Node link(LinkTarget... targets);

    Node link(String node);

    Node link(String... nodes);
}
