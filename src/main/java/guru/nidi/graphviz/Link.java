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
public class Link extends Attributed<Link> {
    final LinkTarget from;
    final LinkTarget to;

    private Link(LinkTarget from, LinkTarget to) {
        this.from = from;
        this.to = to;
        final CreationContext ctx = CreationContext.current();
        if (ctx != null) {
            attrs(ctx.links());
        }
    }

    public static Link to(LinkTarget to) {
        return new Link(null, to);
    }

    public static Link to(Node to) {
        return to(NodePoint.of(to));
    }

    public static Link between(LinkTarget from, LinkTarget to) {
        return new Link(from, to);
    }

    public static Link between(Node from, Node to) {
        return between(NodePoint.of(from), NodePoint.of(to));
    }
}
