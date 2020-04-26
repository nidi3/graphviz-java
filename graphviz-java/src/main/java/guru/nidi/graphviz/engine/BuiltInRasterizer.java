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
package guru.nidi.graphviz.engine;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

class BuiltInRasterizer implements Rasterizer {
    final String format;
    @Nullable
    final String renderer;
    @Nullable
    final String formatter;

    BuiltInRasterizer(String format, @Nullable String renderer, @Nullable String formatter) {
        this.format = format;
        this.renderer = renderer;
        this.formatter = formatter;
    }

    @Override
    public Format format() {
        return Format.PNG;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public BufferedImage rasterize(Graphviz graphviz, Consumer<Graphics2D> graphicsConfigurer, String input) {
        return null;
    }
}
