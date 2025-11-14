package fvf4k.demo.domain.api

import arrow.core.raise.context.Raise
import fvf4k.demo.domain.ApplicationError
import fvf4k.demo.domain.model.CategorizedTransaction
import fvf4k.demo.domain.model.Transaction

fun interface TransactionCategorizer {
    context(_: Raise<ApplicationError>)
    fun categorize(transaction: Transaction): CategorizedTransaction
}