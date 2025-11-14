package fvf4k.demo.infra.jpa

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
import java.util.*
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid


@Table(name = "categorized_transaction")
@Entity
data class CategorizedTransactionEntity(
    @Id val id: UUID,
    @Column(name = "transaction_id", nullable = false) val transactionId: TransactionId,
    @Column(name = "client_id", nullable = false) val clientId: ClientId,
    @Column(name = "account_id", nullable = false) val accountId: AccountId,
    @Column(name = "amount", nullable = false) val amount: Amount,
    @Column(name = "mcc", nullable = false) val mcc: MerchantCategoryCode,
    @Column(name = "expense_category", nullable = false) val expenseCategory: ExpenseCategory
)

fun CategorizedTransactionEntity.toDomain(): CategorizedTransaction =
    CategorizedTransaction(
        id = CategorizedTransactionId(this.id.toKotlinUuid()),
        transaction = Transaction(
            id = transactionId,
            clientId = clientId,
            accountId = accountId,
            amount = amount,
            mcc = mcc
        ),
        expenseCategory = expenseCategory
    )

fun CategorizedTransaction.toEntity(): CategorizedTransactionEntity =
    CategorizedTransactionEntity(
        id = this.id.value.toJavaUuid(),
        version = 0L,
        transactionId = this.transaction.id,
        clientId = this.transaction.clientId,
        accountId = this.transaction.accountId,
        amount = this.transaction.amount,
        mcc = this.transaction.mcc,
        expenseCategory = this.expenseCategory
    )
