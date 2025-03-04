package fun.vs.fw.demo.service;


import fun.vs.fw.demo.repository.CategorizedTransaction;
import fun.vs.fw.demo.repository.CategorizedTransactionRepository;
import fun.vs.fw.demo.messages.TransactionMessage;
import org.springframework.stereotype.Service;

@Service
public class TransactionCategorizationService {

    private final CategorizedTransactionRepository repository;
    private final MerchantDirectoryService merchantDirectoryService;

    public TransactionCategorizationService(CategorizedTransactionRepository repository,
                                            MerchantDirectoryService merchantDirectoryService) {
        this.repository = repository;
        this.merchantDirectoryService = merchantDirectoryService;
    }

    public CategorizedTransaction categorizeTransaction(TransactionMessage transaction) {
        var merchantInfo = merchantDirectoryService.getCategoryForMerchant(transaction.mcc());
        var categorizedTransaction = repository.findByTransactionId(transaction.transactionId());

        var cat = categorizedTransaction.map(ct ->
                new CategorizedTransaction(
                        ct.getId(),
                        transaction.transactionId(),
                        transaction.clientId(),
                        transaction.accountId(),
                        transaction.amount(),
                        merchantInfo.category()
                )
        ).orElseGet(() -> new CategorizedTransaction(
                transaction.transactionId(),
                transaction.clientId(),
                transaction.accountId(),
                transaction.amount(),
                merchantInfo.category()
        ));

        return repository.save(cat);
    }
}
