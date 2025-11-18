package fvf4k.demo.domain.spi

import arrow.core.raise.context.Raise
import fvf4k.demo.domain.failure.Failure
import fvf4k.demo.domain.model.ExpenseCategory
import fvf4k.demo.domain.model.MerchantCategoryCode

fun interface ResolveExpenseCategory {
    context(_: Raise<Failure>)
    operator fun invoke(mcc: MerchantCategoryCode): ExpenseCategory
}