package fvf4k.demo.infra

import arrow.core.Nel
import arrow.core.raise.context.either
import fvf4k.demo.domain.failure.CategorizedTransactionCorrupted
import fvf4k.demo.domain.failure.InvalidMerchantCategoryPattern
import fvf4k.demo.domain.failure.NullOrEmpty
import fvf4k.demo.infra.jpa.CategorizedTransactionEntity
import fvf4k.demo.infra.jpa.toDomain
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import java.util.*
import kotlin.test.fail


class CategorizedTransactionEntitySpec : FreeSpec({

    "should accumulate data corruption failures when converting to domain model" {
        val entity = CategorizedTransactionEntity(
            id = UUID.randomUUID(),
            transactionId = UUID.randomUUID(),
            clientId = null,
            accountId = null,
            amount = 100.0.toBigDecimal(),
            currencyCode = "EUR",
            mcc = "123",
            expenseCategory = ""
        )

        val result = either {
            entity.toDomain()
        }

        result shouldBeLeft CategorizedTransactionCorrupted(
            Nel.of(
                NullOrEmpty(property = "clientId", value = null),
                NullOrEmpty(property = "accountId", value = null),
                InvalidMerchantCategoryPattern("123"),
                NullOrEmpty(property = "expenseCategory", value = "")
            )
        )
    }

    "should convert to domain model successfully when all data is valid" {
        val entity = CategorizedTransactionEntity(
            id = UUID.randomUUID(),
            transactionId = UUID.fromString("629847fd-edba-4888-933b-66c75f5d67ad"),
            clientId = UUID.randomUUID(),
            accountId = UUID.randomUUID(),
            amount = 250.75.toBigDecimal(),
            currencyCode = "EUR",
            mcc = "5411",
            expenseCategory = "Groceries"
        )

        val result = either {
            entity.toDomain()
        }

        val categorizedTransaction = result.shouldBeRight()

        categorizedTransaction.id.value shouldBe entity.id
        categorizedTransaction.transaction.id.value shouldBe entity.transactionId
        categorizedTransaction.transaction.clientId.value shouldBe entity.clientId
        categorizedTransaction.transaction.accountId.value shouldBe entity.accountId
        categorizedTransaction.transaction.money.value shouldBe entity.amount
        categorizedTransaction.transaction.mcc.value shouldBe entity.mcc
        categorizedTransaction.expenseCategory.value shouldBe entity.expenseCategory

//        629847fd-edba-4888-933b-66c75f5d67ad
//        2e228dde-c37e-458c-b969-c44a2450cde8
//        aa3a1fbe-2738-4f84-9585-6398f0ff6038
    }
})
