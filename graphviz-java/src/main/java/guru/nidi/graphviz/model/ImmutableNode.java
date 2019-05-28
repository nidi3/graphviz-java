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

import java.util.*;

class ImmutableNode extends MutableNode implements Node {
    ImmutableNode(Label name) {
        this(name, new ArrayList<>(), Attributes.attrs());
    }

    private ImmutableNode(Label name, List<Link> links, Attributes<? extends ForNode> attributes) {
        super(name, links, attributes);
    }

    public ImmutableNode link(LinkTarget target) {
        return (ImmutableNode) copyOfMut().addLink(target);
    }

    public ImmutableNode link(LinkTarget... targets) {
        return (ImmutableNode) copyOfMut().addLink(targets);
    }

    public ImmutableNode link(List<? extends LinkTarget> targets) {
        return (ImmutableNode) copyOfMut().addLink(targets);
    }

    public ImmutableNode link(String node) {
        return (ImmutableNode) copyOfMut().addLink(node);
    }

    public ImmutableNode link(String... nodes) {
        return (ImmutableNode) copyOfMut().addLink(nodes);
    }

    public ImmutableNode with(Attributes<? extends ForNode> attrs) {
        return (ImmutableNode) copyOfMut().add(attrs);
    }

    private ImmutableNode copyOfMut() {
        return new ImmutableNode(name, new ArrayList<>(links), attributes.copy());
    }

    @Override
    public List<Link> links() {
        return Collections.unmodifiableList(super.links());
    }
}
