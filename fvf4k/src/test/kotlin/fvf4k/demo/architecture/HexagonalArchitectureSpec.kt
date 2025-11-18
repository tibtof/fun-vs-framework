package fvf4k.demo.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator.assertArchitecture
import com.lemonappdev.konsist.api.architecture.Layer
import io.kotest.core.spec.style.FreeSpec

class HexagonalArchitectureSpec : FreeSpec({
    "hexagonal architecture layers should respect dependency rules" {
        Konsist
            .scopeFromProduction()
            .assertArchitecture {
                // Domain
                val domainFailures = Layer("Domain failures", "fvf4k.demo.domain.failure..")
                val domainModel = Layer("Domain model", "fvf4k.demo.domain.model..")
                val domainApi = Layer("Domain API", "fvf4k.demo.domain.api..")
                val domainSpi = Layer("Domain SPI", "fvf4k.demo.domain.spi..")

                domainFailures.dependsOnNothing()
                domainModel.dependsOn(domainFailures)
                domainApi.dependsOn(domainModel)
                domainSpi.dependsOn(domainModel)

                //Infrastructure
                val configuration = Layer("Configuration", "fvf4k.demo.infra.config..")
                configuration.dependsOn(domainApi)

                val kafka = Layer("Kafka", "fvf4k.demo.infra.kafka..")
                kafka.dependsOn(domainApi)

                val web = Layer("Web", "fvf4k.demo.infra.web..")
                web.dependsOn(domainApi)

                val jpa = Layer("JPA", "fvf4k.demo.infra.jpa..")
                jpa.dependsOn(domainSpi)

                val merchantDirectory =
                    Layer("Merchant Directory (external http service)", "fvf4k.demo.infra.merchantdirectory..")
                merchantDirectory.dependsOn(domainSpi)
            }
    }

})
