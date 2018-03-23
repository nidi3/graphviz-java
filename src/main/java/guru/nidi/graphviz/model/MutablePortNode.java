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

public class MutablePortNode implements LinkTarget, MutableLinkSource<MutableNode> {
    protected MutableNode node;
    protected String record;
    protected Compass compass;

    public MutablePortNode() {
    }

    protected MutablePortNode(MutableNode node, String record, Compass compass) {
        this.node = node;
        this.record = record;
        this.compass = compass;
    }

    public MutablePortNode copy() {
        return new MutablePortNode(node.copy(), record, compass);
    }

    public MutablePortNode setNode(MutableNode node) {
        this.node = node;
        return this;
    }

    public MutablePortNode setRecord(String record) {
        this.record = record;
        return this;
    }

    public MutablePortNode setCompass(Compass compass) {
        this.compass = compass;
        return this;
    }

    public MutableNode addLink(LinkTarget target) {
        return node.addLink(target);
    }

    @Override
    public Link linkTo() {
        return Link.to(this);
    }

    public MutableNode node() {
        return node;
    }

    public String record() {
        return record;
    }

    public Compass compass() {
        return compass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final MutablePortNode portNode = (MutablePortNode) o;

        if (node != null ? !node.equals(portNode.node) : portNode.node != null) {
            return false;
        }
        if (record != null ? !record.equals(portNode.record) : portNode.record != null) {
            return false;
        }
        return compass == portNode.compass;
    }

    @Override
    public int hashCode() {
        int result = node != null ? node.hashCode() : 0;
        result = 31 * result + (record != null ? record.hashCode() : 0);
        result = 31 * result + (compass != null ? compass.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return (record == null ? "" : record) + ":" + (compass == null ? "" : compass) + ":" + node.toString();
    }
}
