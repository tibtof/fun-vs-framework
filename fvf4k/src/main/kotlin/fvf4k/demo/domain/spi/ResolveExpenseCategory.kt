package fvf4k.demo.domain.spi

import arrow.core.raise.context.Raise
import fvf4k.demo.domain.failure.CategorizeTransactionFailure
import fvf4k.demo.domain.model.ExpenseCategory
import fvf4k.demo.domain.model.MerchantCategoryCode

fun interface ResolveExpenseCategory {
    context(_: Raise<CategorizeTransactionFailure>)
    operator fun invoke(mcc: MerchantCategoryCode): ExpenseCategory
}