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

import edu.umd.cs.findbugs.Priorities;
import guru.nidi.codeassert.checkstyle.*;
import guru.nidi.codeassert.config.AnalyzerConfig;
import guru.nidi.codeassert.config.In;
import guru.nidi.codeassert.dependency.*;
import guru.nidi.codeassert.findbugs.*;
import guru.nidi.codeassert.junit.CodeAssertJunit5Test;
import guru.nidi.codeassert.model.Model;
import guru.nidi.codeassert.pmd.*;
import guru.nidi.graphviz.rough.FillStyle;
import guru.nidi.graphviz.rough.FillStyleTest;
import net.sourceforge.pmd.RulePriority;
import org.junit.jupiter.api.Test;

import static guru.nidi.codeassert.junit.CodeAssertMatchers.matchesRulesExactly;
import static org.hamcrest.MatcherAssert.assertThat;

class CodeAnalysisTest extends CodeAssertJunit5Test {

    @Test
    void dependencies() {
        assertThat(dependencyResult(), matchesRulesExactly());
    }

    @Override
    protected DependencyResult analyzeDependencies() {
        class GuruNidiGraphviz extends DependencyRuler {
            DependencyRule engine, rough;

            public void defineRules() {
                rough.mayUse(engine);
            }
        }
        final DependencyRules rules = DependencyRules.denyAll()
                .withExternals("java.*", "javax.*", "com.*", "org.*")
                .withRelativeRules(new GuruNidiGraphviz());
        return new DependencyAnalyzer(AnalyzerConfig.maven().main()).rules(rules).analyze();
    }

    @Override
    protected Model createModel() {
        return Model.from(AnalyzerConfig.maven().main().getClasses()).read();
    }

    @Override
    protected FindBugsResult analyzeFindBugs() {
        final BugCollector collector = new BugCollector().minPriority(Priorities.NORMAL_PRIORITY)
                .apply(FindBugsConfigs.dependencyTestIgnore(CodeAnalysisTest.class));
        return new FindBugsAnalyzer(AnalyzerConfig.maven().mainAndTest(), collector).analyze();
    }

    @Override
    protected PmdResult analyzePmd() {
        final PmdViolationCollector collector = new PmdViolationCollector().minPriority(RulePriority.MEDIUM)
                .apply(PmdConfigs.minimalPmdIgnore())
                .because("It's a ok", In.clazz(FillStyleTest.class).ignore("JUnitTestContainsTooManyAsserts"))
                .because("It's a bug?", In.clazz(FillStyle.class).ignore("AccessorMethodGeneration"));
        return new PmdAnalyzer(AnalyzerConfig.maven().mainAndTest(), collector)
                .withRulesets(PmdConfigs.defaultPmdRulesets())
                .analyze();
    }

    @Override
    protected CpdResult analyzeCpd() {
        final CpdMatchCollector collector = new CpdMatchCollector()
                .apply(PmdConfigs.cpdIgnoreEqualsHashCodeToString());
        return new CpdAnalyzer(AnalyzerConfig.maven().main(), 36, collector).analyze();
    }

    @Override
    protected CheckstyleResult analyzeCheckstyle() {
        final StyleEventCollector collector = new StyleEventCollector()
                .apply(CheckstyleConfigs.minimalCheckstyleIgnore())
                .because("I don't agree", In.clazz(FillStyle.class).ignore("empty.line.separator"));
        final StyleChecks checks = CheckstyleConfigs.adjustedGoogleStyleChecks();
        return new CheckstyleAnalyzer(AnalyzerConfig.maven().main(), checks, collector).analyze();
    }
}
