package fvf4k.demo.domain.spi

import arrow.core.raise.context.Raise
import fvf4k.demo.domain.failure.Failure
import fvf4k.demo.domain.model.ExpenseCategory
import fvf4k.demo.domain.model.MerchantCategoryCode

fun interface MerchantDirectory {
    context(_: Raise<Failure>)
    fun getFor(mcc: MerchantCategoryCode): ExpenseCategory
}