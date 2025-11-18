package fvf4k.demo.infra.jpa

import arrow.core.raise.catch
import arrow.core.raise.context.Raise
import arrow.core.raise.context.raise
import fvf4k.demo.domain.failure.Failure
import fvf4k.demo.domain.failure.UpdateError
import fvf4k.demo.domain.model.CategorizedTransaction
import fvf4k.demo.domain.spi.SaveCategorizedTransaction
import fvf4k.demo.domain.spi.UpdateCategorizedTransaction


class CategorizedTransactionWriteRepositoryAdapter(
    val jpaRepository: CategorizedTransactionJpaRepository
) : SaveCategorizedTransaction, UpdateCategorizedTransaction {

    context(_: Raise<Failure>)
    override fun insert(transaction: CategorizedTransaction): CategorizedTransaction =
        catch({
            jpaRepository.save(transaction.toJpaEntity()).toDomain()
        }) {
            raise(UpdateError("could not insert transaction category: ${it.message}"))
        }

    context(_: Raise<Failure>)
    override fun update(transaction: CategorizedTransaction): CategorizedTransaction =
        catch({
            jpaRepository.save(transaction.toJpaEntity()).toDomain()
        }) {
            raise(UpdateError("could not update transaction category: ${it.message}"))
        }
}
