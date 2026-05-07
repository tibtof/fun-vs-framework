package fvf4k.demo.infra.md

import arrow.core.raise.catch
import arrow.core.raise.context.Raise
import arrow.core.raise.context.raise
import arrow.core.raise.context.withError
import fvf4k.demo.domain.failure.CategorizeTransactionFailure
import fvf4k.demo.domain.failure.ExpenseCategoryMappingFailed
import fvf4k.demo.domain.failure.ExpenseCategoryResolutionFailed
import fvf4k.demo.domain.model.ExpenseCategory
import fvf4k.demo.domain.model.MerchantCategoryCode
import fvf4k.demo.domain.spi.ResolveExpenseCategory

class ExpenseCategoryResolverAdapter(val merchantDirectoryService: MerchantDirectoryService) : ResolveExpenseCategory {
    context(_: Raise<CategorizeTransactionFailure>)
    override fun invoke(mcc: MerchantCategoryCode): ExpenseCategory {
        val merchantInfo = catch(
            block = { merchantDirectoryService.getMerchantCategoryCode(mcc.value) },
            catch = { exception ->
                raise(ExpenseCategoryResolutionFailed("could not retrieve merchant info for mcc='${mcc.value}': ${exception.message}"))
            }
        )
        val expenseCategory = withError(::ExpenseCategoryMappingFailed) {
            ExpenseCategory(merchantInfo.category)
        }
        return expenseCategory
    }
}
