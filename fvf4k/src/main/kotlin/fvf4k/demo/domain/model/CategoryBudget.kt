package fvf4k.demo.domain.model

/**
 * Represents a budget summary for a specific expense category.
 * This immutable record is used to encapsulate the category name and the total amount
 * of financial transactions associated with that category.
 *
 *
 * It is primarily used as a projection in repository queries to aggregate
 * the total transaction amounts per category for a specific client.
 */
data class CategoryBudget(val category: ExpenseCategory, val totalMoney: Money)