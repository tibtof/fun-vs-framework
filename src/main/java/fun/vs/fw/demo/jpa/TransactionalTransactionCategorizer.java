package fun.vs.fw.demo.jpa;


import fun.vs.fw.demo.domain.CategorizedTransaction;
import fun.vs.fw.demo.domain.Transaction;
import fun.vs.fw.demo.domain.TransactionCategorizer;
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
