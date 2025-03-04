package fun.vs.fw.demo.controller;


import fun.vs.fw.demo.repository.CategorizedTransaction;
import fun.vs.fw.demo.repository.CategorizedTransactionRepository;
import fun.vs.fw.demo.repository.CategoryBudget;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategorizedTransactionController {

    private final CategorizedTransactionRepository repository;

    public CategorizedTransactionController(CategorizedTransactionRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/client/{clientId}/transactions")
    public List<CategorizedTransaction> getTransactionsByClientAndCategory(@PathVariable String clientId,
                                                                           @RequestParam String category) {
        return repository.findByClientIdAndExpenseCategory(clientId, category);
    }

    @GetMapping("/client/{clientId}/categories-budget")
    public List<CategoryBudget> getBudgetByCategory(@PathVariable String clientId) {
        return repository.findBudgetsByCategory(clientId);
    }

    @GetMapping("/client/{clientId}/categories")
    public List<String> getDistinctExpenseCategoriesByClientId(@PathVariable String clientId) {
        return repository.findDistinctExpenseCategoriesByClientId(clientId);
    }
}
