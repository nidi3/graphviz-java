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

import javax.annotation.Nullable;

public final class Rank {
    private Rank() {
    }

    public static GraphRank dir(RankDir dir) {
        return new GraphRank(dir, null, false, true, false);
    }

    public static GraphRank sep(double sep) {
        return new GraphRank(null, sep, false, true, false);
    }

    public static GraphRank sepEqually(double sep) {
        return new GraphRank(null, sep, true, true, false);
    }

    public static GraphRank newRank() {
        return new GraphRank(null, null, false, true, true);
    }

    public static Attributes<ForGraph> inSubgraph(RankType rankType) {
        return new SubgraphRank(rankType);
    }

    private static class SubgraphRank extends SingleAttributes<String, ForGraph> {
        SubgraphRank(RankType rankType) {
            super("rank", rankType.value);
        }
    }

    public static class GraphRank implements Attributes<ForGraph> {
        @Nullable
        private final RankDir dir;
        @Nullable
        private final Double sep;
        private final boolean equally;
        private final boolean cluster;
        private final boolean newRank;

        GraphRank(@Nullable RankDir dir, @Nullable Double sep, boolean equally,
                  boolean cluster, boolean newRank) {
            this.dir = dir;
            this.sep = sep;
            this.equally = equally;
            this.cluster = cluster;
            this.newRank = newRank;
        }

        public GraphRank dir(RankDir dir) {
            return new GraphRank(dir, sep, equally, cluster, newRank);
        }

        public GraphRank sep(double sep) {
            return new GraphRank(dir, sep, equally, cluster, newRank);
        }

        public GraphRank sepEqually(double sep) {
            return new GraphRank(dir, sep, true, cluster, newRank);
        }

        public GraphRank noCluster() {
            return cluster(false);
        }

        public GraphRank cluster(boolean cluster) {
            return new GraphRank(dir, sep, equally, cluster, newRank);
        }

        public GraphRank newRank() {
            return newRank(true);
        }

        public GraphRank newRank(boolean newRank) {
            return new GraphRank(dir, sep, equally, cluster, newRank);
        }

        @Override
        public Attributes<? super ForGraph> applyTo(MapAttributes<? super ForGraph> attrs) {
            if (dir != null) {
                attrs.add("rankdir", dir.value);
            }
            if (!cluster) {
                attrs.add("clusterrank", "global");
            }
            if (sep != null || equally) {
                attrs.add("ranksep", (sep == null ? "" : sep) + (equally ? " equally" : ""));
            }
            if (newRank) {
                attrs.add("newrank", true);
            }
            return attrs;
        }
    }

    public enum RankType {
        SAME("same"),
        MIN("min"),
        MAX("max"),
        SOURCE("source"),
        SINK("sink");
        final String value;

        RankType(String value) {
            this.value = value;
        }
    }

    public enum RankDir {
        TOP_TO_BOTTOM("TB"),
        BOTTOM_TO_TOP("BT"),
        LEFT_TO_RIGHT("LR"),
        RIGHT_TO_LEFT("RL");
        final String value;

        RankDir(String value) {
            this.value = value;
        }
    }
}
