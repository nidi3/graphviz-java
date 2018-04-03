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

public class CodeCoverage {
    @Test
    public void coverage() {
        //TODO 75,75,75 should be the goal
        final JacocoAnalyzer analyzer = new JacocoAnalyzer(new CoverageCollector(BRANCH, LINE, METHOD)
                .just(For.global().setMinima(0, 0, 0)) //TODO remove
//                .just(For.global().setMinima(60, 75, 75))
                .just(For.allPackages().setMinima(75, 75, 75))
                .just(For.thePackage("*.attribute").setMinima(70, 75, 75))
                .just(For.thePackage("*.engine").setMinima(30, 70, 70))
                .just(For.thePackage("*.model").setMinima(60, 75, 75))
                .just(For.thePackage("*.service").setMinima(40, 65, 75))
                .just(For.thePackage("com.kitfox.svg*").setMinima(0, 0, 0)) //TODO remove
        );
        assertThat("Enough code coverage", analyzer.analyze(), hasEnoughCoverage());
    }
}
