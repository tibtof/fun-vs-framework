package fvf4k.demo.domain

import arrow.core.raise.context.Raise
import fvf4k.demo.domain.model.ExpenseCategory
import fvf4k.demo.domain.model.MerchantCategoryCode


fun interface MerchantDirectory {
    context(_: Raise<ApplicationError>)
    fun getFor(mcc: MerchantCategoryCode): ExpenseCategory
}