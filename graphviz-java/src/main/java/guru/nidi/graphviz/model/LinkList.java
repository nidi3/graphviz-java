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

import java.util.*;

import static java.util.stream.Collectors.toList;

class LinkList extends ArrayList<Link> {
    private final LinkSource owner;

    LinkList(LinkSource owner, List<Link> links) {
        super(links);
        this.owner = owner;
    }

    @Override
    public Link set(int index, Link element) {
        return super.set(index, owner.linkTo(element));
    }

    @Override
    public boolean add(Link link) {
        return super.add(owner.linkTo(link));
    }

    @Override
    public void add(int index, Link element) {
        super.add(index, owner.linkTo(element));
    }

    @Override
    public boolean addAll(Collection<? extends Link> c) {
        return super.addAll(c.stream().map(owner::linkTo).collect(toList()));
    }

    @Override
    public boolean addAll(int index, Collection<? extends Link> c) {
        return super.addAll(index, c.stream().map(owner::linkTo).collect(toList()));
    }
}
