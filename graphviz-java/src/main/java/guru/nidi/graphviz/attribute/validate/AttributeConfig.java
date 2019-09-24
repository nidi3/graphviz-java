package guru.nidi.graphviz.attribute.validate;

import guru.nidi.graphviz.attribute.validate.AttributeValidator.Scope;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

import static guru.nidi.graphviz.attribute.validate.AttributeValidator.Scope.*;
import static java.util.Collections.singletonList;

class AttributeConfig {
    enum Engine {
        CIRCO, NOT_DOT, DOT, NEATO, OSAGE, TWOPI, FDP, SFDP, PATCHWORK
    }

    enum Format {
        WRITE, SVG, BITMAP, MAP, CMAP, POSTSCRIPT, XDOT
    }

    final List<Datatype> types;
    @Nullable
    final Object defaultVal;
    @Nullable
    final Double minimum;
    final EnumSet<Engine> engines;
    final EnumSet<Format> formats;
    final EnumSet<Scope> scopes;

    private AttributeConfig(@Nullable EnumSet<Scope> scopes, List<Datatype> types, @Nullable Object defaultVal, @Nullable Double minimum,
                            @Nullable EnumSet<Engine> engines, @Nullable EnumSet<Format> formats) {
        this.scopes = scopes == null ? EnumSet.noneOf(Scope.class) : scopes;
        this.types = types;
        this.defaultVal = defaultVal;
        this.minimum = minimum;
        this.engines = engines == null ? EnumSet.noneOf(Engine.class) : engines;
        this.formats = formats == null ? EnumSet.noneOf(Format.class) : formats;
    }

    static AttributeConfig entry(String scopes, Datatype type) {
        return entry(scopes, type, null);
    }

    static AttributeConfig entry(String scopes, List<Datatype> types) {
        return entry(scopes, types, null);
    }

    static AttributeConfig entry(String scopes, Datatype type, @Nullable Object defaultVal) {
        return entry(scopes, type, defaultVal, null);
    }

    static AttributeConfig entry(String scopes, List<Datatype> types, @Nullable Object defaultVal) {
        return entry(scopes, types, defaultVal, null);
    }

    static AttributeConfig entry(String scopes, Datatype type, @Nullable Object defaultVal, @Nullable Double minimum) {
        return entry(scopes, singletonList(type), defaultVal, minimum);
    }

    static AttributeConfig entry(String scopes, List<Datatype> types, @Nullable Object defaultVal, @Nullable Double minimum) {
        return new AttributeConfig(scopesOf(scopes), types, defaultVal, minimum, null, null);
    }

    AttributeConfig engines(Engine... engines) {
        return new AttributeConfig(scopes, types, defaultVal, minimum, EnumSet.of(engines[0], engines), formats);
    }

    AttributeConfig formats(Format... formats) {
        return new AttributeConfig(scopes, types, defaultVal, minimum, engines, EnumSet.of(formats[0], formats));
    }

    private static EnumSet<Scope> scopesOf(String scopes) {
        final EnumSet<Scope> res = EnumSet.noneOf(Scope.class);
        for (int i = 0; i < scopes.length(); i++) {
            switch (scopes.charAt(i)) {
                case 'G':
                    res.add(GRAPH);
                    break;
                case 'S':
                    res.add(SUB_GRAPH);
                    break;
                case 'C':
                    res.add(CLUSTER);
                    break;
                case 'N':
                    res.add(NODE);
                    break;
                case 'E':
                    res.add(EDGE);
                    break;
                default:
                    throw new IllegalArgumentException("unknown cope '" + scopes.charAt(i) + "'.");
            }
        }
        return res;
    }

}
