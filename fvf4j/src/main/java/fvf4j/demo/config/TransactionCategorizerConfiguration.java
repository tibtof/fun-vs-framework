package fvf4j.demo.config;


import fvf4j.demo.domain.TransactionCategorizer;
import fvf4j.demo.jpa.CategorizedTransactionRepositoryAdapter;
import fvf4j.demo.jpa.TransactionalTransactionCategorizer;
import fvf4j.demo.merchantdirectory.MerchantDirectoryAdapter;
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
