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
        return graph("");
    }

    public static Graph graph(String name) {
        return graph(name(name));
    }

    public static Graph graph(Label label) {
        return new ImmutableGraph().labeled(label);
    }

    public static Node node(String name) {
        return node(Label.of(name));
    }

    public static Node node(Label label) {
        return CreationContext.current()
                .map(ctx -> ctx.immutableNode(label))
                .orElseGet(() -> new ImmutableNode(label));
    }

    public static NodePoint loc(String record) {
        return loc(record, null);
    }

    public static NodePoint loc(Compass compass) {
        return loc(null, compass);
    }

    public static NodePoint loc(String record, Compass compass) {
        return new ImmutableNodePoint(null, record, compass);
    }


    public static MutableGraph mutGraph() {
        return new MutableGraph();
    }

    public static MutableGraph mutGraph(String name) {
        return new MutableGraph().setLabel(name);
    }

    public static MutableGraph mutGraph(Label label) {
        return new MutableGraph().setLabel(label);
    }

    public static MutableNode mutNode(String name) {
        return mutNode(Label.of(name));
    }

    public static MutableNode mutNode(Label label) {
        return CreationContext.current()
                .map(ctx -> ctx.mutableNode(label))
                .orElseGet(() -> new MutableNode().setLabel(label));
    }

    public static MutableNodePoint mutLoc(String record) {
        return new MutableNodePoint().setRecord(record);
    }

    public static MutableNodePoint mutLoc(Compass compass) {
        return new MutableNodePoint().setCompass(compass);
    }

    public static MutableNodePoint mutLoc(String record, Compass compass) {
        return new MutableNodePoint().setRecord(record).setCompass(compass);
    }


    public static Label name(String name) {
        return Label.of(name);
    }

    public static Label html(String html) {
        return Label.html(html);
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
