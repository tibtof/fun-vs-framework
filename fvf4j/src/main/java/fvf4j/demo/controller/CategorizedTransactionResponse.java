package fvf4j.demo.controller;


import fvf4j.demo.domain.CategorizedTransaction;

public record CategorizedTransactionResponse(
        String transactionId,
        String expenseCategory) {

    public static CategorizedTransactionResponse valueOf(CategorizedTransaction categorizedTransaction) {
        return new CategorizedTransactionResponse(
                categorizedTransaction.transactionId().value(),
                categorizedTransaction.expenseCategory().value()
        );
    }
}
