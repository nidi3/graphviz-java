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

import static java.util.Locale.ENGLISH;

public final class Style<T extends For> implements Attributes<T> {
    @Nullable
    private final String value;
    @Nullable
    private final Color color;
    @Nullable
    private final Double width;
    @Nullable
    private final Arrow.DirType dirType;

    public static final Style<ForNodeLink>
            DASHED = new Style<>("dashed"),
            DOTTED = new Style<>("dotted"),
            SOLID = new Style<>("solid"),
            INVIS = new Style<>("invis"),
            BOLD = new Style<>("bold");
    public static final Style<ForGraphNode>
            FILLED = new Style<>("filled"),
            STRIPED = new Style<>("striped"),
            ROUNDED = new Style<>("rounded"),
            RADIAL = new Style<>("radial");
    public static final Style<ForNode>
            WEDGED = new Style<>("wedged"),
            DIAGONALS = new Style<>("diagonals");

    private Style(@Nullable String value,
                  @Nullable Color color, @Nullable Double width, @Nullable Arrow.DirType dirType) {
        this.value = value;
        this.color = color;
        this.width = width;
        this.dirType = dirType;
    }

    private Style(String value) {
        this(value, null, null, null);
    }

    public static Style<ForLink> tapered(double width) {
        return new Style<>("tapered", null, width, null);
    }

    public static Style<ForLink> tapered(double width, Arrow.DirType dir) {
        return new Style<>("tapered", null, width, dir);
    }

    public static Style<ForGraphNode> striped(Color color, Color... colors) {
        return new Style<>("striped", color.and(colors), null, null);
    }

    public static Style<ForGraphNode> wedged(Color color, Color... colors) {
        return new Style<>("wedged", color.and(colors), null, null);
    }

    public static Style<ForAll> lineWidth(double width) {
        return new Style<>(null, null, width, null);
    }

    @SafeVarargs
    public static <S extends For> Style<S> combine(Style<? extends S> style, Style<? extends S>... styles) {
        Style<S> res = and(style, null);
        for (final Style<? extends S> s : styles) {
            res = and(res, s);
        }
        return res;
    }

    private static <S extends For> Style<S> and(Style<? extends S> a, @Nullable Style<? extends S> b) {
        return b == null
                ? new Style<>(a.value, a.color, a.width, a.dirType)
                : new Style<>(b.value == null ? a.value : a.value == null ? b.value : a.value + "," + b.value,
                b.color == null ? a.color : b.color,
                b.width == null ? a.width : b.width,
                b.dirType == null ? a.dirType : b.dirType);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Attributes<? super T> applyTo(MapAttributes<? super T> attrs) {
        if (value != null) {
            attrs.add("style", value);
        }
        if (color != null) {
            color.applyTo((MapAttributes) attrs);
        }
        if (width != null) {
            attrs.add("penwidth", width);
        }
        if (dirType != null) {
            attrs.add("dir", dirType.name().toLowerCase(ENGLISH));
        }
        return attrs;
    }
}

