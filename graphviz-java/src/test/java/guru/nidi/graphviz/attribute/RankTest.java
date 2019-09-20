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
package guru.nidi.graphviz.attribute;

import org.junit.jupiter.api.Test;

import static guru.nidi.graphviz.attribute.Attributes.attr;
import static guru.nidi.graphviz.attribute.Attributes.attrs;
import static guru.nidi.graphviz.attribute.Rank.RankDir.LEFT_TO_RIGHT;
import static guru.nidi.graphviz.attribute.Rank.RankDir.TOP_TO_BOTTOM;
import static guru.nidi.graphviz.attribute.Rank.RankType.SAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RankTest {
    @Test
    void rank() {
        assertEquals(attrs(attr("rank", "same")), attrs(Rank.inSubgraph(SAME)));
    }

    @Test
    void dir() {
        assertEquals(attrs(attr("rankdir", "LR")), attrs(Rank.dir(LEFT_TO_RIGHT)));
    }

    @Test
    void sep() {
        assertEquals(attrs(attr("ranksep", "2.0")), attrs(Rank.sep(2)));
        assertEquals(attrs(attr("ranksep", "2.0 equally")), attrs(Rank.sepEqually(2)));
    }

    @Test
    void newRank() {
        assertEquals(attrs(attr("newrank", true)), attrs(Rank.newRank()));
    }

    @Test
    void combine() {
        assertEquals(attrs(attr("newrank", true), attr("clusterrank", "global"),
                attr("rankdir", "TB"), attr("ranksep", "3.0 equally")),
                attrs(Rank.sepEqually(2).newRank(true).noCluster().dir(TOP_TO_BOTTOM).sep(3)));
    }
}
