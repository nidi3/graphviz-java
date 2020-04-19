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
package guru.nidi.graphviz;

import guru.nidi.codeassert.config.For;
import guru.nidi.codeassert.jacoco.CoverageCollector;
import guru.nidi.codeassert.jacoco.JacocoAnalyzer;
import org.junit.jupiter.api.Test;

import static guru.nidi.codeassert.jacoco.CoverageType.*;
import static guru.nidi.codeassert.junit.CodeAssertMatchers.hasEnoughCoverage;
import static org.hamcrest.MatcherAssert.assertThat;

class CodeCoverage {
    @Test
    void coverage() {
        //TODO 75,75,75 should be the goal
        final JacocoAnalyzer analyzer = new JacocoAnalyzer(new CoverageCollector(BRANCH, LINE, METHOD)
                .just(For.allPackages().setMinima(75, 75, 75))
                .just(For.thePackage("*.attribute").setMinima(65, 75, 75))
                .just(For.thePackage("*.engine").setMinima(55, 60, 70))
                .just(For.thePackage("*.service").setMinima(45, 75, 75))
                .just(For.thePackage("*.use").setMinima(0, 0, 0))
        );
        assertThat("Enough code coverage", analyzer.analyze(), hasEnoughCoverage());
    }
}
