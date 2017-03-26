/*
 * Copyright (C) 2015 Stefan Niederhauser (nidin@gmx.ch)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package guru.nidi.graphviz;

import edu.umd.cs.findbugs.Priorities;
import guru.nidi.codeassert.config.AnalyzerConfig;
import guru.nidi.codeassert.config.In;
import guru.nidi.codeassert.dependency.DependencyRule;
import guru.nidi.codeassert.dependency.DependencyRuler;
import guru.nidi.codeassert.dependency.DependencyRules;
import guru.nidi.codeassert.findbugs.BugCollector;
import guru.nidi.codeassert.findbugs.FindBugsAnalyzer;
import guru.nidi.codeassert.findbugs.FindBugsResult;
import guru.nidi.codeassert.junit.CodeAssertTest;
import guru.nidi.codeassert.junit.PredefConfig;
import guru.nidi.codeassert.model.ModelAnalyzer;
import guru.nidi.codeassert.model.ModelResult;
import guru.nidi.codeassert.pmd.*;
import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.engine.AbstractGraphvizEngine;
import guru.nidi.graphviz.engine.GraphvizJdkEngine;
import guru.nidi.graphviz.engine.GraphvizServerEngine;
import guru.nidi.graphviz.engine.GraphvizV8Engine;
import guru.nidi.graphviz.model.*;
import guru.nidi.graphviz.parse.Parser;
import net.sourceforge.pmd.RulePriority;
import org.junit.Test;

import static guru.nidi.codeassert.junit.CodeAssertMatchers.packagesMatchExactly;
import static org.junit.Assert.assertThat;

public class CodeAnalysisTest extends CodeAssertTest {
    @Test
    public void dependencies() {
        class GuruNidiGraphviz extends DependencyRuler {
            DependencyRule model, attribute, engine, parse;

            public void defineRules() {
                engine.mayUse(model);
                parse.mayUse(model, attribute);
                model.mayUse(attribute);
            }
        }
        final DependencyRules rules = DependencyRules.denyAll()
                .withExternals("java*", "com.*", "org.*")
                .withRelativeRules(new GuruNidiGraphviz());
        assertThat(modelResult(), packagesMatchExactly(rules));
    }

    @Override
    protected ModelResult analyzeModel() {
        return new ModelAnalyzer(AnalyzerConfig.maven().main()).analyze();
    }

    @Override
    public void circularDependencies() {
        //TODO un-overwrite as soon as svg salamander is available from maven
    }

    @Override
    protected FindBugsResult analyzeFindBugs() {
        final BugCollector collector = new BugCollector().minPriority(Priorities.NORMAL_PRIORITY)
                .apply(PredefConfig.dependencyTestIgnore(CodeAnalysisTest.class))
                .because("It's SVG salamander", In.loc("com.kitfox.svg*").ignoreAll())
                .because("It's examples", In.clazz(ReadmeTest.class).ignore("DLS_DEAD_LOCAL_STORE"))
                .because("It's ok",
                        In.clazz(AbstractGraphvizEngine.class).ignore("SC_START_IN_CTOR"),
                        In.clazz(MutableGraph.class).ignore("SE_COMPARATOR_SHOULD_BE_SERIALIZABLE"),
                        In.loc("Communicator").ignore("RR_NOT_CHECKED"));
        return new FindBugsAnalyzer(AnalyzerConfig.maven().mainAndTest(), collector).analyze();
    }

    @Override
    protected PmdResult analyzePmd() {
        final PmdViolationCollector collector = new PmdViolationCollector().minPriority(RulePriority.MEDIUM)
                .apply(PredefConfig.minimalPmdIgnore())
                .because("It's examples", In.classes(ExampleTest.class, ReadmeTest.class)
                        .ignore("JUnitTestsShouldIncludeAssert", "LocalVariableCouldBeFinal", "UnusedLocalVariable"))
                .because("It's a test", In.loc("*Test")
                        .ignore("ExcessiveMethodLength"))
                .because("There are a lot of colors", In.clazz(Color.class)
                        .ignore("FieldDeclarationsShouldBeAtStartOfClass"))
                .because("it's ok here",
                        In.clazz(Serializer.class).ignore("AvoidStringBufferField"),
                        In.clazz(CreationContext.class).ignore("AvoidThrowingRawExceptionTypes"),
                        In.loc("GraphvizServer").ignore("AvoidInstantiatingObjectsInLoops"),
                        In.clazz(Shape.class).ignore("AvoidFieldNameMatchingTypeName"),
                        In.locs("Lexer", "Parser", "ImmutableGraph", "MutableGraph")
                                .ignore("CyclomaticComplexity", "StdCyclomaticComplexity", "ModifiedCyclomaticComplexity", "NPathComplexity"),
                        In.classes(GraphvizJdkEngine.class, GraphvizV8Engine.class, GraphvizServerEngine.class, AbstractGraphvizEngine.class)
                                .ignore("PreserveStackTrace", "SignatureDeclareThrowsException", "AvoidCatchingGenericException"),
                        In.classes(MutableGraph.class, Serializer.class, Parser.class).ignore("GodClass"),
                        In.locs("ImmutableGraph", "MutableGraph").ignore("ExcessiveMethodLength", "ExcessiveParameterList", "LooseCoupling"))
                .because("It's command line tool", In.loc("GraphvizServer")
                        .ignore("AvoidCatchingGenericException"))
                .because("It's wrapping an Exception with a RuntimeException", In.clazz(CreationContext.class)
                        .ignore("AvoidCatchingGenericException"));
        return new PmdAnalyzer(AnalyzerConfig.maven().mainAndTest(), collector)
                .withRulesets(PredefConfig.defaultPmdRulesets())
                .analyze();
    }

    @Override
    protected CpdResult analyzeCpd() {
        final CpdMatchCollector collector = new CpdMatchCollector()
                .apply(PredefConfig.cpdIgnoreEqualsHashCodeToString())
                .because("It's java",
                        In.loc("*Graph").ignore("Graph(strict, directed, cluster, label,", "if (strict != graph.strict) {"));
        return new CpdAnalyzer(AnalyzerConfig.maven().main(), 35, collector).analyze();
    }
}
