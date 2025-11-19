package fvf4k.demo.infra.md

import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
@EnableFeignClients
class MerchantDirectoryConfiguration {

    @Bean
    fun merchantDirectoryAdapter(merchantDirectoryService: MerchantDirectoryService) =
        ExpenseCategoryResolverAdapter(merchantDirectoryService)
}