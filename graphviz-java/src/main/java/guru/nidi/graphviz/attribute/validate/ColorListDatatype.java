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

import static guru.nidi.graphviz.attribute.validate.Datatypes.COLOR;
import static guru.nidi.graphviz.attribute.validate.ValidatorMessage.Severity.ERROR;

class ColorListDatatype extends Datatype {
    ColorListDatatype() {
        super("list of colors");
    }

    @Override
    ValidatorMessage validate(Object value) {
        final String[] colors = value.toString().split(":");
        final double[] sum = new double[]{0};
        for (final String color : colors) {
            final int pos = color.indexOf(';');
            final ValidatorMessage colorMsg = COLOR.validate(pos < 0 ? color : color.substring(0, pos));
            if (colorMsg != null) {
                return colorMsg;
            }
            final String factorMsg = checkFactor(pos, color, sum);
            if (factorMsg != null) {
                return new ValidatorMessage(ERROR, factorMsg + " in '" + value + "'.");
            }
        }
        if (sum[0] > 1) {
            return new ValidatorMessage(ERROR, "has a sum of factors " + sum[0] + " > 1 in '" + value + "'.");
        }
        return null;
    }

    private String checkFactor(int pos, String color, double[] sum) {
        if (pos >= 0) {
            if (pos == color.length() - 1) {
                return "is missing color factor after ';'";
            }
            final String factor = color.substring(pos + 1);
            final Double factorValue = doubleValue(factor);
            if (factorValue == null) {
                return "has the invalid color factor '" + factor + "'";
            }
            if (factorValue < 0 || factorValue > 1) {
                return "has a color factor '" + factor + "' not between 0 and 1";
            }
            sum[0] += factorValue;
        }
        return null;
    }
}
