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
package guru.nidi.graphviz

import guru.nidi.codeassert.config.For
import guru.nidi.codeassert.jacoco.CoverageCollector
import guru.nidi.codeassert.jacoco.CoverageType.*
import guru.nidi.codeassert.jacoco.JacocoAnalyzer
import guru.nidi.codeassert.junit.CodeAssertMatchers.hasEnoughCoverage
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

class CodeCoverage {
    @Test
    fun coverage() {
        //TODO 75,75,75 should be the goal
        val analyzer = JacocoAnalyzer(CoverageCollector(BRANCH, LINE, METHOD)
                .just(For.global().setMinima(70, 90, 80))
                .just(For.allPackages().setMinima(70, 90, 80))
        )
        assertThat(analyzer.analyze(), hasEnoughCoverage())
    }
}
