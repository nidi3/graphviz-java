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

import guru.nidi.graphviz.attribute.validate.ValidatorEngine;

public enum Engine {
    CIRCO {
        @Override
        ValidatorEngine forValidator() {
            return ValidatorEngine.CIRCO;
        }
    },
    DOT {
        @Override
        ValidatorEngine forValidator() {
            return ValidatorEngine.DOT;
        }
    },
    NEATO {
        @Override
        ValidatorEngine forValidator() {
            return ValidatorEngine.NEATO;
        }
    },
    OSAGE {
        @Override
        ValidatorEngine forValidator() {
            return ValidatorEngine.OSAGE;
        }
    },
    TWOPI {
        @Override
        ValidatorEngine forValidator() {
            return ValidatorEngine.TWOPI;
        }
    },
    FDP {
        @Override
        ValidatorEngine forValidator() {
            return ValidatorEngine.FDP;
        }
    };

    abstract ValidatorEngine forValidator();
}
