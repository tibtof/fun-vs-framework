package fvf4j.demo.domain;


import fvf4j.demo.domain.CategorizedTransactionPorts.FindByTransactionId;
import fvf4j.demo.domain.CategorizedTransactionPorts.SaveCategorizedTransaction;

public interface TransactionCategorizer {

    CategorizedTransaction categorize(Transaction transaction);

    static TransactionCategorizer create(SaveCategorizedTransaction saveCategorizedTransaction,
                                         FindByTransactionId findByTransactionId,
                                         MerchantDirectory merchantDirectory) {
        return new TransactionCategorizerService(saveCategorizedTransaction, findByTransactionId, merchantDirectory);
    }
}

final class TransactionCategorizerService implements TransactionCategorizer {

    private final SaveCategorizedTransaction saveCategorizedTransaction;
    private final FindByTransactionId findByTransactionId;
    private final MerchantDirectory merchantDirectory;

    TransactionCategorizerService(
            SaveCategorizedTransaction saveCategorizedTransaction,
            FindByTransactionId findByTransactionId,
            MerchantDirectory merchantDirectory) {
        this.saveCategorizedTransaction = saveCategorizedTransaction;
        this.findByTransactionId = findByTransactionId;
        this.merchantDirectory = merchantDirectory;
    }

    public CategorizedTransaction categorize(Transaction transaction) {
        var merchantInfo = merchantDirectory.getFor(transaction.mcc());
        var existingCategorizedTransaction = findByTransactionId.findBy(transaction.transactionId());

        var categorizedTransaction = existingCategorizedTransaction
                .map(ct -> transaction.toCategorizedTransaction(merchantInfo).withId(ct.id()))
                .orElseGet(() -> transaction.toCategorizedTransaction(merchantInfo));

        return saveCategorizedTransaction.save(categorizedTransaction);
    }
}