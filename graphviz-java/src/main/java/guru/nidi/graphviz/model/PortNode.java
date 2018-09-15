package guru.nidi.graphviz.model;

import guru.nidi.graphviz.attribute.Label;

import javax.annotation.Nullable;

public interface PortNode extends LinkSource, LinkTarget {
    <N extends LinkSource & LinkTarget> N node();

    Label name();

    PortNode port(@Nullable String record);

    PortNode port(@Nullable Compass compass);

    PortNode port(@Nullable String record, @Nullable Compass compass);

    Port port();
}
