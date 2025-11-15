package fvf4k.demo.domain.model

import arrow.core.raise.context.Raise
import arrow.core.raise.context.raise
import fvf4k.demo.domain.InvalidMerchantCategoryPattern
import fvf4k.demo.domain.NullMerchantCategoryCode
import fvf4k.demo.domain.NullOrEmpty
import fvf4k.demo.domain.ValidationError
import java.math.BigDecimal
import kotlin.uuid.Uuid


data class Transaction(
    val id: TransactionId,
    val clientId: ClientId,
    val accountId: AccountId,
    val amount: Amount,
    val mcc: MerchantCategoryCode
)

@JvmInline value class ClientId private constructor(val value: String) {
    companion object {
        context(_: Raise<ValidationError>)
        operator fun invoke(value: String?): ClientId {
            if (value == null || value.isEmpty()) raise(NullOrEmpty("clientId", value))
            else return ClientId(value)
        }
    }
}

@JvmInline value class AccountId private constructor(val value: String) {
    companion object {
        context(_: Raise<ValidationError>)
        operator fun invoke(value: String?): AccountId {
            if (value == null || value.isEmpty()) raise(NullOrEmpty("accountId", value))
            else return AccountId(value)
        }
    }
}

@JvmInline value class Amount private constructor(val value: BigDecimal) {
    companion object {
        context(_: Raise<ValidationError>)
        operator fun invoke(value: BigDecimal?): Amount {
            if (value == null) raise(NullOrEmpty("amount", value))
            else return Amount(value)
        }
    }
}

@JvmInline value class TransactionId(val value: Uuid) {
    companion object {
        context(_: Raise<ValidationError>)
        operator fun invoke(value: String?): TransactionId {
            if (value == null || value.isEmpty()) raise(NullOrEmpty("transactionId", value))
            else return TransactionId(value)
        }
    }
}

@JvmInline value class MerchantCategoryCode private constructor(val value: String) {
    companion object {
        private val MCC_PATTERN = Regex("^\\d{4}$")

        context(_: Raise<ValidationError>)
        operator fun invoke(value: String?): MerchantCategoryCode = when {
            value == null -> raise(NullMerchantCategoryCode)
            MCC_PATTERN.matches(value) -> raise(InvalidMerchantCategoryPattern(value))
            else -> MerchantCategoryCode(value)
        }
    }
}
