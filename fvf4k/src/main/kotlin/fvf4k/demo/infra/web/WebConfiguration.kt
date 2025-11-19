package fvf4k.demo.infra.web

import fvf4k.demo.domain.api.QueryBudgetByCategory
import fvf4k.demo.domain.api.QueryByClientIdAndExpenseCategory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class WebConfiguration {

    @Bean
    fun queryService(
        queryByClientIdAndExpenseCategory: QueryByClientIdAndExpenseCategory,
        queryBudgetByCategory: QueryBudgetByCategory
    ) = TransactionQueryService(
        queryByClientIdAndExpenseCategory,
        queryBudgetByCategory
    )
}