package fvf4k.demo.infra.web

import arrow.core.raise.context.zipOrAccumulate
import arrow.core.raise.eagerEffect
import fvf4k.demo.domain.api.QueryBudgetByCategory
import fvf4k.demo.domain.api.QueryByClientIdAndExpenseCategory
import fvf4k.demo.domain.model.ClientId
import fvf4k.demo.domain.model.ExpenseCategory
import fvf4k.demo.domain.model.TransactionId
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Service
class QueryRepository(
    val queryByClientIdAndExpenseCategory: QueryByClientIdAndExpenseCategory,
    val queryBudgetByCategory: QueryBudgetByCategory
)

@RestController
class CategorizedTransactionController(val queryService: QueryRepository) {
    @GetMapping("/client/{clientId}/transactions")
    fun getClientTransactionsByCategory(
        @PathVariable clientId: String?,
        @RequestParam expenseCategory: String?
    ): ResponseEntity<*> = eagerEffect {
        val (validClientId, validExpenseCategory) = validateAndMapErrors {
            zipOrAccumulate(
                { ClientId(clientId) },
                { ExpenseCategory(expenseCategory) }
            ) { c, e -> c to e }
        }

        val transactions = queryAndMapErrors {
            queryService.queryByClientIdAndExpenseCategory(validClientId, validExpenseCategory)
        }

        transactions.map { t ->
            CategorizedTransactionResponse(
                transactionId = t.transaction.id,
                expenseCategory = t.expenseCategory
            )
        }
    }.toResponseEntity()


    @GetMapping("/client/{clientId}/categories-budget")
    fun getClientExpensesGroupedByCategory(
        @PathVariable clientId: String?
    ): ResponseEntity<*> = eagerEffect {
        val validClientId = validateAndMapError {
            ClientId(clientId)
        }

        val categoryBudgets = queryAndMapErrors {
            queryService.queryBudgetByCategory(validClientId)
        }

        categoryBudgets
    }.toResponseEntity()

}

data class CategorizedTransactionResponse(
    val transactionId: TransactionId,
    val expenseCategory: ExpenseCategory
)
