package fvf4j.demo.merchantdirectory;

/**
 * Represents information about a merchant, including the merchant category value (MCC)
 * and the associated category.
 * <p>
 * This record is used to encapsulate details about a specific merchant for categorization
 * purposes in applications such as transaction processing and analysis.
 * <p>
 * Fields:
 * - mcc: The Merchant Category Code (MCC), a four-digit number uniquely identifying the
 *   type of business or service provided by the merchant.
 * - category: A string representing the category associated with the merchant, often used
 *   to classify and analyze transactions.
 */
public record MerchantInfo(
        String mcc,
        String category) {}
