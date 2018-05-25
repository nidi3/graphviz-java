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
import guru.nidi.codeassert.junit.*;
import guru.nidi.codeassert.pmd.*;
import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.engine.*;
import guru.nidi.graphviz.model.*;
import guru.nidi.graphviz.parse.Parser;
import net.sourceforge.pmd.RulePriority;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static guru.nidi.codeassert.junit.CodeAssertMatchers.matchesRulesExactly;
import static org.hamcrest.MatcherAssert.assertThat;

class CodeAnalysisTest extends CodeAssertJunit5Test {

    //TODO un-overwrite as soon as svg salamander is available from maven
    @Override
    protected EnumSet<CodeAssertTestType> defaultTests() {
        final EnumSet<CodeAssertTestType> types = super.defaultTests();
        types.remove(CodeAssertTestType.CIRCULAR_DEPENDENCIES);
        return types;
    }

    @Test
    void dependencies() {
        assertThat(dependencyResult(), matchesRulesExactly());
    }

    @Override
    protected DependencyResult analyzeDependencies() {
        class GuruNidiGraphviz extends DependencyRuler {
            DependencyRule model, attribute, engine, parse, service;

            public void defineRules() {
                engine.mayUse(model, service);
                parse.mayUse(model, attribute);
                model.mayUse(attribute);
            }
        }
        final DependencyRules rules = DependencyRules.denyAll()
                .withExternals("java*", "com.*", "org.*")
                .withRelativeRules(new GuruNidiGraphviz());
        return new DependencyAnalyzer(AnalyzerConfig.maven().main()).rules(rules).analyze();
    }

    @Override
    protected FindBugsResult analyzeFindBugs() {
        final BugCollector collector = new BugCollector().minPriority(Priorities.NORMAL_PRIORITY)
                .apply(PredefConfig.dependencyTestIgnore(CodeAnalysisTest.class))
                .because("It's SVG salamander", In.loc("com.kitfox.svg*").ignoreAll())
                .because("It's examples", In.loc("ReadmeTest").ignore("DLS_DEAD_LOCAL_STORE"))
                .because("GraphvizServer is on localhost",
                        In.locs("GraphvizServer", "GraphvizServerEngine")
                                .ignore("UNENCRYPTED_SERVER_SOCKET", "UNENCRYPTED_SOCKET"))
                .because("We don't execute user submitted JS code",
                        In.clazz(GraphvizJdkEngine.class).ignore("SCRIPT_ENGINE_INJECTION"))
                .because("It's ok",
                        In.loc("DefaultExecutor").ignore("DM_DEFAULT_ENCODING"),
                        In.loc("GraphvizServer").ignore("COMMAND_INJECTION", "CRLF_INJECTION_LOGS"),
                        In.locs("GraphvizCmdLineEngine", "EngineTest", "SystemUtils", "Renderer").ignore("PATH_TRAVERSAL_IN"),
                        In.loc("EngineTest").ignore("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE"),
                        In.loc("Communicator").ignore("RR_NOT_CHECKED"));
        return new FindBugsAnalyzer(AnalyzerConfig.maven().mainAndTest(), collector).analyze();
    }

    @Override
    protected PmdResult analyzePmd() {
        final PmdViolationCollector collector = new PmdViolationCollector().minPriority(RulePriority.MEDIUM)
                .apply(PredefConfig.minimalPmdIgnore())
                .because("It's examples", In.locs("ExampleTest", "ReadmeTest")
                        .ignore("JUnitTestsShouldIncludeAssert", "LocalVariableCouldBeFinal", "UnusedLocalVariable"))
                .because("It's a test", In.loc("*Test")
                        .ignore("ExcessiveMethodLength"))
                .because("It's a bug in PMD?", In.clazz(MutableNode.class).ignore("ConstructorCallsOverridableMethod"))
                .because("There are a lot of colors", In.clazz(Color.class)
                        .ignore("FieldDeclarationsShouldBeAtStartOfClass"))
                .because("it's ok here",
                        In.clazz(Format.class).ignore("AvoidDuplicateLiterals"),
                        In.loc("LabelTest").ignore("JUnitTestContainsTooManyAsserts"),
                        In.clazz(Serializer.class).ignore("AvoidStringBufferField"),
                        In.clazz(CreationContext.class).ignore("AvoidThrowingRawExceptionTypes"),
                        In.loc("GraphvizServer").ignore("AvoidInstantiatingObjectsInLoops"),
                        In.clazz(Shape.class).ignore("AvoidFieldNameMatchingTypeName"),
                        In.loc("CommandRunnerTest").ignore("JUnitTestsShouldIncludeAssert"),
                        In.locs("Lexer", "Parser", "ImmutableGraph", "MutableGraph", "Label#applyTo", "Options#toJson")
                                .ignore("CyclomaticComplexity", "StdCyclomaticComplexity", "ModifiedCyclomaticComplexity", "NPathComplexity"),
                        In.classes(GraphvizJdkEngine.class, GraphvizV8Engine.class, GraphvizServerEngine.class, AbstractGraphvizEngine.class)
                                .ignore("PreserveStackTrace", "SignatureDeclareThrowsException", "AvoidCatchingGenericException"),
                        In.classes(MutableGraph.class, Serializer.class, Parser.class).ignore("GodClass"),
                        In.locs("ImmutableGraph", "MutableGraph").ignore("ExcessiveMethodLength", "ExcessiveParameterList", "LooseCoupling"))
                .because("It's command line tool", In.loc("GraphvizServer")
                        .ignore("AvoidCatchingGenericException", "PreserveStackTrace"))
                .because("I don't understand the message",
                        In.locs("CommandRunnerTest", "AbstractJsGraphvizEngine").ignore("SimplifiedTernary"))
                .because("I don't agree",
                        In.everywhere().ignore("SimplifyStartsWith"))
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
                        In.loc("*Graph").ignore("Graph(strict, directed, cluster, name,", "if (strict != graph.strict) {"));
        return new CpdAnalyzer(AnalyzerConfig.maven().main(), 35, collector).analyze();
    }

    @Override
    protected CheckstyleResult analyzeCheckstyle() {
        final StyleEventCollector collector = new StyleEventCollector()
                .apply(PredefConfig.minimalCheckstyleIgnore())
                .just(In.locs("Color", "Arrow", "Rank", "RankDir", "Shape", "Token", "Style").ignore("empty.line.separator"));
        final StyleChecks checks = PredefConfig.adjustedGoogleStyleChecks();
        return new CheckstyleAnalyzer(AnalyzerConfig.maven().main(), checks, collector).analyze();
    }
}
