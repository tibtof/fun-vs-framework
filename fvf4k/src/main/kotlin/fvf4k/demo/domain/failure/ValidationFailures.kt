package fvf4k.demo.domain.failure

import arrow.core.NonEmptyList


sealed interface ValidationFailure : Failure
typealias ValidationFailures = NonEmptyList<ValidationFailure>

data class NullOrEmpty<T>(val property: String, val value: T) : ValidationFailure {
    override val message = "$property cannot be null or empty, actual value: '$value'"
}

data class InvalidCurrencyCode(val currencyCode: String) : ValidationFailure {
    override val message = "Invalid currency code: '$currencyCode'"
}

sealed interface MccValidationFailure : ValidationFailure

data object NullMerchantCategoryCode : MccValidationFailure {
    override val message = "Merchant category code cannot be null"
}

data class InvalidMerchantCategoryPattern(val mcc: String) : MccValidationFailure {
    override val message = "Merchant category code must be exactly 4 digits. Actual value: '$mcc'"
}
