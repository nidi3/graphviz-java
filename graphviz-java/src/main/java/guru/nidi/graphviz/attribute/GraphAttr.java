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
package guru.nidi.graphviz.attribute;

import static java.util.Locale.ENGLISH;

/**
 * Graph attributes.
 */
public final class GraphAttr {
    private static final String SIZE = "size";

    /**
     * Center the drawing.
     * 
     * @see <a href="https://www.graphviz.org/docs/attrs/center/">center</a>
     */
    public static final Attributes<ForGraph> CENTER = new SingleAttributes<>("center", true);
    /**
     * Allow edges between clusters.
     * 
     * @see <a href="https://www.graphviz.org/docs/attrs/compound/">compound</a>
     */
    public static final Attributes<ForGraph> COMPOUND = new SingleAttributes<>("compound", true);
    /**
     * Use edge concentrators.
     * 
     * @see <a href="https://www.graphviz.org/docs/attrs/concentrate/">concentrate</a>
     */
    public static final Attributes<ForGraph> CONCENTRATE = new SingleAttributes<>("concentrate", true);
    /**
     * Do not force placements of all xlabels.
     * 
     * @see <a href="https://www.graphviz.org/docs/attrs/forcelabels/">forcelabels</a>
     */
    public static final Attributes<ForGraph> FORCE_LABELS_NOT = new SingleAttributes<>("forcelabels", false);
    /**
     * Set graph orientation to landscape.
     * 
     * @see <a href="https://www.graphviz.org/docs/attrs/orientation/">orientation</a>
     */
    public static final Attributes<ForGraph> LANDSCAPE = new SingleAttributes<>("orientation", "L");

    private GraphAttr() {
    }

    /**
     * Specifies the DPI for a graph.
     * 
     * @param dpi a DPI value
     * @return a DPI attribute
     * 
     * @see <a href="https://www.graphviz.org/docs/attrs/dpi/">dpi</a>
     */
    public static Attributes<ForGraph> dpi(int dpi) {
        return new SingleAttributes<>("dpi", dpi);
    }

    /**
     * Specifies the maximum size of the drawn graph.
     * 
     * @param size maximum size of the graph
     * @return a size attribute
     * 
     * @see <a href="https://www.graphviz.org/docs/attrs/size/">size</a>
     */
    public static Attributes<ForGraph> sizeMax(double size) {
        return new SingleAttributes<>(SIZE, size);
    }

    /**
     * Specifies the maximum size of the drawn graph.
     * 
     * @param sizeX maximum width of the graph
     * @param sizeY maximum height of the graph
     * @return a size attribute
     * 
     * @see <a href="https://www.graphviz.org/docs/attrs/size/">size</a>">
     */
    public static Attributes<ForGraph> sizeMax(double sizeX, double sizeY) {
        return new SingleAttributes<>(SIZE, sizeX + "," + sizeY);
    }

    /**
     * Specifies the preferred size of the drawn graph.
     * 
     * @param size preferred size of the graph
     * @return a size attribute
     * 
     * @see <a href="https://www.graphviz.org/docs/attrs/size/">size</a>
     */
    public static Attributes<ForGraph> sizePreferred(double size) {
        return new SingleAttributes<>(SIZE, size + "!");
    }

    /**
     * Specifies the preferred size of the drawn graph.
     * 
     * @param sizeX preferred width of the graph
     * @param sizeY preferred height of the graph
     * @return a size attribute
     * 
     * @see <a href="https://www.graphviz.org/docs/attrs/size/">size</a>
     */
    public static Attributes<ForGraph> sizePreferred(double sizeX, double sizeY) {
        return new SingleAttributes<>(SIZE, sizeX + "," + sizeY + "!");
    }

    /**
     * Possible options for splines.
     * 
     * @see GraphAttr#splines(SplineMode)
     */
    public enum SplineMode {
        LINE, SPLINE, POLYLINE, ORTHO, CURVED, NONE
    }

    /**
     * Controls if/how edges are represented.
     * 
     * @param mode spline mode
     * @return a splines attribute
     * 
     * @see <a href="https://www.graphviz.org/docs/attrs/splines/">dpi</a>
     */
    public static Attributes<ForGraph> splines(SplineMode mode) {
        return new SingleAttributes<>("splines", mode.toString().toLowerCase(ENGLISH));
    }

    /**
     * Specifies the padding around a graph.
     * 
     * @param pad amount of padding
     * @return a padding attribute
     * 
     * @see <a href="https://www.graphviz.org/docs/attrs/pad/">dpi</a>
     */
    public static Attributes<ForGraph> pad(double pad) {
        return new SingleAttributes<>("pad", pad);
    }

    /**
     * Specifies the padding around a graph.
     * 
     * @param padX amount of horizontal padding
     * @param padY amount of vertical padding
     * @return a padding attribute
     * 
     * @see <a href="https://www.graphviz.org/docs/attrs/pad/">dpi</a>
     */
    public static Attributes<ForGraph> pad(double padX, double padY) {
        return new SingleAttributes<>("pad", padX + "," + padY);
    }

    /**
     * Sets the margin around a graph.
     * 
     * @param margin the margin to use, in inches
     * @return a margin attribute
     * 
     * @see <a href="https://www.graphviz.org/docs/attrs/margin/">dpi</a>
     */
    public static Attributes<ForGraph> margin(double margin) {
        return new SingleAttributes<>("margin", margin);
    }

    /**
     * Sets the margin around a graph.
     * 
     * @param marginX the horizontal margin to use, in inches
     * @param marginY the vertical margin to use, in inches
     * @return a margin attribute
     * 
     * @see <a href="https://www.graphviz.org/docs/attrs/margin/">dpi</a>
     */
    public static Attributes<ForGraph> margin(double marginX, double marginY) {
        return new SingleAttributes<>("margin", marginX + "," + marginY);
    }
}
