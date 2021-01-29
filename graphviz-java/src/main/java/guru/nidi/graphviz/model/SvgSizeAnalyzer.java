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
package guru.nidi.graphviz.model;

import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.DOTALL;

public final class SvgSizeAnalyzer {
    private static final Pattern TRANSFORM_PATTERN = Pattern.compile(
            "scale\\((?<scaleX>[0-9.]+) (?<scaleY>[0-9.]+)\\) "
                    + "rotate\\((?<rotate>[0-9.]+)\\) "
                    + "translate\\((?<translateX>[0-9.]+) (?<translateY>[0-9.]+)\\)",
            DOTALL);
    private static final Pattern SVG_PATTERN = Pattern.compile(
            "<svg width=\"(?<width>\\d+)(?<unit>p[tx])\" height=\"(?<height>\\d+)p[tx]\""
                    + "(?<between>.*?>\\R<g.*?)transform=\""
                    + TRANSFORM_PATTERN.pattern(),
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
    @Nullable
    private Double rotate;
    @Nullable
    private Double translateX;
    @Nullable
    private Double translateY;

    public static SvgSizeAnalyzer svg(String svg) {
        return new SvgSizeAnalyzer(SVG_PATTERN, svg);
    }

    public static SvgSizeAnalyzer transform(String transform) {
        return new SvgSizeAnalyzer(TRANSFORM_PATTERN, transform);
    }

    private SvgSizeAnalyzer(Pattern pattern, String input) {
        matcher = pattern.matcher(input);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Generated SVG has not the expected format. "
                    + "There might be image size problems.");
        }
    }

    public String getSvg() {
        final String size = width == null
                ? "width=\"" + getWidth() + getUnit() + "\" height=\"" + getHeight() + getUnit() + "\""
                : "width=\"" + width + "px\" height=\"" + height + "px\"";
        return matcher.replaceFirst("<svg " + size + matcher.group("between")
                + "transform=\"" + getTransform());
    }

    public String getTransform() {
        final String scale = scaleX == null
                ? "scale(" + getScaleX() + " " + getScaleY() + ") "
                : "scale(" + scaleX + " " + scaleY + ") ";
        final String rot = rotate == null
                ? "rotate(" + getRotate() + ") "
                : "rotate(" + rotate + ") ";
        final String translate = translateX == null
                ? "translate(" + getTranslateX() + " " + getTranslateY() + ")"
                : "translate(" + translateX + " " + translateY + ")";
        return scale + rot + translate;
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

    public void setRotate(double rotate) {
        this.rotate = rotate;
    }

    public void setTranslate(double translateX, double translateY) {
        this.translateX = translateX;
        this.translateY = translateY;
    }
}
