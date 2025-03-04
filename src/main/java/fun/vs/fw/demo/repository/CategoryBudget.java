package fun.vs.fw.demo.repository;


import java.math.BigDecimal;

/**
 * Represents a budget summary for a specific expense category.
 * This immutable record is used to encapsulate the category name and the total amount
 * of financial transactions associated with that category.
 * <p>
 * It is primarily utilized as a projection in repository queries to aggregate
 * the total transaction amounts per category for a specific client.
 */
public record CategoryBudget(String category, BigDecimal totalAmount) {}
