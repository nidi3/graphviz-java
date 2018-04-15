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

public final class Factory {
    private Factory() {
    }

    public static Graph graph() {
        return graph("");
    }

    public static Graph graph(String name) {
        return new ImmutableGraph().named(name);
    }

    public static Node node(String name) {
        return node(Label.of(name));
    }

    public static Node node(Label name) {
        return CreationContext.createNode(name);
    }

    public static PortNode port(String record) {
        return port(record, null);
    }

    public static PortNode port(Compass compass) {
        return port(null, compass);
    }

    public static PortNode port(String record, Compass compass) {
        return new ImmutablePortNode(null, record, compass);
    }


    public static MutableGraph mutGraph() {
        return new MutableGraph();
    }

    public static MutableGraph mutGraph(String name) {
        return new MutableGraph().setName(name);
    }

    public static MutableNode mutNode(String name) {
        return mutNode(Label.of(name));
    }

    public static MutableNode mutNode(Label name) {
        return CreationContext.createMutNode(name);
    }

    public static MutablePortNode mutPort(String record) {
        return new MutablePortNode().setRecord(record);
    }

    public static MutablePortNode mutPort(Compass compass) {
        return new MutablePortNode().setCompass(compass);
    }

    public static MutablePortNode mutPort(String record, Compass compass) {
        return new MutablePortNode().setRecord(record).setCompass(compass);
    }


    public static Link to(Node node) {
        return Link.to(node);
    }

    public static Link to(LinkTarget node) {
        return Link.to(node);
    }

    public static Link between(LinkSource from, LinkTarget to) {
        return Link.between(from, to);
    }

}
