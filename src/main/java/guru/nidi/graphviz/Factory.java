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

/**
 *
 */
public class Factory {
    private Factory() {
    }

    public static Graph graph(String name) {
        return Graph.named(name);
    }

    public static Graph graph(Name name) {
        return Graph.named(name);
    }

    public static Node node(String name) {
        return Node.named(name);
    }

    public static Node node(Name name) {
        return Node.named(name);
    }

    public static NodePoint compass(String compass) {
        return NodePoint.ofCompass(compass);
    }

    public static NodePoint record(String record) {
        return NodePoint.ofRecord(record);
    }

    public static Name name(String name) {
        return Name.of(name);
    }

    public static Name html(String html) {
        return Name.html(html);
    }

    public static Link to(Node node) {
        return Link.to(node);
    }

    public static Link to(NodePoint node) {
        return Link.to(node);
    }

    public static Link between(NodePoint from, NodePoint to) {
        return Link.between(from, to);
    }
}
