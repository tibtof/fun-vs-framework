package fvf4j.demo;


import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.tngtech.archunit.library.Architectures;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;

public class HexagonalArchitectureTest {

    private final JavaClasses importedClasses =
            new ClassFileImporter()
                    .withImportOption(new ImportOption.DoNotIncludeTests())
                    .withImportOption(new ImportOption.DoNotIncludeJars())
                    .importPackages("fvf4j.demo");

    @Test
    void shouldFollowOnionArchitecture() {
        Architectures.onionArchitecture()
                .domainModels("..domain..")
                .applicationServices("..domain..")
                .adapter("rest", "..controller..")
                .adapter("persistence", "..jpa..")
                .adapter("messaging", "..kafka..")
                .adapter("external", "..merchantdirectory..")
                .adapter("config", "..config..")
                .ignoreDependency(
                        resideInAPackage("..config.."),
                        resideInAnyPackage("..jpa..")
                )
                .withOptionalLayers(true)
                .because("Hexagonal architecture follows onion architecture principles")
                .check(importedClasses);
    }

    @Test
    void domainShouldNotDependOnSpringFramework() {
        ArchRuleDefinition.noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("org.springframework..")
                .check(importedClasses);
    }
}
