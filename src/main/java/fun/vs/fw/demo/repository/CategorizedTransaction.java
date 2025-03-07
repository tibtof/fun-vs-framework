package fun.vs.fw.demo.repository;

import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * Represents a categorized financial transaction associated with a specific client and account.
 * This entity includes attributes such as the transaction ID, client ID, account ID, transaction
 * amount, and the expense category to which the transaction belongs.
 * <p>
 * This class is marked as a JPA entity and mapped to the "categorized_transaction" table
 * in the database.
 */
@Entity
@Table(name = "categorized_transaction")
public class CategorizedTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_id", unique = true, nullable = false)
    private String transactionId;

    @Column(name = "client_id", nullable = false)
    private String clientId;

    @Column(name = "account_id", nullable = false)
    private String accountId;

    @Column(name = "amount", nullable = false, precision = 2)
    private BigDecimal amount;

    @Column(name = "expense_category", nullable = false)
    private String expenseCategory;

    public CategorizedTransaction() {
    }

    public CategorizedTransaction(Long id, String transactionId, String clientId, String accountId, BigDecimal amount, String expenseCategory) {
        this(transactionId, clientId, accountId, amount, expenseCategory);
        this.id = id;
    }

    public CategorizedTransaction(String transactionId, String clientId, String accountId, BigDecimal amount, String expenseCategory) {
        this.transactionId = transactionId;
        this.clientId = clientId;
        this.accountId = accountId;
        this.amount = amount;
        this.expenseCategory = expenseCategory;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getExpenseCategory() {
        return expenseCategory;
    }

    public void setExpenseCategory(String expenseCategory) {
        this.expenseCategory = expenseCategory;
    }
}
