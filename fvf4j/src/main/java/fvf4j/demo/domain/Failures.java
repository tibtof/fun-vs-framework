package fvf4j.demo.domain;

import fvf4j.demo.domain.TransactionCategorizationError.MerchantCategoryLookupFailed;
import fvf4j.demo.domain.TransactionCategorizationError.TransactionCategoryNotFound;

sealed interface TransactionCategorizationError permits
        TransactionCategoryNotFound,
        MerchantCategoryLookupFailed {

    record TransactionCategoryNotFound(String message) implements TransactionCategorizationError {}

    record MerchantCategoryLookupFailed(String message) implements TransactionCategorizationError {}
}
