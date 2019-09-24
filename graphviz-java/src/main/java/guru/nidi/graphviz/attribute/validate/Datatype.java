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

import java.util.regex.Pattern;

import static guru.nidi.graphviz.attribute.validate.ValidatorMessage.Severity.ERROR;
import static guru.nidi.graphviz.attribute.validate.ValidatorMessage.Severity.WARNING;

class PatternDatatype extends Datatype {
    private final String pattern;

    PatternDatatype(String name, String pattern) {
        super(name);
        this.pattern = pattern;
    }

    @Override
    ValidatorMessage validate(Object value) {
        return matches(value, pattern)
                ? null : new ValidatorMessage(ERROR, "'" + value + "' is not a valid " + name + ".");
    }
}

abstract class Datatype {
    final String name;

    Datatype(String name) {
        this.name = name;
    }

    abstract ValidatorMessage validate(Object value);

    Pattern pattern(String p) {
        return Pattern.compile(p
                .replace("%s", "(e,%f,%f)?(s,%f,%f)?%p(%p %p %p)+")
                .replace("%p", "(%f,%f(,%f)?!?)")
                .replace("%c", "(n|ne|e|se|s|sw|w|nw|c|_)")
                .replace("%x", "([0-9A-Fa-f])")
                .replace("%n", "(1\\.0|1|0|0?\\.[0-9]+)")
                .replace("%d", "([+-]?[0-9]{1,9})")
                .replace("%f", "([+-]?(\\d+([.]\\d*)?(e[+-]?\\d+)?|[.]\\d+(e[+-]?\\d+)?))"));
    }

    boolean matches(Object value, String pattern) {
        return pattern(pattern).matcher(value.toString()).matches();
    }

    static final Datatype INT = new PatternDatatype("integer", "%d");
    static final Datatype DOUBLE = new PatternDatatype("float", "%f");
    static final Datatype DOUBLE_LIST = new PatternDatatype("list of floats", "%f(:%f)*");
    static final Datatype BOOL = new Datatype("boolean") {
        @Override
        ValidatorMessage validate(Object value) {
            return boolValue(value.toString()) == null
                    ? new ValidatorMessage(ERROR, "'" + value + "' is not a valid boolean.")
                    : matches(value, "%d")
                    ? new ValidatorMessage(WARNING, "Using numerical value '" + value + "' as boolean.")
                    : null;
        }
    };
    static final Datatype STRING = new PatternDatatype("string", ".*");
    static final Datatype ESC_STRING = new PatternDatatype("escaped string", ".*");
    static final Datatype LBL_STRING = new PatternDatatype("label string", ".*");
    static final Datatype POINT = new PatternDatatype("point", "%p"); //depends on dim
    static final Datatype POINT_LIST = new PatternDatatype("list of points", "%p( %p)*");
    static final Datatype ADD_POINT = new PatternDatatype("add point", "\\+?%p");
    static final Datatype ADD_DOUBLE = new PatternDatatype("add float", "%f");
    static final Datatype PORT_POS = new PatternDatatype("port position", "[^:]+|%c|[^:]+:%c");
    static final Datatype LAYER_RANGE = new Datatype("layer range") {
        @Override
        ValidatorMessage validate(Object value) {
            return null;
        }
    };
    static final Datatype LAYER_LIST = new Datatype("list of layers") {
        @Override
        ValidatorMessage validate(Object value) {
            return null;
        }
    };
    static final Datatype ARROW_TYPE = new Datatype("arrow") {
        @Override
        ValidatorMessage validate(Object value) {
            return new ArrowTypeValidator(value).validate();
        }
    };
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
    static final Datatype SMOOTH_TYPE = new PatternDatatype("smooth type", "none|avg_dist|graph_dist|power_dist|rng|spring|triangle");
    static final Datatype START_TYPE = new Datatype("start type") {
        @Override
        ValidatorMessage validate(Object value) {
            return null;
        }
    };
    static final Datatype STYLE = new Datatype("style") {
        @Override
        ValidatorMessage validate(Object value) {
            return null;
        }
    };
    static final Datatype SHAPE = new Datatype("shape") {
        @Override
        ValidatorMessage validate(Object value) {
            return null;
        }
    };
    static final Datatype VIEW_PORT = new Datatype("view port") {
        @Override
        ValidatorMessage validate(Object value) {
            return null;
        }
    };
    static final Datatype COLOR = new Datatype("color") {
        @Override
        ValidatorMessage validate(Object value) {
            return matches(value, "#%x%x ?%x%x ?%x%x( ?%x%x)?")
                    || matches(value, "#%n[, ]+%n[, ]+%n")
                    || matches(value, "[^#].*")
                    ? null : new ValidatorMessage(ERROR, "'" + value + "' is not a valid " + name + ".");
        }
    };
    static final Datatype COLOR_LIST = new Datatype("list of colors") {
        @Override
        ValidatorMessage validate(Object value) {
            return null;
        }
    };

    static Double doubleValue(Object value) {
        return value instanceof Double ? (Double) value : tryParseDouble(value.toString());
    }

    static Integer intValue(Object value) {
        return value instanceof Integer ? (Integer) value : tryParseInt(value.toString());
    }

    static Boolean boolValue(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        final String val = value.toString();
        final Integer i = tryParseInt(val);
        if (val.equalsIgnoreCase("true") || val.equalsIgnoreCase("yes") || (i != null && i != 0)) {
            return true;
        }
        if (val.equalsIgnoreCase("false") || val.equalsIgnoreCase("no") || (i != null && i == 0)) {
            return false;
        }
        return null;
    }

    static Double tryParseDouble(String s) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    static Integer tryParseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

}
