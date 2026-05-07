package fvf4k.demo.mock

import fvf4k.demo.infra.jpa.CategorizedTransactionEntity
import fvf4k.demo.infra.jpa.CategorizedTransactionJpaRepository
import fvf4k.demo.infra.kafka.TransactionMessage
import fvf4k.demo.infra.md.MerchantDirectoryService
import fvf4k.demo.infra.md.MerchantInfo
import io.kotest.core.spec.style.FreeSpec
import io.mockk.every
import io.mockk.mockk
import java.util.*

internal class TransactionCategorizerService(
    val repository: CategorizedTransactionJpaRepository,
    val merchantDirectoryService: MerchantDirectoryService
) {
    fun categorizeTransaction(message: TransactionMessage): MerchantInfo {
        val transaction = repository.findById(TODO())
        TODO()
    }
}

class MockkSample : FreeSpec({
    "!should categorize transaction correctly" {
        val message: TransactionMessage = mockk()
        val mcc = message.mcc!!
        val mockRepository = mockk<CategorizedTransactionJpaRepository>()
        val mockMerchantDirectoryService = mockk<MerchantDirectoryService>()
        val categorizedTransactionId = UUID.randomUUID()

        val transactionCategorizer = TransactionCategorizerService(mockRepository, mockMerchantDirectoryService)

        every { mockMerchantDirectoryService.getMerchantCategoryCode(message.mcc) }
            .answers { MerchantInfo(message.mcc, "Transportation") }
        every { mockRepository.findByTransactionId(UUID.fromString(message.transactionId)) }
            .answers { null }
        every { mockRepository.save(any()) }
            .answers {
                val ct = it.invocation.args[0] as CategorizedTransactionEntity
                ct.copy(id = categorizedTransactionId)
            }

        transactionCategorizer.categorizeTransaction(message)
    }
})