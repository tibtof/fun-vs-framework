package fvf4k.demo.infra.merchantdirectory

import arrow.core.raise.catch
import arrow.core.raise.context.Raise
import arrow.core.raise.context.raise
import fvf4k.demo.domain.ApplicationError
import fvf4k.demo.domain.CouldNotCategorizeTransaction
import fvf4k.demo.domain.MerchantDirectory
import fvf4k.demo.domain.model.ExpenseCategory
import fvf4k.demo.domain.model.MerchantCategoryCode

class MerchantDirectoryAdapter(val merchantDirectoryService: MerchantDirectoryService) : MerchantDirectory {
    context(_: Raise<ApplicationError>)
    override fun getFor(mcc: MerchantCategoryCode): ExpenseCategory {
        val merchantInfo = catch(
            block = { merchantDirectoryService.getMerchantCategoryCode(mcc.value) },
            catch = { exception ->
                raise(CouldNotCategorizeTransaction("could not retrieve merchant info for mcc='${mcc.value}': ${exception.message}"))
            }
        )
        return ExpenseCategory.Companion(merchantInfo.category)
    }
}