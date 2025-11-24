package fvf4k.demo.domain.failure

import arrow.core.NonEmptyList


sealed interface ValidationFailed : Failure
typealias ValidationFailures = NonEmptyList<ValidationFailed>

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
