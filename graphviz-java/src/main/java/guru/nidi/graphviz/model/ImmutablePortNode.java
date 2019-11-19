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

import guru.nidi.graphviz.attribute.Label;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

class ImmutablePortNode implements PortNode, LinkSource, LinkTarget {
    private final MutableNode node;
    private final Port port;

    ImmutablePortNode(MutableNode node, Port port) {
        this.node = node;
        this.port = port;
    }

    public ImmutablePortNode copy() {
        return new ImmutablePortNode(node.copy(), port);
    }

    @Override
    public PortNode port(@Nullable String record) {
        return new ImmutablePortNode(node, new Port(record, port.compass()));
    }

    @Override
    public PortNode port(@Nullable Compass compass) {
        return new ImmutablePortNode(node, new Port(port.record(), compass));
    }

    @Override
    public PortNode port(@Nullable String record, @Nullable Compass compass) {
        return new ImmutablePortNode(node, new Port(record, compass));
    }

    public Port port() {
        return port;
    }

    @Override
    public List<Link> links() {
        return node.links;
    }

    @Override
    public Link linkTo(LinkTarget target) {
        return node.linkTo(port.isEmpty() ? target : Link.between(port, target));
    }

    @Override
    public Link linkTo() {
        return Link.to(this);
    }

    @Override
    public LinkTarget asLinkTarget() {
        return node;
    }

    @Override
    public LinkSource asLinkSource() {
        return node;
    }

    @Override
    public void addTo(MutableGraph graph) {
        graph.nodes.add(node);
    }

    @Override
    public MutableNode node() {
        return node;
    }

    @Override
    public Label name() {
        return node.name();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ImmutablePortNode that = (ImmutablePortNode) o;
        return Objects.equals(node, that.node)
                && Objects.equals(port, that.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(node, port);
    }

    @Override
    public String toString() {
        return node.name + port.toString();
    }
}
