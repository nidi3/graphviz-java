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
package guru.nidi.graphviz;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Support graphviz inside javadoc.
 * <p>
 * {@graphviz
 * graph test {
 * rankdir=LR
 * a -- b
 * b -- c [color=red]
 * }
 * }
 * </p>
 * end
 */
public class GraphvizTaglet implements Taglet {
    private static final Pattern START_PATTERN = Pattern.compile("^\\s*(di)?graph\\s*(.*?)\\s\\{");

    public static void register(Map<String, Taglet> taglets) {
        final GraphvizTaglet taglet = new GraphvizTaglet();
        taglets.put(taglet.getName(), taglet);
    }

    @Override
    public boolean inField() {
        return false;
    }

    @Override
    public boolean inConstructor() {
        return false;
    }

    @Override
    public boolean inMethod() {
        return false;
    }

    @Override
    public boolean inOverview() {
        return false;
    }

    @Override
    public boolean inPackage() {
        return false;
    }

    @Override
    public boolean inType() {
        return false;
    }

    @Override
    public boolean isInlineTag() {
        return true;
    }

    @Override
    public String getName() {
        return "graphviz";
    }

    @Override
    public String toString(Tag tag) {
        try {
            final File file = new File(packageOf(tag), imageNameOf(tag));
            final File output = Graphviz.fromString(tag.text()).render(Format.PNG).toFile(file);
            return "<img title='" + imageTitleOf(tag) + "' src='" + output.getName() + "'></img>";
        } catch (IOException e) {
            throw new RuntimeException("Problem writing graphviz file", e);
        }
    }

    private String imageTitleOf(Tag tag) {
        final Matcher matcher = START_PATTERN.matcher(tag.text());
        return matcher.find() ? matcher.group(2) : "";
    }

    private String imageNameOf(Tag tag) {
        final String name = tag.position().file().getName();
        final String simple = name.substring(0, name.lastIndexOf('.'));
        return simple + "@" + tag.position().line();
    }

    private String packageOf(Tag tag) {
        final String className = tag.holder().toString();
        final String pack = className.substring(0, className.lastIndexOf('.'));
        return pack.replace('.', '/');
    }

    @Override
    public String toString(Tag[] tags) {
        return null;
    }
}
