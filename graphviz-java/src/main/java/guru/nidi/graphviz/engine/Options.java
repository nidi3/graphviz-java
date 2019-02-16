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
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.emptyList;
import static java.util.Locale.ENGLISH;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/*
 * Copyright (C) 2015 Stefan Niederhauser (nidin@gmx.ch)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public final class Options {
    private static final Pattern
            FORMAT = Pattern.compile("format:'(.*?)'"),
            ENGINE = Pattern.compile("engine:'(.*?)'"),
            MEMORY = Pattern.compile("totalMemory:'(.*?)'"),
            Y_INVERT = Pattern.compile("yInvert:(.*?)[, }]"),
            BASE_DIR = Pattern.compile("basedir:'(.*?)'"),
            IMAGES = Pattern.compile("images:\\[(.*?)]");

    final Engine engine;
    final Format format;
    @Nullable
    final Integer totalMemory;
    @Nullable
    final Boolean yInvert;
    final File basedir;
    final List<Image> images;

    private Options(Engine engine, Format format, @Nullable Integer totalMemory,
                    @Nullable Boolean yInvert, File basedir, List<Image> images) {
        this.engine = engine;
        this.format = format;
        this.totalMemory = totalMemory;
        this.yInvert = yInvert;
        this.basedir = basedir;
        this.images = images;
    }

    public static Options create() {
        return new Options(Engine.DOT, Format.SVG, null, null, new File("."), emptyList());
    }

    public static Options fromJson(String json) {
        final Matcher format = FORMAT.matcher(json);
        format.find();
        final Matcher engine = ENGINE.matcher(json);
        engine.find();
        final Matcher memory = MEMORY.matcher(json);
        final boolean hasMemory = memory.find();
        final Matcher yInvert = Y_INVERT.matcher(json);
        final boolean hasYInvert = yInvert.find();
        final Matcher basedir = BASE_DIR.matcher(json);
        final boolean hasBasedir = basedir.find();
        final Matcher imgs = IMAGES.matcher(json);
        final boolean hasImgs = imgs.find() && imgs.group(1).length() > 0;
        final String[] imgList = hasImgs ? imgs.group(1).split("},\\{") : new String[0];

        return new Options(
                Engine.valueOf(engine.group(1)),
                Format.valueOf(format.group(1)),
                hasMemory ? Integer.parseInt(memory.group(1)) : null,
                hasYInvert ? Boolean.parseBoolean(yInvert.group(1)) : null,
                new File(hasBasedir ? basedir.group(1) : "."),
                Arrays.stream(imgList).map(Image::fromJson).collect(toList()));
    }

    public Options engine(Engine engine) {
        return new Options(engine, format, totalMemory, yInvert, basedir, images);
    }

    public Options format(Format format) {
        return new Options(engine, format, totalMemory, yInvert, basedir, images);
    }

    public Options totalMemory(@Nullable Integer totalMemory) {
        return new Options(engine, format, totalMemory, yInvert, basedir, images);
    }

    public Options yInvert(@Nullable Boolean yInvert) {
        return new Options(engine, format, totalMemory, yInvert, basedir, images);
    }

    public Options basedir(File basedir) {
        return new Options(engine, format, totalMemory, yInvert, basedir, images);
    }

    public Options image(String image) {
        final List<Image> imgs = new ArrayList<>(this.images);
        imgs.add(loadImage(image));
        return new Options(engine, format, totalMemory, yInvert, basedir, imgs);
    }

    private Image loadImage(String path) {
        if (path.startsWith("http://") || path.startsWith("https://")) {
            try {
                final BufferedImage img = ImageIO.read(new URL(path));
                return new Image(path, img.getWidth(), img.getHeight());
            } catch (IOException e) {
                throw new GraphvizException("Problem loading image " + path, e);
            }
        }
        final File file = new File(path).isAbsolute() ? new File(path) : new File(basedir, path);
        try {
            final BufferedImage img = ImageIO.read(file);
            return new Image(file.getAbsolutePath(), img.getWidth(), img.getHeight());
        } catch (IOException e) {
            throw new GraphvizException("Problem loading image " + file, e);
        }

    }

    public String toJson(boolean raw) {
        final String form = "format:'" + (raw ? format : format.vizName) + "'";
        final String eng = ",engine:'" + (raw ? engine : engine.toString().toLowerCase(ENGLISH)) + "'";
        final String mem = totalMemory == null ? "" : (",totalMemory:'" + totalMemory + "'");
        final String yInv = yInvert == null ? "" : (",yInvert:" + yInvert);
        final String base = ",basedir:'" + basedir.getAbsolutePath() + "'";
        final String imgs = ",images:[" + images.stream().map(Image::toJson).collect(joining(",")) + "]";
        return "{" + form + eng + mem + yInv + base + imgs + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Options options = (Options) o;
        return engine == options.engine
                && format == options.format
                && Objects.equals(totalMemory, options.totalMemory)
                && Objects.equals(yInvert, options.yInvert)
                && basedir.equals(options.basedir)
                && images.equals(options.images);
    }

    @Override
    public String toString() {
        return "Options{"
                + "engine=" + engine
                + ", format=" + format
                + ", totalMemory=" + totalMemory
                + ", yInvert=" + yInvert
                + ", basedir=" + basedir
                + ", images=" + images
                + '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(engine, format, totalMemory, yInvert, basedir, images);
    }

    private static class Image {
        private static final Pattern
                PATH = Pattern.compile("path:'(.*?)'"),
                WIDTH = Pattern.compile("width:'(.*?)px'"),
                HEIGHT = Pattern.compile("height:'(.*?)px'");

        final String path;
        final int width;
        final int height;

        Image(String path, int width, int height) {
            this.path = path;
            this.width = width;
            this.height = height;
        }

        String toJson() {
            return "{path:'" + path + "',width:'" + width + "px',height:'" + height + "px'}";
        }

        static Image fromJson(String json) {
            final Matcher path = PATH.matcher(json);
            path.find();
            final Matcher width = WIDTH.matcher(json);
            width.find();
            final Matcher height = HEIGHT.matcher(json);
            height.find();

            return new Image(path.group(1), Integer.parseInt(width.group(1)), Integer.parseInt(height.group(1)));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final Image image = (Image) o;
            return width == image.width
                    && height == image.height
                    && path.equals(image.path);
        }

        @Override
        public int hashCode() {
            return Objects.hash(path, width, height);
        }

        @Override
        public String toString() {
            return "Image{"
                    + "path='" + path + '\''
                    + ", width=" + width
                    + ", height=" + height
                    + '}';
        }
    }
}
