package fvf4k.demo.web

import arrow.core.Nel
import arrow.core.raise.context.either
import arrow.core.raise.context.zipOrAccumulate
import fvf4k.demo.domain.DatabaseQueryError
import fvf4k.demo.domain.ValidationError
import fvf4k.demo.domain.api.QueryBudgetByCategory
import fvf4k.demo.domain.api.QueryByClientIdAndExpenseCategory
import fvf4k.demo.domain.api.QueryExpenseCategoriesByClientId
import fvf4k.demo.domain.model.ClientId
import fvf4k.demo.domain.model.ExpenseCategory
import fvf4k.demo.domain.model.TransactionId
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class CategorizedTransactionController<out R>(val findBy: R)
        where R : QueryByClientIdAndExpenseCategory,
              R : QueryExpenseCategoriesByClientId,
              R : QueryBudgetByCategory {

    @GetMapping("/client/{clientId}/transactions")
    fun getTransactionsByClientAndCategory(
        @PathVariable clientId: String?,
        @RequestParam expenseCategory: String?
    ): ResponseEntity<List<CategorizedTransactionResponse>> = either {
        val (validatedClientId, validatedCategory) = validateAndMapErrors {
            zipOrAccumulate(
                { ClientId(clientId) },
                { ExpenseCategory(expenseCategory) }
            ) { c, e -> c to e }
        }

        val transactions = queryAndMapErrors {
            findBy(validatedClientId, validatedCategory)
        }

        transactions.map { t ->
            CategorizedTransactionResponse(
                transactionId = t.transaction.id,
                expenseCategory = t.expenseCategory
            )
        }
    }.fold(
        ifRight = { categorizedTransactions ->
            ResponseEntity.ok(categorizedTransactions)
        },
        ifLeft = { error ->
            when (error) {
                is Nel<ValidationError> -> ResponseEntity.badRequest().build()
                is DatabaseQueryError -> ResponseEntity.notFound().build()
                else -> ResponseEntity.internalServerError().build()
            }
        }
    )
}

data class CategorizedTransactionResponse(
    val transactionId: TransactionId,
    val expenseCategory: ExpenseCategory
)