package fvf4k.demo.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator.assertArchitecture
import com.lemonappdev.konsist.api.architecture.Layer
import com.lemonappdev.konsist.api.ext.list.withPackage
import com.lemonappdev.konsist.api.verify.assertFalse
import io.kotest.core.spec.style.FreeSpec

class HexagonalArchitectureSpec : FreeSpec({
    "domain should be independent from infrastructure" {
        Konsist
            .scopeFromProduction()
            .assertArchitecture {
                val domain = Layer("Domain", "..domain..")
                val configuration = Layer("Config", "..config..")
                val kafka = Layer("Kafka", "..infra.kafka..")
                val web = Layer("Web", "..infra.web..")
                val jpa = Layer("JPA", "..infra.jpa..")
                val merchantDirectory = Layer("MerchDir", "..infra.md..")

                domain.doesNotDependOn(
                    configuration,
                    kafka,
                    web,
                    jpa,
                    merchantDirectory
                )
            }
    }

    "domain should be independent from Spring Framework" {
        Konsist
            .scopeFromProduction()
            .classes()
            .withPackage("fvf4k.demo.domain..")
            .assertFalse { classDeclaration ->
                classDeclaration
                    .containingFile
                    .imports
                    .any { import ->
                        import.hasNameStartingWith("org.springframework")
                    }
            }
    }

    "hexagonal architecture layers should respect dependency rules" {
        Konsist
            .scopeFromProduction()
            .assertArchitecture {
                // Domain
                val domainFailures = Layer("Domain failures", "..domain.failure..")
                val domainModel = Layer("Domain model", "..domain.model..")
                val domainApi = Layer("Domain API", "..domain.api..")
                val domainSpi = Layer("Domain SPI", "..domain.spi..")

                domainFailures.dependsOnNothing()
                domainModel.dependsOn(domainFailures)
                domainApi.dependsOn(domainModel)
                domainSpi.dependsOn(domainModel)

                //Infrastructure
                val configuration = Layer("Configuration", "..infra.config..")
                configuration.dependsOn(domainApi)

                val kafka = Layer("Kafka", "..infra.kafka..")
                kafka.dependsOn(domainApi)

                val web = Layer("Web", "..infra.web..")
                web.dependsOn(domainApi)

                val jpa = Layer("JPA", "..infra.jpa..")
                jpa.dependsOn(domainSpi)

                val merchantDirectory = Layer("Merchant Directory (external http service)", "..infra.md..")
                merchantDirectory.dependsOn(domainSpi)
            }
    }

})
