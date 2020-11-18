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

import java.lang.reflect.Field;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

abstract class Datatype {
    final String name;

    Datatype(String name) {
        this.name = name;
    }

    abstract ValidatorMessage validate(Object value);

    static Pattern pattern(String p) {
        return Pattern.compile(p
                .replace("%s", "(e,%f,%f)?(s,%f,%f)?%p(%p %p %p)+")
                .replace("%p", "(%f,%f(,%f)?!?)")
                .replace("%c", "(n|ne|e|se|s|sw|w|nw|c|_)")
                .replace("%x", "([0-9A-Fa-f])")
                .replace("%n", "(1\\.0|1|0|0?\\.[0-9]+)")
                .replace("%d", "([+-]?[0-9]{1,9})")
                .replace("%f", "([+-]?(\\d+([.]\\d*)?(e[+-]?\\d+)?|[.]\\d+(e[+-]?\\d+)?))"));
    }

    static boolean matches(Object value, String pattern) {
        return pattern(pattern).matcher(value.toString()).matches();
    }

    static Double doubleValue(Object value) {
        return value instanceof Double ? (Double) value : tryParseDouble(value.toString());
    }

    static Integer intValue(Object value) {
        return value instanceof Integer ? (Integer) value : tryParseInt(value.toString());
    }

    static Boolean boolValue(Object value) {
        return value instanceof Boolean ? (Boolean) value : boolValue(value.toString(), tryParseInt(value.toString()));
    }

    static Boolean boolValue(String val, Integer i) {
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

    static Set<String> fieldNames(Class<?> clazz) {
        final Field valueField = silently(() -> {
            try {
                return clazz.getDeclaredField("value");
            } catch (ReflectiveOperationException e) {
                return clazz.getSuperclass().getDeclaredField("value");
            }
        });
        valueField.setAccessible(true);
        return Stream.of(clazz.getFields())
                .filter(f -> f.getType() == clazz)
                .map(f -> silently(() -> (String) valueField.get(f.get(null))))
                .collect(toSet());
    }

    private static <T> T silently(Callable<T> s) {
        try {
            return s.call();
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

}
