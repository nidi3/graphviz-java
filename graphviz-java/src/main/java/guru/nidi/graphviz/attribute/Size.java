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

import static guru.nidi.graphviz.attribute.NodeAttr.nodeAttr;

public final class Size implements Attributes<ForNode> {
    public enum Mode {
        FIXED("true"), MINIMUM("false"), SHAPE("shape");

        final String value;

        Mode(String value) {
            this.value = value;
        }
    }

    @Nullable
    private final Mode mode;
    @Nullable
    private final Double width;
    @Nullable
    private final Double height;
    @Nullable
    private final Double marginX;
    @Nullable
    private final Double marginY;

    private Size(@Nullable Mode mode, @Nullable Double width, @Nullable Double height,
                 @Nullable Double marginX, @Nullable Double marginY) {
        this.mode = mode;
        this.width = width;
        this.height = height;
        this.marginX = marginX;
        this.marginY = marginY;
    }

    public static Size mode(Mode mode) {
        return new Size(mode, null, null, null, null);
    }

    public static Size std() {
        return new Size(null, null, null, null, null);
    }

    public Size size(double width, double height) {
        return new Size(mode, width, height, marginX, marginY);
    }

    public Size margin(double marginX, double marginY) {
        return new Size(mode, width, height, marginX, marginY);
    }

    @Override
    public Attributes<? super ForNode> applyTo(MapAttributes<? super ForNode> attrs) {
        if (mode != null) {
            nodeAttr("fixedsize", mode.value).applyTo(attrs);
        }
        if (width != null) {
            nodeAttr("width", width).applyTo(attrs);
            nodeAttr("height", height).applyTo(attrs);
        }
        if (marginX != null) {
            nodeAttr("margin", marginX + "," + marginY).applyTo(attrs);
        }
        return attrs;
    }
}
