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

public final class Image implements Attributes<ForNode> {
    public enum Position {
        TOP_LEFT("tl"),
        TOP_CENTER("tc"),
        TOP_RIGHT("tr"),
        MIDDLE_LEFT("ml"),
        MIDDLE_CENTER("mc"),
        MIDDLE_RIGHT("mr"),
        BOTTOM_LEFT("bl"),
        BOTTOM_CENTER("bc"),
        BOTTOM_RIGHT("br");

        String value;

        Position(String value) {
            this.value = value;
        }
    }

    public enum Scale {
        NONE("false"),
        FIT("true"),
        WIDTH("width"),
        HEIGHT("height"),
        BOTH("both");

        String value;

        Scale(String value) {
            this.value = value;
        }
    }

    private final String path;
    private final Position position;
    private final Scale scale;

    private Image(String path, Position position, Scale scale) {
        this.path = path;
        this.position = position;
        this.scale = scale;
    }

    public static Image of(String path) {
        return new Image(path, Position.MIDDLE_CENTER, Scale.NONE);
    }

    public Image position(Position position) {
        return new Image(path, position, scale);
    }

    public Image scale(Scale scale) {
        return new Image(path, position, scale);
    }

    @Override
    public Attributes<? super ForNode> applyTo(MapAttributes<? super ForNode> attrs) {
        attrs.add("image", path);
        if (position != Position.MIDDLE_CENTER) {
            attrs.add("imagepos", position.value);
        }
        if (scale != Scale.NONE) {
            attrs.add("imagescale", scale.value);
        }
        return attrs;
    }
}
