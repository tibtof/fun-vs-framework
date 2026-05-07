@file:OptIn(ExperimentalTypeInference::class)

package fvf4k.demo.infra.config

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.raise.Raise
import arrow.core.raise.either
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.withContext
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRED
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.DefaultTransactionDefinition
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.experimental.ExperimentalTypeInference


data class TransactionCoroutineContext(val transaction: TransactionStatus) :
    AbstractCoroutineContextElement(TransactionCoroutineContext) {
    companion object Key : CoroutineContext.Key<TransactionCoroutineContext>
}

suspend fun <Error, R> PlatformTransactionManager.inTransaction(
    mapException: (Throwable) -> Error,
    @BuilderInference block: suspend Raise<Error>.() -> R,
): Either<Error, R> {
    val existingTransaction = currentCoroutineContext()[TransactionCoroutineContext]?.transaction
    return if (existingTransaction != null) {
        either { block() }
    } else {
        val newTransaction = this.getTransaction(DefaultTransactionDefinition(PROPAGATION_REQUIRED))
        withContext(currentCoroutineContext() + TransactionCoroutineContext(newTransaction)) {
            when (val result = either { block() }) {
                is Either.Left -> {
                    rollback(newTransaction)
                    result
                }

                is Either.Right -> {
                    Either.catch { commit(newTransaction) }
                        .mapLeft { mapException(it) }
                        .flatMap { result }
                }
            }
        }
    }
}

suspend fun <Error, R> PlatformTransactionManager.inTransaction(
    context: CoroutineContext,
    mapException: (Throwable) -> Error,
    @BuilderInference block: suspend Raise<Error>.() -> R
): Either<Error, R> = withContext(context) {
    inTransaction(mapException, block)
}
