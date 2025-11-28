package fvf4k.demo.domain.failure

import arrow.core.Nel

sealed interface QueryCategorizedTransactionFailure : CategorizeTransactionFailure

data class QueryCategorizedTransactionFailed(
    override val message: String
) : QueryCategorizedTransactionFailure

data class CategorizedTransactionCorrupted(
    val innerErrors: ValidationFailures,
    override val message: String = "CategorizedTransaction database entry corrupted."
) : QueryCategorizedTransactionFailure, CategorizeTransactionFailure

data class InvalidQueryParameters(
    val innerErrors: ValidationFailures,
    override val message: String = "Invalid query parameters."
) : QueryCategorizedTransactionFailure

fun InvalidQueryParameter(failure: ValidationFailure) = InvalidQueryParameters(Nel.of(failure))
