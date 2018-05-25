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

public final class Factory {
    private Factory() {
    }

    public static Graph graph() {
        return new Graph();
    }

    public static Graph graph(String name) {
        return new Graph().name(name);
    }

    public static Node node(String name) {
        return GraphContext.node(name);
    }

    public static Port port(String record) {
        return new Port().setRecord(record);
    }

    public static Port port(Compass compass) {
        return new Port().setCompass(compass);
    }

    public static Port port(String record, Compass compass) {
        return new Port().setRecord(record).setCompass(compass);
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
