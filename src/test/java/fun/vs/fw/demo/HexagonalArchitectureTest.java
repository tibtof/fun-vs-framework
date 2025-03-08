package fun.vs.fw.demo;


import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;
import org.junit.jupiter.api.Test;

public class HexagonalArchitectureTest {

    private final JavaClasses importedClasses = new ClassFileImporter().importPackages("fun.vs.fw.demo");


    @Test
    void domainShouldNotDependOnOtherModules() {
        ArchRuleDefinition.noClasses()
                .that().resideInAPackage("fun.vs.fw.demo.domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "fun.vs.fw.demo.config..",
                        "fun.vs.fw.demo.controller..",
                        "fun.vs.fw.demo.jpa..",
                        "fun.vs.fw.demo.kafka..",
                        "fun.vs.fw.demo.merchantdirectory..")
                .check(importedClasses);
    }

    @Test
    void domainShouldNotDependOnSpringFramework() {
        ArchRuleDefinition.noClasses()
                .that().resideInAPackage("fun.vs.fw.demo.domain..")
                .should().dependOnClassesThat().resideInAnyPackage("org.springframework..")
                .check(importedClasses);
    }

    @Test
    void modulesShouldOnlyDependOnDomain() {
        // Define slices for all non-domain modules.
        SlicesRuleDefinition.slices()
                .matching("fun.vs.fw.demo.(controller|jpa|kafka|merchantdirectory)..")
                .should().notDependOnEachOther()
                .check(importedClasses);
    }
}
