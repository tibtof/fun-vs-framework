package fvf4k.demo.domain.model

import arrow.core.raise.catch
import arrow.core.raise.context.Raise
import arrow.core.raise.context.raise
import fvf4k.demo.domain.failure.InvalidCurrencyCode
import fvf4k.demo.domain.failure.InvalidMerchantCategoryPattern
import fvf4k.demo.domain.failure.NullMerchantCategoryCode
import fvf4k.demo.domain.failure.NullOrEmpty
import fvf4k.demo.domain.failure.ValidationFailed
import java.math.BigDecimal
import java.util.*


data class Transaction(
    val id: TransactionId,
    val clientId: ClientId,
    val accountId: AccountId,
    val money: Money,
    val mcc: MerchantCategoryCode
)

@JvmInline value class ClientId private constructor(val value: UUID) {
    companion object {
        context(_: Raise<ValidationFailed>)
        operator fun invoke(value: String?): ClientId {
            if (value == null || value.isEmpty()) raise(NullOrEmpty("clientId", value))
            else return ClientId(value)
        }

        context(_: Raise<ValidationFailed>)
        operator fun invoke(value: UUID?): ClientId {
            if (value == null) raise(NullOrEmpty("clientId", value))
            else return ClientId(value)
        }
    }
}

@JvmInline value class AccountId private constructor(val value: UUID) {
    companion object {
        context(_: Raise<ValidationFailed>)
        operator fun invoke(value: String?): AccountId {
            if (value == null || value.isEmpty()) raise(NullOrEmpty("accountId", value))
            else return AccountId(value)
        }

        context(_: Raise<ValidationFailed>)
        operator fun invoke(value: UUID?): AccountId {
            if (value == null) raise(NullOrEmpty("accountId", value))
            else return AccountId(value)
        }
    }
}

data class Money private constructor(val value: BigDecimal, val currency: Currency) {
    companion object {
        context(_: Raise<ValidationFailed>)
        operator fun invoke(value: BigDecimal?, currencyCode: String?): Money {
            if (currencyCode == null || currencyCode.isEmpty()) raise(NullOrEmpty("currencyCode", currencyCode))
            val currency = catch({ Currency.getInstance(currencyCode) }) { raise(InvalidCurrencyCode(currencyCode)) }
            if (value == null) raise(NullOrEmpty("amount", value))
            return Money(value, currency)
        }
    }
}

@JvmInline value class TransactionId(val value: UUID) {
    companion object {
        context(_: Raise<ValidationFailed>)
        operator fun invoke(value: String?): TransactionId {
            if (value == null || value.isEmpty()) raise(NullOrEmpty("transactionId", value))
            else return TransactionId(value)
        }

        context(_: Raise<ValidationFailed>)
        operator fun invoke(value: UUID?): TransactionId {
            if (value == null) raise(NullOrEmpty("transactionId", value))
            else return TransactionId(value)
        }
    }
}

@JvmInline value class MerchantCategoryCode private constructor(val value: String) {
    companion object {
        private val MCC_PATTERN = Regex("^\\d{4}$")

        context(_: Raise<ValidationFailed>)
        operator fun invoke(value: String?): MerchantCategoryCode = when {
            value == null -> raise(NullMerchantCategoryCode)
            !MCC_PATTERN.matches(value) -> raise(InvalidMerchantCategoryPattern(value))
            else -> MerchantCategoryCode(value)
        }
    }
}
