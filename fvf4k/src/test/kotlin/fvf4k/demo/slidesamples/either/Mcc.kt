package fvf4k.demo.slidesamples.either

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right

sealed interface Failure {
    val message: String
}

sealed interface ValidationFailed : Failure

data object NullMerchantCategoryCode : ValidationFailed {
    override val message = "Merchant category code cannot be null"
}

data class InvalidMerchantCategoryPattern(val mcc: String) : ValidationFailed {
    override val message = "Merchant category code must be exactly 4 digits. Actual value: '$mcc'"
}

@JvmInline value class MerchantCategoryCode private constructor(val value: String) {
    companion object {
        private val MCC_PATTERN = Regex("^\\d{4}$")

        operator fun invoke(value: String?): Either<ValidationFailed, MerchantCategoryCode> = when {
            value == null -> Left(NullMerchantCategoryCode)
            !MCC_PATTERN.matches(value) -> Left(InvalidMerchantCategoryPattern(value))
            else -> Right(MerchantCategoryCode(value))
        }
    }
}
