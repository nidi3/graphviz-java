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
import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static guru.nidi.graphviz.engine.GraphvizLoader.readAsBytes;
import static guru.nidi.graphviz.engine.StringFunctions.replaceNonWordChars;
import static guru.nidi.graphviz.engine.TempFiles.tempDir;
import static guru.nidi.graphviz.service.SystemUtils.uriPathOf;
import static java.lang.Integer.parseInt;
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

    public final Engine engine;
    public final Format format;
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
        return new Options(Engine.DOT, Format.SVG, null, null, new File("."), new ArrayList<>());
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
                hasMemory ? parseInt(memory.group(1)) : null,
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

    public Options image(String path) {
        images.add(Image.create(path, completePath(path)));
        return this;
    }

    public String processImagePath(String originalPath) {
        return images.stream()
                .filter(i -> i.originalPath.equals(completePath(originalPath)))
                .map(i -> i.processPath)
                .findFirst().orElse(originalPath);
    }

    public String originalImagePath(String processPath) {
        return images.stream()
                .filter(i -> i.processPath.equals(processPath))
                .map(i -> i.originalPath)
                .findFirst().orElse(processPath);
    }

    private String completePath(String path) {
        return isUrl(path) || new File(path).isAbsolute() || basedir.getPath().equals(".")
                ? path
                : uriPathOf(new File(basedir, path).getPath());
    }

    static boolean isUrl(String path) {
        return path.startsWith("http://") || path.startsWith("https://");
    }

    public String toJson(boolean raw) {
        final String form = "format:'" + (raw ? format : format.vizName) + "'";
        final String eng = ",engine:'" + (raw ? engine : engine.toString().toLowerCase(ENGLISH)) + "'";
        final String mem = totalMemory == null ? "" : (",totalMemory:'" + totalMemory + "'");
        final String yInv = yInvert == null ? "" : (",yInvert:" + yInvert);
        final String base = ",basedir:'" + uriPathOf(basedir) + "'";
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

        final String originalPath;
        final String processPath;
        final int width;
        final int height;

        static Image create(String path, String completePath) {
            try {
                if (isUrl(path)) {
                    final Path dir = tempDir("Images");
                    final File file = dir.resolve(replaceNonWordChars(path)).toFile();
                    try (final InputStream in = new URL(path).openStream();
                         final OutputStream out = new FileOutputStream(file)) {
                        out.write(readAsBytes(in));
                    }
                    final BufferedImage image = ImageIO.read(file);
                    return new Image(path, uriPathOf(file), image.getWidth(), image.getHeight());
                }
                final BufferedImage image = ImageIO.read(new File(completePath));
                return new Image(completePath, uriPathOf(new File(completePath)), image.getWidth(), image.getHeight());
            } catch (IOException e) {
                throw new GraphvizException("Could not load image '" + path + "'.", e);
            }
        }

        private Image(String originalPath, String processPath, int width, int height) {
            this.originalPath = originalPath;
            this.processPath = processPath;
            this.width = width;
            this.height = height;
        }

        String toJson() {
            return "{path:'" + processPath + "',width:'" + width + "px',height:'" + height + "px'}";
        }

        static Image fromJson(String json) {
            final Matcher path = PATH.matcher(json);
            path.find();
            final Matcher width = WIDTH.matcher(json);
            width.find();
            final Matcher height = HEIGHT.matcher(json);
            height.find();

            return new Image(path.group(1), path.group(1), parseInt(width.group(1)), parseInt(height.group(1)));
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
                    && originalPath.equals(image.originalPath)
                    && processPath.equals(image.processPath);
        }

        @Override
        public int hashCode() {
            return Objects.hash(originalPath, processPath, width, height);
        }

        @Override
        public String toString() {
            return "Image{"
                    + "originalPath='" + originalPath + '\''
                    + ", processPath='" + processPath + '\''
                    + ", width=" + width
                    + ", height=" + height
                    + '}';
        }
    }
}
