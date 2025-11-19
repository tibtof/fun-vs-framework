package fvf4k.demo.infra.jpa

import arrow.core.raise.catch
import arrow.core.raise.context.Raise
import arrow.core.raise.context.raise
import fvf4k.demo.domain.failure.SaveCategorizedTransactionFailure
import fvf4k.demo.domain.failure.UpdateError
import fvf4k.demo.domain.model.CategorizedTransaction
import fvf4k.demo.domain.spi.SaveCategorizedTransaction


class CategorizedTransactionWriteRepositoryAdapter(
    val jpaRepository: CategorizedTransactionJpaRepository
) : SaveCategorizedTransaction {

    context(_: Raise<SaveCategorizedTransactionFailure>)
    override fun invoke(transaction: CategorizedTransaction): CategorizedTransaction =
        catch({
            jpaRepository.save(transaction.toJpaEntity()).toDomain()
        }) {
            raise(UpdateError("Could not save TransactionCategory: ${it.message}"))
        }
}
