package fvf4k.demo.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator.assertArchitecture
import com.lemonappdev.konsist.api.architecture.Layer
import com.tngtech.archunit.library.Architectures
import io.kotest.core.spec.style.FreeSpec

class HexagonalArchitectureSpec : FreeSpec({
    "hexagonal architecture layers should respect dependency rules" {
        Konsist
            .scopeFromProduction()
            .assertArchitecture {
//                val
//                Allowed libraries
//                val libraries = listOf("java.util..", "arrow.core..", "com.github.f4b6a3.uuid..")
//                val librariesLayer = Layer("Libraries", libraries.joinToString(", "))

                // Define layers
                val domain = Layer("Domain", "fvf4k.demo.domain.model..")
//                val domainModel = Layer("Domain model", "fvf4k.demo.domain.model..")
                val domainApi = Layer("Domain API", "fvf4k.demo.domain.api..")
                val merchantDirectory = Layer("Merchant Directory", "fvf4k.demo.infra.merchantdirectory..")
                val domainSpi = Layer("Domain SPI", "fvf4k.demo.domain.spi..")
//                val domainFailures = Layer("Domain failures", "fvf4k.demo.domain.failure..")

                // Domain should not depend on infrastructure
                domain.dependsOnNothing()
//                domainModel.dependsOnNothing()
                domainApi.dependsOn(domain)
//                domainSpi.dependsOn(domain)
//                domainSpi.dependsOnNothing()

                val web = Layer("Web", "fvf4k.demo.infra.web..")
                web.dependsOn(domain)

                // Infrastructure can depend on domain
                merchantDirectory.dependsOn(domain)

            }
    }

//    "hex archunit" {
//        Architectures.onionArchitecture()
//            .domainModels("..domain..")
//            .withOptionalLayers(true)
//            .adapter("Web", "..web..")
//            .adapter("JPA")
//            .adapter("Merchant Directory", "..merchantdirectory..")
//    }
})
