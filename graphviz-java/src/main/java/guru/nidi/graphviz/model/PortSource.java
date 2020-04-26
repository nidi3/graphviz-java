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

import java.util.List;

@SuppressWarnings("ConstantConditions")
class PortSource implements LinkSource {
    final Port port;

    PortSource(Port port) {
        this.port = port;
    }

    @Override
    public Label name() {
        return null;
    }

    @Override
    public List<Link> links() {
        return null;
    }

    @Override
    public Link linkTo(LinkTarget target) {
        return null;
    }

    @Override
    public void addTo(MutableGraph graph) {
    }

    @Override
    public LinkTarget asLinkTarget() {
        return null;
    }
}
