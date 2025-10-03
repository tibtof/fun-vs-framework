package fun.vs.fw.demo.config;


import fun.vs.fw.demo.domain.TransactionCategorizer;
import fun.vs.fw.demo.jpa.CategorizedTransactionRepositoryAdapter;
import fun.vs.fw.demo.jpa.TransactionalTransactionCategorizer;
import fun.vs.fw.demo.merchantdirectory.MerchantDirectoryAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransactionCategorizerConfiguration {

    @Bean
    public TransactionCategorizer transactionCategorizationService(
            CategorizedTransactionRepositoryAdapter repositoryAdapter,
            MerchantDirectoryAdapter merchantDirectoryAdapter) {
        return new TransactionalTransactionCategorizer(
                TransactionCategorizer.create(
                        repositoryAdapter,
                        repositoryAdapter,
                        merchantDirectoryAdapter
                )
        );
    }
}
