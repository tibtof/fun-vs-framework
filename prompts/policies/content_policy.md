# Content Policy for Fun vs Framework Project

## Purpose

This document defines what code patterns, practices, and architectural approaches are **allowed** and **prohibited** when working on the "Fun vs Framework" demo project.

## Architectural Policies

### ✅ REQUIRED Patterns

#### 1. Hexagonal Architecture
**Policy**: ALL code must follow hexagonal (ports & adapters) architecture.

**Rules**:
- Domain layer (`domain/`) must be at the center
- Ports define boundaries (`domain/api/`, `domain/spi/`)
- Adapters implement ports (`controller/`, `infra/`)
- Dependencies flow **inward only**

**Enforcement**:
- ArchUnit tests (Java)
- Konsist tests (Kotlin)
- Code review

#### 2. Typed Error Handling
**Policy**: Domain logic must use explicit error types, not exceptions.

**Required Kotlin Pattern**:
```kotlin
context(Raise<DomainError>)
fun operation(): Result {
    ensure(condition) { DomainError.ValidationError("msg") }
    // or
    raise(DomainError.BusinessRuleViolation("msg"))
}
```

**Required Java Pattern**:
```java
sealed interface Result<T, E> permits Success, Failure {}

Result<T, DomainError> operation() {
    if (!condition) {
        return new Failure<>(new ValidationError("msg"));
    }
    return new Success<>(result);
}
```

**Exceptions allowed ONLY for**:
- Infrastructure failures (database connection, network)
- Programmer errors (IllegalArgumentException in constructors)

#### 3. Value Objects
**Policy**: Use value objects for domain primitives.

**Required Pattern (Kotlin)**:
```kotlin
@JvmInline
value class TransactionId(val value: UUID) {
    init {
        require(value != UUID.NIL) { "ID cannot be nil" }
    }
}
```

**Required Pattern (Java)**:
```java
record TransactionId(UUID value) {
    public TransactionId {
        if (value.equals(UUID.NIL)) {
            throw new IllegalArgumentException("ID cannot be nil");
        }
    }
}
```

**Prohibited**: Primitive obsession (naked String, UUID, BigDecimal in domain APIs)

#### 4. Immutability
**Policy**: Domain models must be immutable.

**Required**:
- No setters
- Final/val fields
- Defensive copies for collections
- Validation at construction

**Prohibited**:
- Mutable state in domain
- Anemic domain models (just getters/setters)

### ❌ PROHIBITED Patterns

#### 1. Framework Leakage into Domain
**Strict Rule**: NO framework annotations or imports in `domain/` package.

