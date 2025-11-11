package fvf4k.demo.domain

import java.math.BigDecimal

data class CategorizedTransaction(
    val id: CategorizedTransactionId,
    val transactionId: TransactionId,
    val clientId: ClientId,
    val accountId: AccountId,
    val amount: Amount,
    val expenseCategory: ExpenseCategory
)

@JvmInline value class CategorizedTransactionId(val value: Long)
@JvmInline value class TransactionId(val value: String)
@JvmInline value class ClientId(val value: String)
@JvmInline value class AccountId(val value: String)
@JvmInline value class Amount(val value: BigDecimal)
@JvmInline value class ExpenseCategory(val value: String)
