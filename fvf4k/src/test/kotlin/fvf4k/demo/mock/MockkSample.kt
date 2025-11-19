package fvf4k.demo.mock

import fvf4k.demo.infra.jpa.CategorizedTransactionJpaRepository
import io.kotest.core.spec.style.FreeSpec
import io.mockk.mockk
import java.util.UUID

internal class TransactionCategorizerService(val repository: CategorizedTransactionJpaRepository) {
    fun categorizeTransaction(transactionId: UUID, category: String) {
        val transaction = repository.findById(transactionId)
        TODO()
    }
}

class MockkSample: FreeSpec({
  "should categorize transaction correctly" {
     val mockRepository = mockk<CategorizedTransactionJpaRepository>()


  }
})