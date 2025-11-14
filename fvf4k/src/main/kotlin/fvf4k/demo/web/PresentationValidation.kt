package fvf4k.demo.web

import arrow.core.Nel
import arrow.core.raise.context.Raise
import arrow.core.raise.context.raise
import arrow.core.raise.fold
import fvf4k.demo.domain.DatabaseQueryError
import fvf4k.demo.domain.ValidationError
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private val logger = LoggerFactory.getLogger("WebOperationFailure")

sealed interface WebOperationFailure {
    val httpStatus: HttpStatus
    val errorType: String
    val userMessage: String
}

data class InvalidInput(
    val errors: Nel<ValidationError>
) : WebOperationFailure {
    override val httpStatus = HttpStatus.BAD_REQUEST
    override val errorType = "VALIDATION_ERROR"
    override val userMessage = "The request contains invalid data"
}

data class ServiceUnavailable(
    val error: DatabaseQueryError
) : WebOperationFailure {
    override val httpStatus = HttpStatus.SERVICE_UNAVAILABLE
    override val errorType = "SERVICE_UNAVAILABLE"
    override val userMessage = "The service is temporarily unavailable. Please try again later."
}

data class ErrorResponse @OptIn(ExperimentalTime::class) constructor(
    val timestamp: String = Clock.System.now().toString(),
    val status: Int,
    val error: String,
    val type: String,
    val message: String,
    val details: List<String> = emptyList()
)

private fun WebOperationFailure.logError(requestPath: String? = null) {
    val pathInfo = requestPath?.let { " [path=$it]" } ?: ""

    when (this) {
        is InvalidInput -> {
            // Client errors = WARN level (not our fault)
            logger.error(
                "Validation error{}:\n{}",
                pathInfo,
                errors.map { "  - ${it.message}" }.joinToString("\n")
            )
        }

        is ServiceUnavailable -> {
            // Server errors = ERROR level (our fault)
            logger.error(
                "Service unavailable error{}: {} (type: {})",
                pathInfo,
                error.message,
                error::class.simpleName
            )
        }
    }
}

fun WebOperationFailure.toResponseEntity(): ResponseEntity<ErrorResponse> =
    when (this) {
        is InvalidInput ->
            ResponseEntity
                .status(httpStatus)
                .body(
                    ErrorResponse(
                        status = httpStatus.value(),
                        error = httpStatus.reasonPhrase,
                        type = errorType,
                        message = userMessage,
                        details = errors.map { it.message }
                    ))

        is ServiceUnavailable ->
            ResponseEntity
                .status(httpStatus)
                .body(
                    ErrorResponse(
                        status = httpStatus.value(),
                        error = httpStatus.reasonPhrase,
                        type = errorType,
                        message = userMessage,
                        details = listOf(error.message)
                    )
                )
    }

context(_: Raise<WebOperationFailure>)
fun <T> validateAndMapErrors(block: context(Raise<Nel<ValidationError>>) () -> T): T =
    fold(
        block = block,
        recover = { errors: Nel<ValidationError> ->
            raise(InvalidInput(errors))
        },
        transform = { it }
    )

context(_: Raise<WebOperationFailure>)
fun <T> queryAndMapErrors(block: context(Raise<DatabaseQueryError>) () -> T): T =
    fold(
        block = block,
        recover = { error: DatabaseQueryError ->
            raise(ServiceUnavailable(error))
        },
        transform = { it }
    )
