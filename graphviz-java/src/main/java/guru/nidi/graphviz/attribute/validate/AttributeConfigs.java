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
package guru.nidi.graphviz.attribute.validate;

import java.util.*;

import static guru.nidi.graphviz.attribute.validate.AttributeConfig.entry;
import static guru.nidi.graphviz.attribute.validate.Datatypes.*;
import static guru.nidi.graphviz.attribute.validate.ValidatorEngine.*;
import static guru.nidi.graphviz.attribute.validate.ValidatorFormat.*;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

final class AttributeConfigs {
    private static final double EPSILON = 1e-8;
    private static final Map<String, List<AttributeConfig>> CONFIG = new HashMap<>();

    private AttributeConfigs() {
    }

    static List<AttributeConfig> get(String name) {
        return CONFIG.get(name);
    }

    static {
        add("Damping", entry("G", DOUBLE, 0.99, 0.0).engines(NEATO));
        add("K", entry("GC", DOUBLE, 0.3, 0.0).engines(SFDP, FDP));
        add("URL", entry("ENGC", ESC_STRING).formats(SVG, POSTSCRIPT, MAP));
        add("_background", entry("G", STRING));
        add("area", entry("NC", DOUBLE, 1.0, EPSILON).engines(PATCHWORK));
        add("arrowhead", entry("E", ARROW_TYPE, "normal"));
        add("arrowsize", entry("E", DOUBLE, 1.0, 0.0));
        add("arrowtail", entry("E", ARROW_TYPE, "normal"));
        add("bb", entry("G", RECT).formats(WRITE));
        add("bgcolor", entry("GC", asList(COLOR, COLOR_LIST)));
        add("center", entry("G", BOOL, false));
        add("charset", entry("G", STRING, "UTF-8"));
        add("class", entry("ENGC", STRING));
        add("clusterrank", entry("G", CLUSTER_MODE, "local").engines(DOT));
        add("color", entry("ENC", asList(COLOR, COLOR_LIST), "black"));
        add("colorscheme", entry("ENCG", STRING, ""));
        add("comment", entry("ENG", STRING, ""));
        add("compound", entry("G", BOOL, false).engines(DOT));
        add("concentrate", entry("G", BOOL, false));
        add("constraint", entry("E", BOOL, true).engines(DOT));
        add("decorate", entry("E", BOOL, false));
        add("defaultdist", entry("G", DOUBLE, "1+(avg. len)*sqrt(|V|)", EPSILON).engines(NEATO)); //TODO
        add("dim", entry("G", INT, 2, 2.0).engines(SFDP, FDP, NEATO));
        add("dimen", entry("G", INT, 2, 2.0).engines(SFDP, FDP, NEATO));
        add("dir", entry("E", DIR_TYPE, "forward(directed)<BR>none(undirected)")); //TODO
        add("diredgeconstraints", entry("G", asList(STRING, BOOL), false).engines(NEATO));
        add("distortion", entry("N", DOUBLE, 0.0, -100.0));
        add("dpi", entry("G", DOUBLE, 72.0).formats(SVG, BITMAP));
        add("edgeURL", entry("E", ESC_STRING, "").formats(SVG, MAP));
        add("edgehref", entry("E", ESC_STRING, "").formats(SVG, MAP));
        add("edgetarget", entry("E", ESC_STRING).formats(SVG, MAP));
        add("edgetooltip", entry("E", ESC_STRING, "").formats(SVG, CMAP));
        add("epsilon", entry("G", DOUBLE, ".0001 * # nodes(mode == KK)<BR>.0001(mode == major)").engines(NEATO)); //TODO
        add("esep", entry("G", asList(ADD_DOUBLE, ADD_POINT), "+3").engines(NOT_DOT)); //TODO
        add("fillcolor", entry("NEC", asList(COLOR, COLOR_LIST), "lightgrey(nodes)<BR>black(clusters")); //TODO
        add("fixedsize", entry("N", asList(BOOL, STRING), false));
        add("fontcolor", entry("ENGC", COLOR, "black"));
        add("fontname", entry("ENGC", STRING, "Times-Roman")); //TODO
        add("fontnames", entry("G", STRING, "").formats(SVG));
        add("fontpath", entry("G", STRING, "system-dependent")); //TODO
        add("fontsize", entry("ENGC", DOUBLE, 14.0, 1.0));
        add("forcelabels", entry("G", BOOL, true));
        add("gradientangle", entry("NCG", INT, 0));
        add("group", entry("N", STRING, "").engines(DOT));
        add("headURL", entry("E", ESC_STRING, "").formats(SVG, MAP));
        add("head_lp", entry("E", POINT).formats(WRITE));
        add("headclip", entry("E", BOOL, true));
        add("headhref", entry("E", ESC_STRING, "").formats(SVG, MAP));
        add("headlabel", entry("E", LBL_STRING, ""));
        add("headport", entry("E", PORT_POS, "center"));
        add("headtarget", entry("E", ESC_STRING).formats(SVG, MAP));
        add("headtooltip", entry("E", ESC_STRING, "").formats(SVG, CMAP));
        add("height", entry("N", DOUBLE, 0.5, 0.02));
        add("href", entry("GCNE", ESC_STRING, "").formats(SVG, POSTSCRIPT, MAP));
        add("id", entry("GCNE", ESC_STRING, "").formats(SVG, POSTSCRIPT, MAP));
        add("image", entry("N", STRING, ""));
        add("imagepath", entry("G", STRING, ""));
        add("imagepos", entry("N", STRING, "mc"));
        add("imagescale", entry("N", BOOL, false));
        add("inputscale", entry("G", DOUBLE).engines(FDP, NEATO));
        add("label", entry("ENGC", LBL_STRING, "\\N(nodes)<BR>''(otherwise)")); //TODO
        add("labelURL", entry("E", ESC_STRING, "").formats(SVG, MAP));
        add("label_scheme", entry("G", INT, 0, 0.0).engines(SFDP));
        add("labelangle", entry("E", DOUBLE, -25.0, -180.0));
        add("labeldistance", entry("E", DOUBLE, 1.0, 0.0));
        add("labelfloat", entry("E", BOOL, false));
        add("labelfontcolor", entry("E", COLOR, "black"));
        add("labelfontname", entry("E", STRING, "Times-Roman")); //TODO
        add("labelfontsize", entry("E", DOUBLE, 14.0, 1.0));
        add("labelhref", entry("E", ESC_STRING, "").formats(SVG, MAP));
        add("labeljust", entry("GC", STRING, "c"));
        add("labelloc", entry("NGC", STRING, "'t'(clusters)<BR>'b'(root graphs)<BR>'c'(nodes)")); //TODO
        add("labeltarget", entry("E", ESC_STRING).formats(SVG, MAP));
        add("labeltooltip", entry("E", ESC_STRING, "").formats(SVG, CMAP));
        add("landscape", entry("G", BOOL, false));
        add("layer", entry("ENC", LAYER_RANGE, ""));
        add("layerlistsep", entry("G", STRING, ","));
        add("layers", entry("G", LAYER_LIST, ""));
        add("layerselect", entry("G", LAYER_RANGE, ""));
        add("layersep", entry("G", STRING, ":\\t")); //TODO
        add("layout", entry("G", STRING, ""));
        add("len", entry("E", DOUBLE, "1.0(neato)<BR>0.3(fdp)").engines(FDP, NEATO)); //TODO
        add("levels", entry("G", INT, Integer.MAX_VALUE, 0.0).engines(SFDP));
        add("levelsgap", entry("G", DOUBLE, 0.0).engines(NEATO));
        add("lhead", entry("E", STRING, "").engines(DOT));
        add("lheight", entry("GC", DOUBLE).formats(WRITE));
        add("lp", entry("EGC", POINT).formats(WRITE));
        add("ltail", entry("E", STRING, "").engines(DOT));
        add("lwidth", entry("GC", DOUBLE).formats(WRITE));
        add("margin", entry("NCG", asList(DOUBLE, POINT), "&#60;device-dependent&#62;")); //TODO
        add("maxiter", entry("G", INT, "100 nodes(mode == KK)<BR>200(mode == major)<BR>600(fdp)").engines(FDP, NEATO));
        //TODO
        add("mclimit", entry("G", DOUBLE, 1.0).engines(DOT));
        add("mindist", entry("G", DOUBLE, 1.0, 0.0).engines(CIRCO));
        add("minlen", entry("E", INT, 1, 0.0).engines(DOT));
        add("mode", entry("G", STRING, "major").engines(NEATO));
        add("model", entry("G", STRING, "shortpath").engines(NEATO));
        add("mosek", entry("G", BOOL, false).engines(NEATO));
        add("newrank", entry("G", BOOL, false).engines(DOT));
        add("nodesep", entry("G", DOUBLE, 0.25, 0.02));
        add("nojustify", entry("GCNE", BOOL, false));
        add("normalize", entry("G", asList(DOUBLE, BOOL), false).engines(NOT_DOT));
        add("notranslate", entry("G", BOOL, false).engines(NEATO));
        add("nslimit", entry("G", DOUBLE).engines(DOT));
        add("nslimit1", entry("G", DOUBLE).engines(DOT));
        add("ordering", entry("GN", STRING, "").engines(DOT));
        add("orientation",
                entry("N", DOUBLE, 0.0, 0.0, 360.0),
                entry("G", STRING, ""));
        add("outputorder", entry("G", OUTPUT_MODE, "breadthfirst"));
        add("overlap", entry("G", asList(STRING, BOOL), true).engines(NOT_DOT));
        add("overlap_scaling", entry("G", DOUBLE, -4.0, -1.0e10)); //TODO prism only
        add("overlap_shrink", entry("G", BOOL, true)); //TODO prism only
        add("pack", entry("G", asList(BOOL, INT), false));
        add("packmode", entry("G", PACK_MODE, "node"));
        add("pad", entry("G", asList(DOUBLE, POINT), 0.0555));
        add("page", entry("G", asList(DOUBLE, POINT)));
        add("pagedir", entry("G", PAGE_DIR, "BL"));
        add("pencolor", entry("C", COLOR, "black"));
        add("penwidth", entry("CNE", DOUBLE, 1.0, 0.0));
        add("peripheries", entry("NC", INT, "shape default(nodes)<BR>1(clusters)", 0.0)); //TODO
        add("pin", entry("N", BOOL, false).engines(FDP, NEATO));
        add("pos", entry("EN", asList(POINT, SPLINE_TYPE)));
        add("quadtree", entry("G", asList(QUAD_TYPE, BOOL), "normal").engines(SFDP));
        add("quantum", entry("G", DOUBLE, 0.0, 0.0));
        add("rank", entry("S", RANK_TYPE).engines(DOT));
        add("rankdir", entry("G", RANK_DIR, "TB").engines(DOT));
        add("ranksep", entry("G", asList(DOUBLE, DOUBLE_LIST), "0.5(dot)<BR>1.0(twopi)", 0.02).engines(TWOPI, DOT));
        //TODO
        add("ratio", entry("G", asList(DOUBLE, STRING)));
        add("rects", entry("N", RECT).formats(WRITE));
        add("regular", entry("N", BOOL, false));
        add("remincross", entry("G", BOOL, true).engines(DOT));
        add("repulsiveforce", entry("G", DOUBLE, 1.0, 0.0).engines(SFDP));
        add("resolution", entry("G", DOUBLE, 72.0).formats(SVG, BITMAP));
        add("root", entry("GN", asList(STRING, BOOL), "&#60;none&#62;(graphs)<BR>false(nodes)").engines(CIRCO, TWOPI));
        //TODO
        add("rotate", entry("G", INT, 0));
        add("rotation", entry("G", DOUBLE, 0).engines(SFDP));
        add("samehead", entry("E", STRING, "").engines(DOT));
        add("sametail", entry("E", STRING, "").engines(DOT));
        add("samplepoints", entry("N", INT, "8(output)<BR>20(overlap and image maps)")); //TODO
        add("scale", entry("G", asList(DOUBLE, POINT)).engines(NOT_DOT));
        add("searchsize", entry("G", INT, 30).engines(DOT));
        add("sep", entry("G", asList(ADD_DOUBLE, ADD_POINT), "+4").engines(NOT_DOT)); //TODO
        add("shape", entry("N", SHAPE, "ellipse"));
        add("shapefile", entry("N", STRING, ""));
        add("showboxes", entry("ENG", INT, 0, 0.0).engines(DOT));
        add("sides", entry("N", INT, 4, 0.0));
        add("size", entry("G", asList(DOUBLE, POINT)));
        add("skew", entry("N", DOUBLE, 0.0, -100.0));
        add("smoothing", entry("G", SMOOTH_TYPE, "none").engines(SFDP));
        add("sortv", entry("GCN", INT, 0, 0.0));
        add("splines", entry("G", asList(BOOL, STRING)));
        add("start", entry("G", START_TYPE, "").engines(FDP, NEATO));
        add("style", entry("ENCG", STYLE, ""));
        add("stylesheet", entry("G", STRING, "").formats(SVG));
        add("tailURL", entry("E", ESC_STRING, "").formats(SVG, MAP));
        add("tail_lp", entry("E", POINT).formats(WRITE));
        add("tailclip", entry("E", BOOL, true));
        add("tailhref", entry("E", ESC_STRING, "").formats(SVG, MAP));
        add("taillabel", entry("E", LBL_STRING, ""));
        add("tailport", entry("E", PORT_POS, "center"));
        add("tailtarget", entry("E", ESC_STRING).formats(SVG, MAP));
        add("tailtooltip", entry("E", ESC_STRING, "").formats(SVG, CMAP));
        add("target", entry("ENGC", asList(ESC_STRING, STRING)).formats(SVG, MAP));
        add("tooltip", entry("NEC", ESC_STRING, "").formats(SVG, CMAP));
        add("truecolor", entry("G", BOOL).formats(BITMAP));
        add("vertices", entry("N", POINT_LIST).formats(WRITE));
        add("viewport", entry("G", VIEW_PORT, ""));
        add("voro_margin", entry("G", DOUBLE, 0.05, 0.0).engines(NOT_DOT));
        add("weight",
                entry("E", asList(INT, DOUBLE), 1, 0.0).engines(DOT, TWOPI),
                entry("E", asList(INT, DOUBLE), 1, 1.0).engines(NEATO, FDP));
        add("width", entry("N", DOUBLE, 0.75, 0.01));
        add("xdotversion", entry("G", STRING).formats(XDOT));
        add("xlabel", entry("EN", LBL_STRING, ""));
        add("xlp", entry("NE", POINT).formats(WRITE));
        add("z", entry("N", DOUBLE, 0.0, -1000.0));
    }

    private static void add(String name, AttributeConfig entry) {
        CONFIG.put(name, singletonList(entry));
    }

    private static void add(String name, AttributeConfig... entries) {
        CONFIG.put(name, asList(entries));
    }
}
