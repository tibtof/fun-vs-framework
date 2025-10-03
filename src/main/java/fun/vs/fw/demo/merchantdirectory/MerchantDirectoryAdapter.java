package fun.vs.fw.demo.merchantdirectory;


import fun.vs.fw.demo.domain.CategorizedTransaction.ExpenseCategory;
import fun.vs.fw.demo.domain.MerchantDirectory;
import fun.vs.fw.demo.domain.Transaction;

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
