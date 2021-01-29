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

import guru.nidi.graphviz.model.SvgSizeAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static guru.nidi.graphviz.engine.Format.*;

class SvgSizeAdjuster implements GraphvizPostProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(SvgSizeAdjuster.class);

    @Override
    public EngineResult postProcess(EngineResult result, Options options, ProcessOptions procOptions) {
        if (options.format != SVG && options.format != SVG_STANDALONE && options.format != PNG) {
            return result;
        }
        return result.mapString(s -> doPostProcess(s, procOptions, options.format != SVG_STANDALONE));
    }

    private String doPostProcess(String result, ProcessOptions procOptions, boolean prefix) {
        final String unprefixed = prefix ? withoutPrefix(result) : result;
        return pointsToPixels(unprefixed, procOptions.dpi, procOptions.width, procOptions.height, procOptions.scale);

    }

    private static String withoutPrefix(String svg) {
        final int pos = svg.indexOf("<svg ");
        return pos < 0 ? svg : svg.substring(pos);
    }

    private static String pointsToPixels(String svg, double dpi, int width, int height, double scale) {
        try {
            final SvgSizeAnalyzer analyzer = SvgSizeAnalyzer.svg(svg);
            setSize(analyzer, width, height, scale);
            setScale(analyzer, dpi);
            return analyzer.getSvg();
        } catch (IllegalArgumentException e) {
            LOG.warn(e.getMessage());
            return svg;
        }
    }

    private static void setSize(SvgSizeAnalyzer analyzer, int width, int height, double scale) {
        double w = analyzer.getWidth();
        double h = analyzer.getHeight();
        if (width > 0 && height > 0) {
            w = width;
            h = height;
        } else if (width > 0) {
            h *= width / w;
            w = width;
        } else if (height > 0) {
            w *= height / h;
            h = height;
        }
        analyzer.setSize((int) Math.round(w * scale), (int) Math.round(h * scale));
    }

    private static void setScale(SvgSizeAnalyzer analyzer, double dpi) {
        final double pixelScale = analyzer.getUnit().equals("px") ? 1 : Math.round(10000 * dpi / 72) / 10000d;
        final double scaleX = analyzer.getScaleX() / pixelScale;
        final double scaleY = analyzer.getScaleY() / pixelScale;
        analyzer.setScale(scaleX, scaleY);
    }

}
