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
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Node;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static guru.nidi.graphviz.engine.Format.PNG;
import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;

public final class FontTools {
    private FontTools() {
    }

    public static List<String> availableFontNames() {
        return Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
    }

    public static void createFontTest(String name, double adjust, File output) throws IOException {
        final Node width = node("If text is too narrow, increase fontAdjust. If it's too wide, decrease it.");
        final Node center = node(Label.html("A very long node label that should be centered inside the border<br/>"
                + "If text is too much left, increase fontAdjust.<br/>"
                + "If it's too much right, decrease it."));
        Graphviz.fromGraph(graph()
                .nodeAttr().with(Font.name(name), Shape.RECTANGLE)
                .with(width.link(center)))
                .fontAdjust(adjust)
                .render(PNG)
                .toFile(output);
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Available fonts: " + availableFontNames());
        if (args.length < 2) {
            System.out.println("Usage: FontTools <font name> <font adjust>");
            System.exit(0);
        }
        try {
            final double adjust = Double.parseDouble(args[1]);
            final String filename = "font-test.png";
            System.out.println("Creating test image '" + filename
                    + "' with font=" + args[0] + " and font adjust=" + adjust);
            createFontTest(args[0], adjust, new File(filename));
        } catch (NumberFormatException e) {
            System.out.println("Illegal number " + args[1]);
        }
    }
}
