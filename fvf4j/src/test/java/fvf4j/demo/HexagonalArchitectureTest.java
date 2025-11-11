package fvf4j.demo;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.tngtech.archunit.library.Architectures;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;

@AnalyzeClasses(
        packages = "fvf4j.demo",
        importOptions = {
                ImportOption.DoNotIncludeTests.class,
                ImportOption.DoNotIncludeJars.class
        }
)
public class HexagonalArchitectureTest {

    @ArchTest
    void shouldFollowOnionArchitecture(JavaClasses importedClasses) {
        Architectures.onionArchitecture()
                .domainModels("..domain..")
                .withOptionalLayers(true)
                .adapter("rest", "..controller..")
                .adapter("persistence", "..jpa..")
                .adapter("messaging", "..kafka..")
                .adapter("external", "..merchantdirectory..")
                .adapter("config", "..config..")
                .ignoreDependency(resideInAPackage("..config.."), resideInAnyPackage("..jpa.."))
                .withOptionalLayers(true)
                .because("Hexagonal architecture follows onion architecture principles")
                .check(importedClasses);
    }

    @ArchTest
    void domainShouldNotDependOnSpringFramework(JavaClasses importedClasses) {
        ArchRuleDefinition.noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("org.springframework..")
                .because("Domain should not depend on Spring Framework")
                .check(importedClasses);
    }
}