**Prohibited in domain/**:
```java
// ❌ NO
@Entity
@Service
@Component
@Autowired
import org.springframework.*
```

**Why**: Domain must be framework-independent, portable, and testable.

**Enforcement**: Architecture tests will fail if violated.

#### 2. Exception-Based Control Flow
**Prohibited in domain logic**:
```kotlin
// ❌ NO
fun categorize(tx: Transaction): CategorizedTransaction {
    if (!tx.isValid()) {
        throw ValidationException("Invalid transaction")
    }
}
```

**Required instead**:
```kotlin
// ✅ YES
context(Raise<DomainError>)
fun categorize(tx: Transaction): CategorizedTransaction {
    ensure(tx.isValid()) { DomainError.ValidationError("Invalid") }
}
```

#### 3. Anemic Domain Models
**Prohibited**:
```java
// ❌ NO
public class Transaction {
    private UUID id;
    private BigDecimal amount;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    // ... just getters/setters
}

// Business logic in separate "service"
@Service
public class TransactionService {
    public boolean isValid(Transaction tx) { /* ... */ }
}
```

**Required**:
```java
// ✅ YES
public record Transaction(
    TransactionId id,
    Amount amount,
    Merchant merchant
) {
    public Transaction {
        // validation at construction
        Objects.requireNonNull(id);
        Objects.requireNonNull(amount);
    }

    // behavior as methods
    public boolean isValid() {
        return amount.isPositive();
    }
}
```

#### 4. Direct Database Access from Controllers
**Prohibited**:
```kotlin
// ❌ NO
@RestController
class TransactionController(
    private val jpaRepository: JpaRepository<TransactionEntity, UUID>
) {
    @GetMapping
    fun getAll() = jpaRepository.findAll()
}
```

**Required**:
```kotlin
// ✅ YES
@RestController
class TransactionController(
    private val queryService: TransactionQueryService // domain port
) {
    @GetMapping
    fun getAll() = queryService.findAll()
        .fold(
            { error -> error.toResponse() },
            { results -> ResponseEntity.ok(results.map { it.toDto() }) }
        )
}
```

#### 5. Business Logic in Controllers or Repositories
**Prohibited**: Controllers and repositories must be thin adapters only.

**Controllers should only**:
- Map DTOs to domain types
- Call domain ports
- Map domain results to HTTP responses

**Repositories should only**:
- Map domain types to persistence format
- Execute queries
- Map persistence format to domain types

## Code Quality Policies

### Naming Conventions

**Packages**:
```
fvf4j.demo.domain.model
fvf4j.demo.domain.api
fvf4j.demo.domain.spi
fvf4j.demo.controller
fvf4j.demo.infra.jpa
fvf4j.demo.infra.kafka
```

**Classes**:
- Domain models: Nouns (e.g., `Transaction`, `CategoryBudget`)
- Value objects: Domain-specific names (e.g., `TransactionId`, `Amount`)
- Ports (inbound): Verb-based (e.g., `TransactionCategorizer`, `BudgetValidator`)
- Ports (outbound): Repository/Service suffix (e.g., `TransactionRepository`, `MerchantDirectory`)
- Controllers: Feature + `Controller` (e.g., `TransactionController`)
- Adapters: Implementation + Adapter suffix (e.g., `TransactionJpaAdapter`)

**Error Types**:
```kotlin
// Kotlin
sealed interface DomainError {
    data class ValidationError(val message: String) : DomainError
    data class BusinessRuleViolation(val rule: String) : DomainError
    data object NotFound : DomainError
}
```

```java
// Java
sealed interface DomainError permits ValidationError, BusinessRuleViolation, NotFound {}
record ValidationError(String message) implements DomainError {}
record BusinessRuleViolation(String rule) implements DomainError {}
record NotFound() implements DomainError {}
```

### Testing Requirements

**Every feature MUST include**:

1. **Domain Tests** (pure unit tests)
   - Test value object validation
   - Test business rules
   - No mocks, no framework

2. **Use Case Tests** (orchestration)
   - Mock ports (repositories, services)
   - Test happy path and error cases
   - Verify error handling

3. **Architecture Tests** (boundary enforcement)
   - No Spring in domain
   - Dependency direction
   - Package naming conventions

4. **Integration Tests** (end-to-end)
   - Real infrastructure via Testcontainers
   - Test full adapter flow
   - Verify database/Kafka integration

**Test Coverage Target**: 80% for domain layer (enforced)

### Documentation Requirements

**JavaDoc/KDoc required for**:
- Public domain APIs (ports)
- Complex business rules
- Error conditions

**Not required for**:
- Self-explanatory code
- Tests
- Private implementation details

## Technology Constraints

### Allowed Dependencies in Domain

**Kotlin**:
```kotlin
// ✅ Allowed
import arrow.core.*
import arrow.core.raise.*
import kotlin.uuid.*
import java.math.BigDecimal
import java.time.*
```

**Java**:
```java
// ✅ Allowed
import java.util.*;
import java.time.*;
import java.math.*;
// No external libraries in Java domain
```

### Prohibited Dependencies in Domain

**Both languages**:
```
❌ org.springframework.*
❌ jakarta.persistence.*
❌ com.fasterxml.jackson.*
❌ org.hibernate.*
```

### Adapter Layer Dependencies

**Allowed only in `controller/` and `infra/`**:
- Spring Framework (all modules)
- Jackson (JSON serialization)
- JPA/Hibernate
- Kafka clients
- HTTP clients (OpenFeign)
- Testcontainers (tests only)

## Concurrency Policy

### Kotlin (Coroutines)
**Policy**: Use structured concurrency with Arrow-kt.

**Allowed**:
```kotlin
parZip(
    { suspendingCall1() },
    { suspendingCall2() }
) { result1, result2 -> combine(result1, result2) }
```

**Required**:
- `suspend` functions for async operations
- Structured concurrency (no GlobalScope)
- Explicit error handling with Raise

### Java (Virtual Threads)
**Policy**: Use structured concurrency with virtual threads.

**Allowed**:
```java
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    var future1 = scope.fork(() -> blockingCall1());
    var future2 = scope.fork(() -> blockingCall2());
    scope.join().throwIfFailed();
    return combine(future1.get(), future2.get());
}
```

**Prohibited**:
- Traditional thread pools in domain
- CompletableFuture in domain (infrastructure only)

## Performance Policy

**Premature optimization is prohibited**. Focus on:
1. Correct architecture
2. Explicit semantics
3. Testability

**Optimize only when**:
- Benchmarks show actual bottleneck
- User story requires specific performance
- Trade-offs are documented

## Backward Compatibility

**Breaking Changes Policy**: This is a demo project, breaking changes are acceptable.

**However**:
- Document migration path
- Update all implementations (Java + Kotlin)
- Update tests
- Update README if API changes

## Review Checklist

Before approving code, verify:

- [ ] Hexagonal architecture followed
- [ ] No framework imports in domain/
- [ ] Typed errors (no exceptions in domain)
- [ ] Value objects used (no primitives)
- [ ] Immutable domain models
- [ ] Tests at all layers
- [ ] Architecture tests pass
- [ ] Both Java and Kotlin consistent
- [ ] Naming conventions followed
- [ ] Documentation updated

## Enforcement

**Automated**:
- Architecture tests (ArchUnit, Konsist)
- Build fails on violation

**Manual**:
- Code review
- Pair programming

## Exceptions to Policy

**Rare cases** where policy can be bent:
1. **Quick experiments**: Clearly marked as WIP
2. **Performance critical path**: Documented with benchmarks
3. **Third-party integration**: Isolated in adapter layer

**Process**:
- Document reason in commit message
- Create TODO to refactor
- Get explicit approval

---

**Remember**: These policies exist to demonstrate principles, not to be dogmatic. The goal is to show how architectural discipline leads to better code.
