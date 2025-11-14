package fvf4k.demo.infra.jpa

import arrow.core.raise.catch
import arrow.core.raise.context.Raise
import arrow.core.raise.context.raise
import fvf4k.demo.domain.CategoryBudget
import fvf4k.demo.domain.DatabaseQueryError
import fvf4k.demo.domain.model.CategorizedTransaction
import fvf4k.demo.domain.model.ClientId
import fvf4k.demo.domain.model.ExpenseCategory
import fvf4k.demo.domain.model.TransactionId
import fvf4k.demo.domain.spi.FindByClientIdAndExpenseCategory
import fvf4k.demo.domain.spi.FindByTransactionId
import fvf4k.demo.domain.spi.FindExpenseCategoriesByClientId


class CategorizedTransactionReadRepositoryAdapter(
    val jpaRepository: CategorizedTransactionJpaRepository
) : FindByTransactionId,
    FindByClientIdAndExpenseCategory,
    FindExpenseCategoriesByClientId {

    context(_: Raise<DatabaseQueryError>)
    override fun invoke(transactionId: TransactionId): CategorizedTransaction? =
        catch(
            block = {
                jpaRepository.findByTransactionId(transactionId)?.toDomain()
            },
            catch = { exception ->
                raise(
                    DatabaseQueryError(
                        "could not execute query by transaction id. " +
                                "id='$transactionId' cause: ${exception.message}"
                    )
                )
            }
        )

    context(_: Raise<DatabaseQueryError>)
    override fun invoke(
        clientId: ClientId,
        expenseCategory: ExpenseCategory
    ): List<CategorizedTransaction> =
        catch(
            block = {
                jpaRepository.findBudgetsByCategory(clientId, expenseCategory)
            },
            catch = { exception ->
                raise(
                    DatabaseQueryError(
                        "could not execute query by transaction id. id='$transactionId' cause: ${exception.message}"
                    )
                )
            }
        )

    context(_: Raise<DatabaseQueryError>)
    override fun invoke(clientId: ClientId): List<CategoryBudget> =
        catch(
            block = {
                jpaRepository.findBudgetsByCategory(clientId)
            },
            catch = { exception ->
                raise(
                    DatabaseQueryError(
                        "could not execute query by category for clientId, cause: ${exception.message}"
                    )
                )
            }
        )
}