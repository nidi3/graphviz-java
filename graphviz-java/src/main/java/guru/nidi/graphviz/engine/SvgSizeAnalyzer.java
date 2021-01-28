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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.DOTALL;

class SvgSizeAnalyzer {
    private static final Pattern SVG_PATTERN = Pattern.compile(
            "<svg width=\"(?<width>\\d+)(?<unit>p[tx])\" height=\"(?<height>\\d+)p[tx]\""
                    + "(?<between>.*?>\\R<g.*?)transform=\""
                    + "scale\\((?<scaleX>[0-9.]+) (?<scaleY>[0-9.]+)\\) "
                    + "rotate\\((?<rotate>[0-9.]+)\\) "
                    + "translate\\((?<translateX>[0-9.]+) (?<translateY>[0-9.]+)\\)",
            DOTALL);
    private final Matcher matcher;
    @Nullable
    private Integer width;
    @Nullable
    private Integer height;
    @Nullable
    private Double scaleX;
    @Nullable
    private Double scaleY;

    SvgSizeAnalyzer(String svg) {
        matcher = SVG_PATTERN.matcher(svg);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Generated SVG has not the expected format. "
                    + "There might be image size problems.");
        }
    }

    public String adjusted() {
        final String size = width == null
                ? "width=\"" + getWidth() + getUnit() + "\" height=\"" + getHeight() + getUnit() + "\""
                : "width=\"" + width + "px\" height=\"" + height + "px\"";
        final String scale = scaleX == null
                ? "scale(" + getScaleX() + " " + getScaleY() + ") "
                : "scale(" + scaleX + " " + scaleY + ") ";
        final String rotate = "rotate(" + getRotate() + ") ";
        final String translate = "translate(" + getTranslateX() + " " + getTranslateY() + ")";
        return matcher.replaceFirst("<svg " + size + matcher.group("between") +
                "transform=\"" + scale + rotate + translate);
    }

    public int getWidth() {
        return Integer.parseInt(matcher.group("width"));
    }

    public int getHeight() {
        return Integer.parseInt(matcher.group("height"));
    }

    public String getUnit() {
        return matcher.group("unit");
    }

    public double getScaleX() {
        return Double.parseDouble(matcher.group("scaleX"));
    }

    public double getScaleY() {
        return Double.parseDouble(matcher.group("scaleY"));
    }

    public double getRotate() {
        return Double.parseDouble(matcher.group("rotate"));
    }

    public double getTranslateX() {
        return Double.parseDouble(matcher.group("translateX"));
    }

    public double getTranslateY() {
        return Double.parseDouble(matcher.group("translateY"));
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setScale(double scaleX, double scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }
}
