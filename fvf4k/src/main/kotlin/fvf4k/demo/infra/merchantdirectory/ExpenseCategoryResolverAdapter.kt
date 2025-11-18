package fvf4k.demo.infra.merchantdirectory

import arrow.core.raise.catch
import arrow.core.raise.context.Raise
import arrow.core.raise.context.raise
import fvf4k.demo.domain.failure.CouldNotCategorizeTransaction
import fvf4k.demo.domain.failure.Failure
import fvf4k.demo.domain.model.ExpenseCategory
import fvf4k.demo.domain.model.MerchantCategoryCode
import fvf4k.demo.domain.spi.ResolveExpenseCategory

class ExpenseCategoryResolverAdapter(val merchantDirectoryService: MerchantDirectoryService) : ResolveExpenseCategory {
    context(_: Raise<Failure>)
    override fun invoke(mcc: MerchantCategoryCode): ExpenseCategory {
        val merchantInfo = catch(
            block = { merchantDirectoryService.getMerchantCategoryCode(mcc.value) },
            catch = { exception ->
                raise(CouldNotCategorizeTransaction("could not retrieve merchant info for mcc='${mcc.value}': ${exception.message}"))
            }
        )
        return ExpenseCategory.Companion(merchantInfo.category)
    }
}
