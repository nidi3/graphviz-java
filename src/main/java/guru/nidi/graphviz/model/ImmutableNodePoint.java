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

class ImmutableNodePoint extends MutableNodePoint implements NodePoint {
    ImmutableNodePoint(MutableNode node, String record, Compass compass) {
        super(node, record, compass);
    }

    public ImmutableNodePoint loc(String record) {
        return new ImmutableNodePoint(node, record, compass);
    }

    public ImmutableNodePoint loc(Compass compass) {
        return new ImmutableNodePoint(node, record, compass);
    }

    public ImmutableNodePoint loc(String record, Compass compass) {
        return new ImmutableNodePoint(node, record, compass);
    }

    public Node link(LinkTarget target) {
        return (Node) new ImmutableNodePoint(node.copy(), record, compass).addLink(target);
    }
}
