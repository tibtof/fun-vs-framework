package fvf4k.demo.slidesamples

import fvf4k.demo.domain.api.QueryBudgetByCategory
import fvf4k.demo.domain.api.QueryByClientIdAndExpenseCategory
import fvf4k.demo.domain.failure.CategorizedTransactionCorrupted
import fvf4k.demo.domain.failure.InvalidQueryParameters
import fvf4k.demo.domain.failure.QueryCategorizedTransactionFailed
import fvf4k.demo.domain.failure.QueryCategorizedTransactionFailure
import fvf4k.demo.domain.model.ClientId
import fvf4k.demo.domain.model.ExpenseCategory
import fvf4k.demo.domain.model.TransactionId
import fvf4k.demo.infra.web.toResponseEntity
import org.springframework.http.HttpStatus
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

//@RestController
//class CategorizedTransactionController(val queryService: QueryRepository) {
//    @GetMapping("/client/{clientId}/categories-budget")
//    fun getClientExpensesGroupedByCategory(
//        @PathVariable clientId: String?
//    ): ResponseEntity<*> {
//        val validClientId = ClientId(clientId)
//        clientId?.
//
//        val categoryBudgets = queryService.queryBudgetByCategory(validClientId)
//
//        categoryBudgets
//    }.toResponseEntity()
//
//}
//
//data class CategorizedTransactionResponse(
//    val transactionId: TransactionId,
//    val expenseCategory: ExpenseCategory
//)

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
