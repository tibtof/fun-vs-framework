package fvf4k.demo.infra.jpa

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class CategorizedTransactionJpaConfiguration {

    @Bean
    fun readJpaAdaptor(repository: CategorizedTransactionJpaRepository) =
        CategorizedTransactionReadRepositoryAdapter(repository)

    @Bean
    fun writeJpaAdaptor(repository: CategorizedTransactionJpaRepository) =
        CategorizedTransactionWriteRepositoryAdapter(repository)
}