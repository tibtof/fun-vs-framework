package fun.vs.fw.demo.controller;


import fun.vs.fw.demo.domain.CategorizedTransaction.ExpenseCategory;
import fun.vs.fw.demo.domain.CategorizedTransactionRepository;
import fun.vs.fw.demo.domain.CategoryBudget;
import fun.vs.fw.demo.domain.Transaction.ClientId;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategorizedTransactionController<R extends
        CategorizedTransactionRepository.FindByClientIdAndExpenseCategory &
        CategorizedTransactionRepository.FindBudgetsByCategory &
        CategorizedTransactionRepository.FindExpenseCategoriesByClient> {

    private final R repository;

    public CategorizedTransactionController(R repository) {
        this.repository = repository;
    }

    @GetMapping("/client/{clientId}/transactions")
    public List<CategorizedTransactionResponse> getTransactionsByClientAndCategory(@PathVariable String clientId,
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
