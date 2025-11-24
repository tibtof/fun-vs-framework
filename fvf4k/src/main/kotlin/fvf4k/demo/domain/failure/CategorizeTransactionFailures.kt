package fvf4k.demo.domain.failure

sealed interface CategorizeTransactionFailure : Failure

data class CategorizedTransactionUpdateError(
    override val message: String
) : CategorizeTransactionFailure

data class ExpenseCategoryResolutionFailed(
    override val message: String
) : CategorizeTransactionFailure

data class ExpenseCategoryMappingFailed(
    val failedValidation: ValidationFailed,
    override val message: String = "Validation failed while mapping expense category: ${failedValidation.message}"
) : CategorizeTransactionFailure
