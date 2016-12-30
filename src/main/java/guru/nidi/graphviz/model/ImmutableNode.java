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
package guru.nidi.graphviz.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ImmutableNode extends MutableNode implements Node {
    ImmutableNode(Label label) {
        this(label, new ArrayList<>(), new HashMap<>());
    }

    private ImmutableNode(Label label, List<Link> links, Map<String, Object> attributes) {
        super(label, links, attributes);
    }

    public NodePoint loc() {
        return loc(null, null);
    }

    public NodePoint loc(String record) {
        return loc(record, null);
    }

    public NodePoint loc(Compass compass) {
        return loc(null, compass);
    }

    public NodePoint loc(String record, Compass compass) {
        return new ImmutableNodePoint(this, record, compass);
    }

    public ImmutableNode link(LinkTarget target) {
        return (ImmutableNode) copyOfMut().addLink(target);
    }

    public ImmutableNode link(LinkTarget... targets) {
        return (ImmutableNode) copyOfMut().addLink(targets);
    }

    public ImmutableNode link(String node) {
        return (ImmutableNode) copyOfMut().addLink(node);
    }

    public ImmutableNode link(String... nodes) {
        return (ImmutableNode) copyOfMut().addLink(nodes);
    }

    public ImmutableNode with(Map<String, Object> attrs) {
        return (ImmutableNode) copyOfMut().add(attrs);
    }

    private ImmutableNode copyOfMut() {
        return new ImmutableNode(label, new ArrayList<>(links), attributes.applyTo(new HashMap<>()));
    }
}
