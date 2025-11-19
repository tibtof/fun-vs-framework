package fvf4k.demo.domain.failure

import arrow.core.Nel
import arrow.core.NonEmptyList

sealed interface Failure {
    val message: String
}

sealed interface ValidationFailed : Failure
typealias ValidationFailures = NonEmptyList<ValidationFailed>

data class InvalidUuid(val uuid: String) : ValidationFailed {
    override val message = "Invalid UUID: '$uuid'"
}

data class NullOrEmpty<T>(val property: String, val value: T) : ValidationFailed {
    override val message = "$property cannot be null or empty, actual value: '$value'"
}

data class InvalidCurrencyCode(val currencyCode: String) : ValidationFailed {
    override val message = "Invalid currency code: '$currencyCode'"
}

data object NullMerchantCategoryCode : ValidationFailed {
    override val message = "Merchant category code cannot be null"
}

data class InvalidMerchantCategoryPattern(val mcc: String) : ValidationFailed {
    override val message = "Merchant category code must be exactly 4 digits. Actual value: '$mcc'"
}

data class CouldNotCategorizeTransaction(val reason: String) : Failure {
    override val message = "Could not categorize transaction: $reason"
}

sealed interface SaveCategorizedTransactionFailure : Failure

sealed interface QueryCategorizedTransactionFailure : Failure

data class QueryCategorizedTransactionFailed(
    override val message: String
) : QueryCategorizedTransactionFailure

data class CategorizedTransactionCorrupted(
    val innerErrors: ValidationFailures,
    override val message: String = "CategorizedTransaction database entry corrupted."
) : QueryCategorizedTransactionFailure, SaveCategorizedTransactionFailure

data class InvalidQueryParameters(
    val innerErrors: ValidationFailures,
    override val message: String = "Invalid query parameters."
) : QueryCategorizedTransactionFailure

fun InvalidQueryParameter(failure: ValidationFailed) = InvalidQueryParameters(Nel.of(failure))

data class UpdateError(
    override val message: String
) : SaveCategorizedTransactionFailure
