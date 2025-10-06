package fvf4j.demo.merchantdirectory;


import fvf4j.demo.domain.CategorizedTransaction.ExpenseCategory;
import fvf4j.demo.domain.MerchantDirectory;
import fvf4j.demo.domain.Transaction;

public class MerchantDirectoryAdapter implements MerchantDirectory {

    private final MerchantDirectoryService merchantDirectoryService;

    public MerchantDirectoryAdapter(MerchantDirectoryService merchantDirectoryService) {
        this.merchantDirectoryService = merchantDirectoryService;
    }

    @Override
    public ExpenseCategory getFor(Transaction.MerchantCategoryCode code) {
        final var merchantInfo = merchantDirectoryService.getCategoryForMerchant(code.value());
        return new ExpenseCategory(merchantInfo.category());
    }
}
