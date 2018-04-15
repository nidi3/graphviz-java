package guru.nidi.graphviz

import guru.nidi.codeassert.config.For
import guru.nidi.codeassert.jacoco.CoverageCollector
import guru.nidi.codeassert.jacoco.CoverageType.*
import guru.nidi.codeassert.jacoco.JacocoAnalyzer
import guru.nidi.codeassert.junit.CodeAssertMatchers.hasEnoughCoverage
import jdk.nashorn.internal.ir.annotations.Ignore
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

class CodeCoverage {
    @Test
    @Ignore
    fun coverage() {
        //TODO 75,75,75 should be the goal
        val analyzer = JacocoAnalyzer(CoverageCollector(BRANCH, LINE, METHOD)
                .just(For.global().setMinima(35, 70, 70))
                .just(For.allPackages().setMinima(50, 50, 50))
        )
        assertThat(analyzer.analyze(), hasEnoughCoverage())
    }
}
