package fvf4k.demo.infra.web

import arrow.core.raise.ExperimentalRaiseAccumulateApi
import arrow.core.raise.context.accumulate
import arrow.core.raise.context.accumulating
import arrow.core.raise.context.either
import arrow.core.raise.context.withError
import fvf4k.demo.domain.api.QueryBudgetByCategory
import fvf4k.demo.domain.api.QueryByClientIdAndExpenseCategory
import fvf4k.demo.domain.failure.CategorizedTransactionCorrupted
import fvf4k.demo.domain.failure.InvalidQueryParameter
import fvf4k.demo.domain.failure.InvalidQueryParameters
import fvf4k.demo.domain.failure.QueryCategorizedTransactionFailed
import fvf4k.demo.domain.failure.QueryCategorizedTransactionFailure
import fvf4k.demo.domain.model.ClientId
import fvf4k.demo.domain.model.ExpenseCategory
import fvf4k.demo.domain.model.TransactionId
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

class TransactionQueryService(
    val queryByClientIdAndExpenseCategory: QueryByClientIdAndExpenseCategory,
    val queryBudgetByCategory: QueryBudgetByCategory
) : QueryByClientIdAndExpenseCategory by queryByClientIdAndExpenseCategory,
    QueryBudgetByCategory by queryBudgetByCategory

@RestController
class CategorizedTransactionController(val queryBy: TransactionQueryService) {
    @OptIn(ExperimentalRaiseAccumulateApi::class)
    @GetMapping("/client/{clientId}/transactions")
    fun getClientTransactionsByCategory(
        @PathVariable clientId: String?,
        @RequestParam expenseCategory: String?
    ): ResponseEntity<*> = either {
        val (validClientId, validExpenseCategory) = withError(::InvalidQueryParameters) {
            accumulate {
                val validClientId = accumulating { ClientId(clientId) }
                val validExpenseCategory = accumulating { ExpenseCategory(expenseCategory) }

                validClientId.value to validExpenseCategory.value
            }
        }

        val transactions = queryBy(validClientId, validExpenseCategory)

        transactions.map { t ->
            CategorizedTransactionResponse(
                transactionId = t.transaction.id,
                expenseCategory = t.expenseCategory
            )
        }
    }.fold(
        ifLeft = { error -> error.toResponseEntity() },
        ifRight = { ct -> ResponseEntity.ok(ct) }
    )

        @GetMapping("/client/{clientId}/categories-budget")
    fun getClientExpensesGroupedByCategory(
        @PathVariable clientId: String?
    ): ResponseEntity<*> = either {
        val validClientId = withError(::InvalidQueryParameter) {
            ClientId(clientId)
        }

        queryBy(validClientId)
    }.fold(
        ifLeft = { error ->
            when (error) {
                is InvalidQueryParameters -> ResponseEntity.badRequest()
                    .body(FailureResponse(error.message, error.innerErrors.map { it.message }))

                is QueryCategorizedTransactionFailed -> ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(FailureResponse(error.message))

                is CategorizedTransactionCorrupted -> ResponseEntity.internalServerError()
                    .body(FailureResponse(error.message, error.innerErrors.map { it.message }))
            }
        },
        ifRight = { ct -> ResponseEntity.ok(ct) }
    )
}

data class CategorizedTransactionResponse(
    val transactionId: TransactionId,
    val expenseCategory: ExpenseCategory
)

data class FailureResponse(
    val message: String,
    val details: List<String> = emptyList()
)

fun QueryCategorizedTransactionFailure.toResponseEntity(): ResponseEntity<FailureResponse> =
    when (this) {
        is InvalidQueryParameters -> ResponseEntity.badRequest()
            .body(FailureResponse(message, innerErrors.map { it.message }))

        is QueryCategorizedTransactionFailed -> ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(FailureResponse(message))

        is CategorizedTransactionCorrupted -> ResponseEntity.internalServerError()
            .body(FailureResponse(message, innerErrors.map { it.message }))
    }
