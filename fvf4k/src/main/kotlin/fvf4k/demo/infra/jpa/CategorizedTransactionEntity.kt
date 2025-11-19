package fvf4k.demo.infra.jpa

import arrow.core.NonEmptyList
import arrow.core.raise.ExperimentalRaiseAccumulateApi
import arrow.core.raise.RaiseDSL
import arrow.core.raise.context.Raise
import arrow.core.raise.context.RaiseAccumulate
import arrow.core.raise.context.accumulate
import arrow.core.raise.context.accumulating
import arrow.core.raise.context.withError
import fvf4k.demo.domain.failure.CategorizedTransactionCorrupted
import fvf4k.demo.domain.model.AccountId
import fvf4k.demo.domain.model.CategorizedTransaction
import fvf4k.demo.domain.model.CategorizedTransactionId
import fvf4k.demo.domain.model.ClientId
import fvf4k.demo.domain.model.ExpenseCategory
import fvf4k.demo.domain.model.MerchantCategoryCode
import fvf4k.demo.domain.model.Money
import fvf4k.demo.domain.model.Transaction
import fvf4k.demo.domain.model.TransactionId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.*
import kotlin.experimental.ExperimentalTypeInference
import arrow.core.raise.zipOrAccumulate as zipOrAccumulateExt


@Table(name = "categorized_transaction")
@Entity
data class CategorizedTransactionEntity(
    @Id @GeneratedValue(strategy = GenerationType.UUID) val id: UUID,
    @Column(name = "transaction_id", nullable = false) val transactionId: UUID,
    @Column(name = "client_id", nullable = false) val clientId: String?,
    @Column(name = "account_id", nullable = false) val accountId: String?,
    @Column(name = "amount", nullable = false) val amount: BigDecimal?,
    @Column(name = "currencyCode", nullable = false) val currencyCode: String?,
    @Column(name = "mcc", nullable = false) val mcc: String?,
    @Column(name = "expense_category", nullable = false) val expenseCategory: String?
)

@OptIn(ExperimentalRaiseAccumulateApi::class)
context(_: Raise<CategorizedTransactionCorrupted>)
fun CategorizedTransactionEntity.toDomain(): CategorizedTransaction =
    withError(::CategorizedTransactionCorrupted) {
        accumulate {
            val validTransactionId = accumulating { TransactionId(transactionId) }
            val validClientId = accumulating { ClientId(clientId) }
            val validAccountId = accumulating { AccountId(accountId) }
            val validMoney = accumulating { Money(amount, currencyCode) }
            val validMcc = accumulating { MerchantCategoryCode(mcc) }
            val validExpenseCategory = accumulating { ExpenseCategory(expenseCategory) }

            CategorizedTransaction(
                id = CategorizedTransactionId(id),
                transaction = Transaction(
                    id = validTransactionId.value,
                    clientId = validClientId.value,
                    accountId = validAccountId.value,
                    money = validMoney.value,
                    mcc = validMcc.value
                ),
                expenseCategory = validExpenseCategory.value
            )
        }
    }

fun CategorizedTransaction.toJpaEntity(): CategorizedTransactionEntity =
    CategorizedTransactionEntity(
        id = this.id.value,
        transactionId = this.transaction.id.value,
        clientId = this.transaction.clientId.value,
        accountId = this.transaction.accountId.value,
        amount = this.transaction.money.value,
        currencyCode = this.transaction.money.currency.currencyCode,
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
