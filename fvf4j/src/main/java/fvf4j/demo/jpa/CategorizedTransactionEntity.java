package fvf4j.demo.jpa;

import fvf4j.demo.domain.CategorizedTransaction;
import fvf4j.demo.domain.CategorizedTransaction.CategorizedTransactionId;
import fvf4j.demo.domain.CategorizedTransaction.ExpenseCategory;
import fvf4j.demo.domain.Transaction.AccountId;
import fvf4j.demo.domain.Transaction.ClientId;
import fvf4j.demo.domain.Transaction.Amount;
import fvf4j.demo.domain.Transaction.TransactionId;
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
@Table(name = "categorized_transaction")
@Entity public class CategorizedTransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    @Column(name = "version")
    private Long version;

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

    public CategorizedTransactionEntity() {
    }

    public CategorizedTransactionEntity(Long id, String transactionId, String clientId, String accountId, BigDecimal amount, String expenseCategory) {
        this(transactionId, clientId, accountId, amount, expenseCategory);
        this.id = id;
    }

    public CategorizedTransactionEntity(String transactionId, String clientId, String accountId, BigDecimal amount, String expenseCategory) {
        this.transactionId = transactionId;
        this.clientId = clientId;
        this.accountId = accountId;
        this.amount = amount;
        this.expenseCategory = expenseCategory;
    }

    public static CategorizedTransactionEntity valueOf(CategorizedTransaction categorizedTransaction) {
        return new CategorizedTransactionEntity(
                categorizedTransaction.id().value(),
                categorizedTransaction.transactionId().value(),
                categorizedTransaction.clientId().value(),
                categorizedTransaction.accountId().value(),
                categorizedTransaction.amount().value(),
                categorizedTransaction.expenseCategory().value()
        );
    }

    public CategorizedTransaction toDomain() {
        return new CategorizedTransaction(
                new CategorizedTransactionId(id),
                new TransactionId(transactionId),
                new ClientId(clientId),
                new AccountId(accountId),
                new Amount(amount),
                new ExpenseCategory(expenseCategory)
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
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
