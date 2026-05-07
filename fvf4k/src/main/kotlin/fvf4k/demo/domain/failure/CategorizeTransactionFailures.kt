package fvf4k.demo.domain.failure

sealed interface CategorizeTransactionFailure : Failure

data class CategorizedTransactionUpdateFailed(
    override val message: String
) : CategorizeTransactionFailure

data class ExpenseCategoryResolutionFailed(
    override val message: String
) : CategorizeTransactionFailure

data class ExpenseCategoryMappingFailed(
    val failedValidation: ValidationFailure,
    override val message: String = "Validation failed while mapping expense category: ${failedValidation.message}"
) : CategorizeTransactionFailure
