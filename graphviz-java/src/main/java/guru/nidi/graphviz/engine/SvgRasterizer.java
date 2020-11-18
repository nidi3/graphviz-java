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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

abstract class SvgRasterizer implements Rasterizer {
    @Override
    public Format format() {
        return Format.SVG;
    }

    @Override
    public BufferedImage rasterize(Graphviz graphviz, Consumer<Graphics2D> graphicsConfigurer, String input) {
        final String svg = input
                .replace("xlink:href=\"file://", "xlink:href= \"file://")
                .replace("xlink:href=\"http://", "xlink:href= \"http://")
                .replace("xlink:href=\"https://", "xlink:href= \"https://")
                .replace("xlink:href=\"", "xlink:href=\"file://")
                .replace("stroke=\"transparent\"", "stroke=\"#fff\" stroke-opacity=\"0.0\"")
                .replace("stroke: transparent", "stroke: #fff; stroke-opacity: 0.0")
                .replaceAll("stroke=\".*?:.*?\"", "stroke=\"none\"")
                .replace("fill=\"transparent\"", "fill=\"#fff\" fill-opacity=\"0.0\"")
                .replace("fill: transparent", "fill: #fff; fill-opacity: 0.0");
        return doRasterize(graphviz, graphicsConfigurer, svg);
    }

    abstract BufferedImage doRasterize(Graphviz graphviz, Consumer<Graphics2D> graphicsConfigurer, String svg);
}
