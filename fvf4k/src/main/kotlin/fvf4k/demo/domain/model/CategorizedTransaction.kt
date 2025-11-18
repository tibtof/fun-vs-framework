package fvf4k.demo.domain.model

import arrow.core.raise.context.Raise
import arrow.core.raise.context.raise
import com.github.f4b6a3.uuid.UuidCreator
import fvf4k.demo.domain.failure.NullOrEmpty
import fvf4k.demo.domain.failure.ValidationFailed
import java.util.*

data class CategorizedTransaction(
    val id: CategorizedTransactionId,
    val transaction: Transaction,
    val expenseCategory: ExpenseCategory
)

@JvmInline value class CategorizedTransactionId(val value: UUID) {
    companion object {
        /**
         * Generates a new [CategorizedTransactionId] using a time‑ordered (epoch‑based) UUID
         * produced by `UuidCreator.getTimeOrderedEpoch()`.
         *
         * Rationale:
         * - Improves clustered index locality in relational DBs: inserts land near the end of the B‑tree
         *   instead of scattering pages like random UUIDv4, reducing page splits and random I/O.
         * - Increases write throughput and reduces write amplification on hot tables.
         * - Preserves natural time ordering, enabling efficient key‑range scans and pagination by creation time.
         * - Retains native UUID column types (e.g., PostgreSQL `uuid`) without resorting to string ULIDs.
         *
         */
        fun generate(): CategorizedTransactionId = CategorizedTransactionId(UuidCreator.getTimeOrderedEpoch())
    }
}

@JvmInline value class ExpenseCategory private constructor(val value: String) {
    companion object {
        context(_: Raise<ValidationFailed>)
        operator fun invoke(value: String?): ExpenseCategory {
//            println(MerchantDirectoryConfiguration())
            if (value == null || value.isEmpty()) raise(NullOrEmpty("expenseCategory", value))
            else return ExpenseCategory(value)
        }
    }
}

