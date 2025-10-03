package fun.vs.fw.demo.controller;


import fun.vs.fw.demo.domain.CategorizedTransaction;

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
