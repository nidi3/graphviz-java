package guru.nidi.graphviz

import guru.nidi.codeassert.checkstyle.CheckstyleAnalyzer
import guru.nidi.codeassert.checkstyle.StyleEventCollector
import guru.nidi.codeassert.config.AnalyzerConfig
import guru.nidi.codeassert.config.CollectorConfig.just
import guru.nidi.codeassert.config.In
import guru.nidi.codeassert.config.Language.KOTLIN
import guru.nidi.codeassert.dependency.*
import guru.nidi.codeassert.findbugs.BugCollector
import guru.nidi.codeassert.findbugs.FindBugsAnalyzer
import guru.nidi.codeassert.junit.CodeAssertJunit5Test
import guru.nidi.codeassert.junit.PredefConfig
import guru.nidi.codeassert.junit.kotlin.KotlinCodeAssertMatchers.hasNoKtlintIssues
import guru.nidi.codeassert.ktlint.KtlintAnalyzer
import guru.nidi.codeassert.ktlint.KtlintCollector
import guru.nidi.codeassert.pmd.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

class CodeAnalysisTest : CodeAssertJunit5Test() {
    private val config = AnalyzerConfig.maven(KOTLIN).main()!!

    override fun analyzeDependencies(): DependencyResult {
        return DependencyAnalyzer(config)
                .rules(DependencyRules.denyAll()
                        .withExternals("java.*", "kotlin.*", "org.*", "jdk.*", "guru.nidi.codeassert.*")
                        .withRelativeRules(object : DependencyRuler() {
                            val attribute = rule()
                            val model = rule()

                            override fun defineRules() {
                                model.mayUse(attribute)
                            }
                        }))
                .analyze()
    }

    override fun analyzeCheckstyle() = CheckstyleAnalyzer(config, PredefConfig.adjustedGoogleStyleChecks(), StyleEventCollector()
            .apply(PredefConfig.minimalCheckstyleIgnore()))
            .analyze()!!

    override fun analyzeCpd() = CpdAnalyzer(config, 25, CpdMatchCollector()).analyze()!!

    override fun analyzePmd() = PmdAnalyzer(config, PmdViolationCollector()
            .apply(PredefConfig.minimalPmdIgnore()))
            .withRulesets(*PredefConfig.defaultPmdRulesets())
            .analyze()!!

    override fun analyzeFindBugs() = FindBugsAnalyzer(config, BugCollector()
            .apply(PredefConfig.minimalFindBugsIgnore())
            .config(just(In.classes("Kraphviz").withMethods("graph").ignore("BC_UNCONFIRMED_CAST_OF_RETURN_VALUE"))))
            .analyze()!!

    @Test
    fun ktlint() {
        val result = KtlintAnalyzer(config, KtlintCollector()
                .because("I think it's ok", In.everywhere().ignore("no-wildcard-imports")))
                .analyze()
        assertThat(result, hasNoKtlintIssues())
    }
}
