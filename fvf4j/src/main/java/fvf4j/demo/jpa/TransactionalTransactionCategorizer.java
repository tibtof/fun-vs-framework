package fvf4j.demo.jpa;


import fvf4j.demo.domain.CategorizedTransaction;
import fvf4j.demo.domain.Transaction;
import fvf4j.demo.domain.TransactionCategorizer;
import org.springframework.transaction.annotation.Transactional;


public class TransactionalTransactionCategorizer implements TransactionCategorizer {

    private final TransactionCategorizer delegate;

    public TransactionalTransactionCategorizer(TransactionCategorizer delegate) {
        this.delegate = delegate;
    }

    @Override
    @Transactional
    public CategorizedTransaction categorize(Transaction transaction) {
        return delegate.categorize(transaction);
    }
}
