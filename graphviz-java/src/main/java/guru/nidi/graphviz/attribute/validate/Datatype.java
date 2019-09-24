package guru.nidi.graphviz.attribute.validate;

import java.util.stream.Stream;

import static guru.nidi.graphviz.attribute.validate.ValidatorMessage.Severity.ERROR;
import static guru.nidi.graphviz.attribute.validate.ValidatorMessage.Severity.WARNING;
import static java.util.Arrays.asList;

abstract class Datatype {
    final String name;

    private Datatype(String name) {
        this.name = name;
    }

    abstract ValidatorMessage validate(Object value);

    static final Datatype INT = new Datatype("integer") {
        @Override
        ValidatorMessage validate(Object value) {
            return intValue(value.toString()) == null
                    ? new ValidatorMessage(ERROR, "'" + value + "' is not a valid integer.") : null;
        }
    };
    static final Datatype DOUBLE = new Datatype("float") {
        @Override
        ValidatorMessage validate(Object value) {
            return doubleValue(value.toString()) == null
                    ? new ValidatorMessage(ERROR, "'" + value + "' is not a valid float.") : null;
        }
    };
    static final Datatype DOUBLE_LIST = new Datatype("list of floats") {
        @Override
        ValidatorMessage validate(Object value) {
            return Stream.of(value.toString().split(":")).anyMatch(s -> doubleValue(s) == null)
                    ? new ValidatorMessage(ERROR, "'" + value + "' is not a valid list of floats.") : null;
        }
    };
    static final Datatype BOOL = new Datatype("boolean") {
        @Override
        ValidatorMessage validate(Object value) {
            return boolValue(value.toString()) == null ? new ValidatorMessage(ERROR, "'" + value + "' is not a valid boolean.")
                    : tryParseInt(value.toString()) == null ? null : new ValidatorMessage(WARNING, "Using numerical value '" + value + "' as boolean.");
        }
    };
    static final Datatype STRING = new Datatype("string") {
        @Override
        ValidatorMessage validate(Object value) {
            return null;
        }
    };
    static final Datatype ESC_STRING = new Datatype("escaped string") {
        @Override
        ValidatorMessage validate(Object value) {
            return null;
        }
    };
    static final Datatype LBL_STRING = new Datatype("label string") {
        @Override
        ValidatorMessage validate(Object value) {
            return null;
        }
    };
    static final Datatype POINT = new Datatype("point") {
        @Override
        ValidatorMessage validate(Object value) {
            final String s = value.toString();
            final String norm = s.endsWith("!") ? s.substring(0, s.length() - 1) : s;
            final String[] parts = norm.split(",");
            return (parts.length != 2 && parts.length != 3) || Stream.of(parts).anyMatch(p -> doubleValue(p) == null)
                    ? new ValidatorMessage(ERROR, "'" + value + "' is not a valid point.") : null;
        }
    };
    static final Datatype POINT_LIST = new Datatype("list of points") {
        @Override
        ValidatorMessage validate(Object value) {
            return Stream.of(value.toString().split("\\s+")).anyMatch(p -> POINT.validate(p) != null)
                    ? new ValidatorMessage(ERROR, "'" + value + "' is not a valid list of points.") : null;
        }
    };
    static final Datatype ADD_POINT = new Datatype("add point") {
        @Override
        ValidatorMessage validate(Object value) {
            final String s = value.toString();
            return POINT.validate(s.startsWith("+") ? s.substring(1) : s) != null
                    ? new ValidatorMessage(ERROR, "'" + value + "' is not a valid add point.") : null;
        }
    };
    static final Datatype ADD_DOUBLE = new Datatype("add float") {
        @Override
        ValidatorMessage validate(Object value) {
            return DOUBLE.validate(value) != null
                    ? new ValidatorMessage(ERROR, "'" + value + "' is not a valid add float.") : null;
        }
    };
    static final Datatype PORT_POS = new Datatype("port position") {
        @Override
        ValidatorMessage validate(Object value) {
            final String[] parts = value.toString().split(":");
            return (parts.length != 1 && parts.length != 2) || (parts.length == 2 && !asList("n", "ne", "e", "se", "s", "sw", "w", "nw", "c", "_").contains(parts[1]))
                    ? new ValidatorMessage(ERROR, "'" + value + "' is not a valid post position.") : null;
        }
    };
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
            return null;
        }
    };
    static final Datatype DIR_TYPE = new Datatype("direction") {
        @Override
        ValidatorMessage validate(Object value) {
            return null;
        }
    };
    static final Datatype RECT = new Datatype("rect") {
        @Override
        ValidatorMessage validate(Object value) {
            return null;
        }
    };
    static final Datatype CLUSTER_MODE = new Datatype("cluster mode") {
        @Override
        ValidatorMessage validate(Object value) {
            return null;
        }
    };
    static final Datatype OUTPUT_MODE = new Datatype("output mode") {
        @Override
        ValidatorMessage validate(Object value) {
            return null;
        }
    };
    static final Datatype PACK_MODE = new Datatype("pack mode") {
        @Override
        ValidatorMessage validate(Object value) {
            return null;
        }
    };
    static final Datatype PAGE_DIR = new Datatype("page direction") {
        @Override
        ValidatorMessage validate(Object value) {
            return null;
        }
    };
    static final Datatype RANK_TYPE = new Datatype("rank type") {
        @Override
        ValidatorMessage validate(Object value) {
            return null;
        }
    };
    static final Datatype RANK_DIR = new Datatype("rank direction") {
        @Override
        ValidatorMessage validate(Object value) {
            return null;
        }
    };
    static final Datatype SPLINE_TYPE = new Datatype("spline") {
        @Override
        ValidatorMessage validate(Object value) {
            return null;
        }
    };
    static final Datatype QUAD_TYPE = new Datatype("quad type") {
        @Override
        ValidatorMessage validate(Object value) {
            return null;
        }
    };
    static final Datatype SMOOTH_TYPE = new Datatype("smooth type") {
        @Override
        ValidatorMessage validate(Object value) {
            return null;
        }
    };
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
            return null;
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
        final String val = value.toString();
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
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
