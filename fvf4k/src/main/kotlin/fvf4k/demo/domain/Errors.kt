package fvf4k.demo.domain

import arrow.core.NonEmptyList

typealias ApplicationErrors = NonEmptyList<ApplicationError>

sealed interface ApplicationError {
    val message: String
}

data class DatabaseQueryError(
    override val message: String
) : ApplicationError

data class DatabaseUpdateError(
    override val message: String
) : ApplicationError

data class DataCorruptionError(
    override val message: String,
    val entity: String,
    val field: String,
    val invalidValue: String
) : ApplicationError

sealed interface ValidationError : ApplicationError

data class NullOrEmpty<T>(val property: String, val value: T) : ValidationError {
    override val message = "$property cannot be null or empty, actual value: '$value'"
}

data object NullMerchantCategoryCode : ValidationError {
    override val message = "Merchant category code cannot be null"
}

data object InvalidMerchantCategoryPattern : ValidationError {
    override val message = "Merchant category code must be exactly 4 digits"
}
