package fvf4k.demo.infra.jpa

import arrow.core.NonEmptyList
import arrow.core.raise.RaiseDSL
import arrow.core.raise.context.Raise
import arrow.core.raise.context.RaiseAccumulate
import arrow.core.raise.context.raise
import arrow.core.raise.recover
import fvf4k.demo.domain.DataCorruptionError
import fvf4k.demo.domain.model.AccountId
import fvf4k.demo.domain.model.Amount
import fvf4k.demo.domain.model.CategorizedTransaction
import fvf4k.demo.domain.model.CategorizedTransactionId
import fvf4k.demo.domain.model.ClientId
import fvf4k.demo.domain.model.ExpenseCategory
import fvf4k.demo.domain.model.MerchantCategoryCode
import fvf4k.demo.domain.model.Transaction
import fvf4k.demo.domain.model.TransactionId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.*
import kotlin.experimental.ExperimentalTypeInference
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid
import arrow.core.raise.zipOrAccumulate as zipOrAccumulateExt


@Table(name = "categorized_transaction")
@Entity
data class CategorizedTransactionEntity(
    @Id val id: UUID,
    @Column(name = "transaction_id", nullable = false) val transactionId: UUID,
    @Column(name = "client_id", nullable = false) val clientId: String,
    @Column(name = "account_id", nullable = false) val accountId: String,
    @Column(name = "amount", nullable = false) val amount: BigDecimal,
    @Column(name = "mcc", nullable = false) val mcc: String,
    @Column(name = "expense_category", nullable = false) val expenseCategory: String
)

context(_: Raise<DataCorruptionError>)
fun CategorizedTransactionEntity.toDomain(): CategorizedTransaction =
    recover(
        block = {
            zipOrAccumulate(
                { TransactionId(transactionId.toKotlinUuid()) },
                { ClientId(clientId) },
                { AccountId(accountId) },
                { Amount(amount) },
                { MerchantCategoryCode(mcc) },
                { ExpenseCategory(expenseCategory) }
            ) { validTransactionId, validClientId, validAccountId, validAmount, validMcc, validExpenseCategory ->
                CategorizedTransaction(
                    id = CategorizedTransactionId(id.toKotlinUuid()),
                    transaction = Transaction(
                        id = validTransactionId,
                        clientId = validClientId,
                        accountId = validAccountId,
                        amount = validAmount,
                        mcc = validMcc
                    ),
                    expenseCategory = validExpenseCategory
                )
            }
        },
        recover = { errors ->
            raise(
                DataCorruptionError("Corrupted CategorizedTransaction database entry", errors)
            )
        })

fun CategorizedTransaction.toJpaEntity(): CategorizedTransactionEntity =
    CategorizedTransactionEntity(
        id = this.id.value.toJavaUuid(),
        transactionId = this.transaction.id.value.toJavaUuid(),
        clientId = this.transaction.clientId.value,
        accountId = this.transaction.accountId.value,
        amount = this.transaction.amount.value,
        mcc = this.transaction.mcc.value,
        expenseCategory = this.expenseCategory.value
    )

/***
 * Temporary implementation of a higher-arity zipOrAccumulate.
 * Created a [PR \#3778](https://github.com/arrow-kt/arrow/pull/3778) in arrow-kt.
 * Remove this after the PR is merged or an alternative is available.
 */
@OptIn(ExperimentalTypeInference::class)
context(raise: Raise<NonEmptyList<Error>>)
@RaiseDSL
inline fun <Error, A, B, C, D, E, F, G> zipOrAccumulate(
    @BuilderInference action1: context(RaiseAccumulate<Error>) () -> A,
    @BuilderInference action2: context(RaiseAccumulate<Error>) () -> B,
    @BuilderInference action3: context(RaiseAccumulate<Error>) () -> C,
    @BuilderInference action4: context(RaiseAccumulate<Error>) () -> D,
    @BuilderInference action5: context(RaiseAccumulate<Error>) () -> E,
    @BuilderInference action6: context(RaiseAccumulate<Error>) () -> F,
    block: (A, B, C, D, E, F) -> G
): G = raise.zipOrAccumulateExt(action1, action2, action3, action4, action5, action6, block)
