package fun.vs.fw.demo.domain;


import fun.vs.fw.demo.domain.CategorizedTransaction.ExpenseCategory;
import fun.vs.fw.demo.domain.Transaction.MerchantCategoryCode;

@FunctionalInterface
public interface MerchantDirectory {
    ExpenseCategory getFor(MerchantCategoryCode code);
}
