package fvf4j.demo.controller;


import fvf4j.demo.domain.CategorizedTransaction.ExpenseCategory;
import fvf4j.demo.domain.CategorizedTransactionPorts.FindBudgetsByCategory;
import fvf4j.demo.domain.CategorizedTransactionPorts.FindByClientIdAndExpenseCategory;
import fvf4j.demo.domain.CategorizedTransactionPorts.FindExpenseCategoriesByClient;
import fvf4j.demo.domain.CategoryBudget;
import fvf4j.demo.domain.Transaction.ClientId;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategorizedTransactionController<R extends
        FindByClientIdAndExpenseCategory &
        FindBudgetsByCategory &
        FindExpenseCategoriesByClient> {

    private final R repository;

    public CategorizedTransactionController(R repository) {
        this.repository = repository;
    }

    @GetMapping("/client/{clientId}/transactions")
    public List<CategorizedTransactionResponse> getTransactionsByClientAndCategory(
            @PathVariable String clientId,
            @RequestParam String category) {
        return repository.findBy(new ClientId(clientId), new ExpenseCategory(category))
                .stream().map(CategorizedTransactionResponse::valueOf).toList();
    }

    @GetMapping("/client/{clientId}/categories-budget")
    public List<CategoryBudget> getBudgetByCategory(@PathVariable String clientId) {
        return repository.findBudgetsByCategory(new ClientId(clientId));
    }

    @GetMapping("/client/{clientId}/categories")
    public List<String> getDistinctExpenseCategoriesByClientId(@PathVariable String clientId) {
        return repository.findExpenseCategoriesBy(new ClientId(clientId))
                .stream().map(ExpenseCategory::value).toList();
    }
}
