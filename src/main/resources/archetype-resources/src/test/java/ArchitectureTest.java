package ${package};

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.GeneralCodingRules;
import picocli.CommandLine.Command;

@AnalyzeClasses(packages = "${package}")
final class ArchitectureTest {

    @ArchTest
    static final ArchRule CORE_DOES_NOT_DEPEND_ON_OUTER_LAYERS = noClasses()
            .that().resideInAPackage("..core..")
            .should().dependOnClassesThat().resideInAnyPackage("..cli..", "..web..");

    @ArchTest
    static final ArchRule NO_PACKAGE_CYCLES = slices()
            .matching("${package}.(*)..")
            .should().beFreeOfCycles();

    @ArchTest
    static final ArchRule COMMANDS_ARE_NAMED_CONSISTENTLY = classes()
            .that().areAnnotatedWith(Command.class)
            .should().haveSimpleNameEndingWith("Command");

    @ArchTest
    static final ArchRule NO_STANDARD_STREAMS = GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;

    @ArchTest
    static final ArchRule NO_GENERIC_EXCEPTIONS = GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;

    private ArchitectureTest() {
    }
}
