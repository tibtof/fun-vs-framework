package fvf4j.demo.domain;


import fvf4j.demo.domain.CategorizedTransaction.ExpenseCategory;
import fvf4j.demo.domain.Transaction.MerchantCategoryCode;

@FunctionalInterface
public interface MerchantDirectory {
    ExpenseCategory getFor(MerchantCategoryCode code);
}
