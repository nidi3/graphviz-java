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
package guru.nidi.graphviz.use;

import guru.nidi.graphviz.attribute.Font;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.*;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.GraphvizV8Engine;
import guru.nidi.graphviz.model.MutableGraph;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import static guru.nidi.graphviz.attribute.Label.of;
import static guru.nidi.graphviz.attribute.Rank.RankDir.LEFT_TO_RIGHT;
import static guru.nidi.graphviz.engine.Format.PNG;
import static guru.nidi.graphviz.model.Factory.mutGraph;
import static guru.nidi.graphviz.model.Factory.node;
import static java.util.Arrays.asList;

public final class FontTools {
    private FontTools() {
    }

    public static List<String> availableFontNames() {
        return asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
    }

    public static void availableFontNamesGraph(File output) throws IOException {
        final MutableGraph g = mutGraph()
                .graphAttrs().add(Rank.dir(LEFT_TO_RIGHT))
                .nodeAttrs().add(Size.mode(Size.Mode.MINIMUM).margin(.1, .1).size(0, 0), Shape.RECTANGLE);
        final List<String> fonts = availableFontNames();
        fonts.sort(Comparator.reverseOrder());
        for (final String f : fonts) {
            g.add(node(f + "2").with(of("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz")).with(Font.name(f)));
            g.add(node(f + "3").with(of("ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ")).with(Font.name(f)));
            g.add(node(f).with(Font.name(f)));
        }
        Graphviz.fromGraph(g).render(PNG).toFile(output);
    }

    public static void main(String[] args) throws IOException {
        Graphviz.useEngine(new GraphvizV8Engine());
        System.out.println("Available fonts: " + availableFontNames());
        final String fontShow = "font-show.png";
        System.out.println("Creating image with all fonts '" + fontShow + "'.");
        availableFontNamesGraph(new File(fontShow));
        if (args.length < 2) {
            System.out.println("Usage: FontTools <font name> <font adjust>");
            System.exit(0);
        }
    }
}
