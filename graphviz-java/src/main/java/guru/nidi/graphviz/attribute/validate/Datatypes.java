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

import static guru.nidi.graphviz.attribute.validate.ValidatorMessage.Severity.ERROR;
import static guru.nidi.graphviz.attribute.validate.ValidatorMessage.Severity.WARN;

//TODO finish
final class Datatypes {
    static final Datatype INT = new PatternDatatype("integer", "%d");
    static final Datatype DOUBLE = new PatternDatatype("float", "%f");
    static final Datatype DOUBLE_LIST = new PatternDatatype("list of floats", "%f(:%f)*");
    static final Datatype BOOL = new Datatype("boolean") {
        @Override
        ValidatorMessage validate(Object value) {
            return boolValue(value.toString()) == null
                    ? new ValidatorMessage(ERROR, "has the invalid boolean value '" + value + "'.")
                    : matches(value, "%d")
                    ? new ValidatorMessage(WARN, "uses the numerical value '" + value + "' as boolean.")
                    : null;
        }
    };
    static final Datatype STRING = new AnyDatatype("string");
    static final Datatype ESC_STRING = new AnyDatatype("escaped string");
    static final Datatype LBL_STRING = new AnyDatatype("label string");
    static final Datatype POINT = new PatternDatatype("point", "%p"); //TODO depends on dim
    static final Datatype POINT_LIST = new PatternDatatype("list of points", "%p( %p)*");
    static final Datatype ADD_POINT = new PatternDatatype("add point", "\\+?%p");
    static final Datatype ADD_DOUBLE = new PatternDatatype("add float", "%f");
    static final Datatype PORT_POS = new PatternDatatype("port position", "[^:]+|%c|[^:]+:%c");
    static final Datatype LAYER_RANGE = new Datatype("layer range") { //TODO
        @Override
        ValidatorMessage validate(Object value) {
            return null;
        }
    };
    static final Datatype LAYER_LIST = new Datatype("list of layers") { //TODO
        @Override
        ValidatorMessage validate(Object value) {
            return null;
        }
    };
    static final Datatype ARROW_TYPE = new ArrowDatatype();
    static final Datatype DIR_TYPE = new PatternDatatype("direction", "forward|back|both|none");
    static final Datatype RECT = new PatternDatatype("rect", "%f,%f,%f,%f");
    static final Datatype CLUSTER_MODE = new PatternDatatype("cluster mode", "local|global|none");
    static final Datatype OUTPUT_MODE = new PatternDatatype("output mode", "breadthfirst|nodesfirst|edgesfirst");
    static final Datatype PACK_MODE = new PatternDatatype("pack mode", "node|clust|graph|array[ctblru]*%d?");
    static final Datatype PAGE_DIR = new PatternDatatype("page direction", "BL|BR|TL|TR|RB|RT|LB|LT");
    static final Datatype RANK_TYPE = new PatternDatatype("rank type", "same|min|source|max|sink");
    static final Datatype RANK_DIR = new PatternDatatype("rank direction", "TB|LR|BT|RL");
    static final Datatype SPLINE_TYPE = new PatternDatatype("spline", "%s(;%s)*");
    static final Datatype QUAD_TYPE = new PatternDatatype("quad type", "normal|fast|none");
    static final Datatype SMOOTH_TYPE = new PatternDatatype("smooth type",
            "none|avg_dist|graph_dist|power_dist|rng|spring|triangle");
    static final Datatype START_TYPE = new PatternDatatype("start type", "(regular|self|random)?.*");
    static final Datatype STYLE = new StyleDatatype();
    static final Datatype SHAPE = new ShapeDatatype();
    static final Datatype VIEW_PORT = new PatternDatatype("view port", "%f,%f(,%f(,(%f,%f|'.*'))?)?");
    static final Datatype COLOR = new ColorDatatype();
    static final Datatype COLOR_LIST = new ColorListDatatype();

    private Datatypes() {
    }
}
