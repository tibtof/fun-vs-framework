package fvf4k.demo.domain

import arrow.core.NonEmptyList

sealed interface ApplicationError {
    val message: String
}

sealed interface DatabaseQueryError : ApplicationError

data class DatabaseQueryFailedError(
    override val message: String
) : DatabaseQueryError

data class DatabaseUpdateError(
    override val message: String
) : ApplicationError

data class DataCorruptionError(
    override val message: String,
    val innerErrors: ValidationErrors
) : DatabaseQueryError, ValidationError

sealed interface ValidationError : ApplicationError
typealias ValidationErrors = NonEmptyList<ValidationError>

data class InvalidUuid(val uuid: String) : ValidationError {
    override val message = "Invalid UUID: '$uuid'"
}

data class NullOrEmpty<T>(val property: String, val value: T) : ValidationError {
    override val message = "$property cannot be null or empty, actual value: '$value'"
}

data object NullMerchantCategoryCode : ValidationError {
    override val message = "Merchant category code cannot be null"
}

data class InvalidMerchantCategoryPattern(val mcc: String) : ValidationError {
    override val message = "Merchant category code must be exactly 4 digits. Actual value: '$mcc'"
}

data class CouldNotCategorizeTransaction(val reason: String) : ApplicationError {
    override val message = "Could not categorize transaction: $reason"
}