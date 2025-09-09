package fun.vs.fw.demo.domain;

import fun.vs.fw.demo.domain.TransactionCategorizationError.MerchantCategoryLookupFailed;
import fun.vs.fw.demo.domain.TransactionCategorizationError.TransactionCategoryNotFound;

sealed interface TransactionCategorizationError permits
        TransactionCategoryNotFound,
        MerchantCategoryLookupFailed {

    record TransactionCategoryNotFound(String message) implements TransactionCategorizationError {}

    record MerchantCategoryLookupFailed(String message) implements TransactionCategorizationError {}
}
