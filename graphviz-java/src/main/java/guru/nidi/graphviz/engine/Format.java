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
package guru.nidi.graphviz.engine;

import guru.nidi.graphviz.attribute.validate.ValidatorFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static guru.nidi.graphviz.engine.StringFunctions.replaceRegex;
import static guru.nidi.graphviz.engine.StringFunctions.replaceSubSpaces;
import static java.util.regex.Pattern.DOTALL;

public enum Format {
    PNG("svg", "png", true, true) {
        @Override
        ValidatorFormat forValidator() {
            return ValidatorFormat.SVG;
        }
    },

    SVG("svg", "svg", false, true) {
        @Override
        ValidatorFormat forValidator() {
            return ValidatorFormat.SVG;
        }
    },

    SVG_STANDALONE("svg", "svg", false, true) {
        @Override
        ValidatorFormat forValidator() {
            return ValidatorFormat.SVG;
        }

    },
    DOT("dot", "dot"),
    XDOT("xdot", "xdot") {
        @Override
        ValidatorFormat forValidator() {
            return ValidatorFormat.XDOT;
        }

    },
    PLAIN("plain", "txt"),
    PLAIN_EXT("plain-ext", "txt"),
    PS("ps", "ps") {
        @Override
        ValidatorFormat forValidator() {
            return ValidatorFormat.POSTSCRIPT;
        }
    },
    PS2("ps2", "ps") {
        @Override
        ValidatorFormat forValidator() {
            return ValidatorFormat.POSTSCRIPT;
        }
    },
    JSON("json", "json"),
    JSON0("json0", "json"),
    IMAP("imap", "imap") {
        @Override
        ValidatorFormat forValidator() {
            return ValidatorFormat.MAP; //TODO correct?
        }
    },
    CMAPX("cmapx", "cmapx") {
        @Override
        ValidatorFormat forValidator() {
            return ValidatorFormat.CMAP; //TODO correct?
        }
    };

    final String vizName;
    public final String fileExtension;
    final boolean image;
    final boolean svg;

    Format(String vizName, String fileExtension) {
        this(vizName, fileExtension, false, false);
    }

    Format(String vizName, String fileExtension, boolean image, boolean svg) {
        this.vizName = vizName;
        this.fileExtension = fileExtension;
        this.image = image;
        this.svg = svg;
    }

    ValidatorFormat forValidator() {
        return ValidatorFormat.OTHER;
    }
}
