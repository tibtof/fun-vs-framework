package fvf4k.demo.domain.api

import arrow.core.raise.context.Raise
import fvf4k.demo.domain.failure.Failure
import fvf4k.demo.domain.model.CategorizedTransaction
import fvf4k.demo.domain.model.Transaction

fun interface TransactionCategorizer {
    context(_: Raise<Failure>)
    fun categorize(transaction: Transaction): CategorizedTransaction
}