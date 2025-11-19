package fvf4k.demo.infra.kafka

import arrow.core.raise.context.Raise
import fvf4k.demo.domain.failure.Failure
import fvf4k.demo.domain.failure.ValidationFailed
import fvf4k.demo.domain.model.AccountId
import fvf4k.demo.domain.model.ClientId
import fvf4k.demo.domain.model.MerchantCategoryCode
import fvf4k.demo.domain.model.Money
import fvf4k.demo.domain.model.Transaction
import fvf4k.demo.domain.model.TransactionId
import java.math.BigDecimal


/** Represents a transaction message containing details about a specific transaction.
 *
 * Instances of this record are typically used to encapsulate transaction data for
 * processing, categorization, or transmission across systems, such as receiving messages
 * from messaging platforms like Kafka.
 *
 * Fields include:
 * - transactionId: A unique identifier for the transaction.
 * - accountId: The ID of the account associated with the transaction.
 * - clientId: The ID of the client performing the transaction.
 * - mcc: The Merchant Category Code representing the type of business where the transaction occurred.
 * - amount: The monetary value of the transaction. */
data class TransactionMessage(
    val transactionId: String?,
    val accountId: String?,
    val clientId: String?,
    val mcc: String?,
    val amount: BigDecimal?,
    val currencyCode: String?
) {
    context(_: Raise<Failure>)
    fun toDomain(): Transaction = Transaction(
        TransactionId(transactionId),
        ClientId(clientId),
        AccountId(accountId),
        Money(amount, currencyCode),
        MerchantCategoryCode(mcc)
    )
}
