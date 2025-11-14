package fvf4k.demo.domain.model

import arrow.core.raise.context.Raise
import arrow.core.raise.context.raise
import fvf4k.demo.domain.NullOrEmpty
import fvf4k.demo.domain.ValidationError
import kotlin.uuid.Uuid

data class CategorizedTransaction(
    val id: CategorizedTransactionId,
    val transaction: Transaction,
    val expenseCategory: ExpenseCategory
)

@JvmInline value class CategorizedTransactionId(val value: Uuid) {
    companion object {
        fun generate(): CategorizedTransactionId = CategorizedTransactionId(Uuid.random())
    }
}

@JvmInline value class ExpenseCategory private constructor(val value: String?) {
    companion object {
        context(_: Raise<ValidationError>)
        operator fun invoke(value: String?): ExpenseCategory {
            if (value == null || value.isEmpty()) raise(NullOrEmpty("expenseCategory", value))
            else return ExpenseCategory(value)
        }
    }
}
