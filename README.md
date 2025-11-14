[![Gradle Build](https://github.com/tibtof/fun-vs-framework/actions/workflows/gradle-build.yml/badge.svg)](https://github.com/tibtof/fun-vs-framework/actions/workflows/gradle-build.yml)

# Own Your Design: Functional Principles vs Framework-Driven Architecture

This repository contains the complete demo project for the talk **"Own Your Design: Functional Principles vs Framework-Driven Architecture"**, showcasing how to evolve from framework-driven layered architecture to functional hexagonal architecture.

**[📺 Watch the talk](https://www.youtube.com/watch?v=kqNDeq-DrVM)** | **[📊 View slides](https://github.com/tibtof/fun-vs-framework/tree/slides)**

## 🎯 Talk Summary

This talk explores how developers can better control complexity by owning their design choices instead of allowing frameworks to dictate architecture. It contrasts:

1. **Framework-driven development** (Spring Boot, annotations, dependency injection magic, implicit runtime behavior)
2. **Functional & explicit design** (domain modeling, typed errors, pure functions, deterministic flow, boundary-aware architecture)

### Core Messages
- **Frameworks are tools, not architecture** — Don't let Spring write your code
- **Own your domain model** — Rich types, explicit errors, enforced invariants
- **Hexagonal architecture** — Predictable, testable, and future-proof
- **Typed error handling** — Robustness through explicit failure modes
- **Functional patterns** — Regain control over complexity

## 📁 Project Structure

This repository contains **two parallel implementations** of the same domain:

### `/fvf4j` — Java 25 Implementation
Modern Java implementation using:
- **Java 25** with sealed interfaces, pattern matching, records
- **Spring Boot 3.5.6** for infrastructure
- **JUnit 5** + **Mockito** for testing
- **ArchUnit 1.4.1** for architecture validation
- Explicit Result types for error handling

### `/fvf4k` — Kotlin 2.2 Implementation
Functional Kotlin implementation using:
- **Kotlin 2.2.20** with context parameters
- **Arrow-kt 2.2.0** for functional programming (Raise, Either, effect handlers)
- **Kotest** for testing
- **Konsist 0.17.3** for architecture validation
- Spring Boot 3.5.6 for infrastructure
- kotlin-logging 7.0.3

Both implementations demonstrate the same architectural evolution using idiomatic patterns for each language.

## 🏗️ Architecture Comparison

### Branch Structure
- **`main`** — Refactored hexagonal architecture solution
- **[`layered`](https://github.com/tibtof/fun-vs-framework/tree/layered)** — Traditional framework-driven layered architecture
- **[`hexagonal`](https://github.com/tibtof/fun-vs-framework/tree/hexagonal)** — Clean hexagonal architecture
- **[PR #2](https://github.com/tibtof/fun-vs-framework/pull/2)** — Detailed diff showing the refactoring journey

### Hexagonal Architecture (Ports & Adapters)

```
domain/
  ├── model/          # Pure domain models (value objects, entities)
  │   ├── Transaction.kt
  │   └── CategorizedTransaction.kt
  ├── api/            # Inbound ports (use cases, queries)
  │   └── TransactionCategorizer.kt
  └── spi/            # Outbound ports (repository interfaces)
      └── CategorizedTransactionRepository.kt
controller/           # Inbound adapters (REST, messaging)
  └── CategorizedTransactionController.kt
infra/               # Outbound adapters (JPA, Kafka, HTTP)
  ├── jpa/           # Database adapters
  └── kafka/         # Event streaming adapters
```

**Key Principles**:
- Domain at the center, independent of frameworks
- Ports define contracts (interfaces)
- Adapters implement infrastructure concerns
- Dependencies point inward

## 🔑 Key Concepts Demonstrated

### 1. Typed Error Handling

**Kotlin with Arrow**:
```kotlin
context(Raise<DomainError>)
fun categorizeTransaction(transaction: Transaction): CategorizedTransaction {
    ensure(transaction.amount.isPositive()) { InvalidAmount }
    val category = merchantDirectory.findCategory(transaction.merchant)
        .bind() // Short-circuits on error
    // ...
}
```

**Java 25 with Sealed Interfaces**:
```java
sealed interface Result<T, E> permits Success, Failure {}

Result<CategorizedTransaction, DomainError> categorize(Transaction tx) {
    if (!tx.amount().isPositive()) {
        return new Failure<>(new InvalidAmount());
    }
    // ...
}
```

### 2. Domain Modeling
- Value objects with enforced invariants
- Make illegal states unrepresentable
- Rich domain types over primitives
- No framework dependencies

### 3. Dependency Inversion
- **Domain** depends on nothing
- **Application** depends only on domain
- **Infrastructure** depends on domain interfaces
- Framework used only at boundaries

### 4. Concurrency Design

**Kotlin Coroutines**:
```kotlin
parZip(
    { fetchMerchantInfo(merchantId) },
    { checkBudget(categoryId) }
) { merchant, budget -> /* combine */ }
```

**Java Virtual Threads**:
```java
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    var merchantTask = scope.fork(() -> fetchMerchant(id));
    var budgetTask = scope.fork(() -> checkBudget(categoryId));
    scope.join().throwIfFailed();
    // ...
}
```

## 🚀 Getting Started

### Prerequisites
- **JDK 25** (for latest Java features)
- **Gradle 8.x**
- **Docker** (for Testcontainers in tests)

### Build & Test

**Java implementation**:
```bash
./gradlew :fvf4j:build
./gradlew :fvf4j:test
```

**Kotlin implementation**:
```bash
./gradlew :fvf4k:build
./gradlew :fvf4k:test
```

**Architecture validation**:
```bash
# Java - ArchUnit tests
./gradlew :fvf4j:test --tests "*ArchTest"

# Kotlin - Konsist tests
./gradlew :fvf4k:test --tests "*ArchitectureTest"
```

## 🧪 Testing Strategy

### Domain Tests
Pure unit tests with no Spring context:
```kotlin
@Test
fun `should categorize transaction with known merchant`() {
    val transaction = Transaction(/*...*/)
    val result = categorizer.categorize(transaction)
    // Assert on domain behavior
}
```

### Architecture Tests
Enforce architectural rules automatically:
```kotlin
// Konsist (Kotlin)
@Test
fun `domain layer should not depend on Spring`() {
    Konsist.scopeFromProject()
        .classes()
        .withPackage("..domain..")
        .shouldNot {
            it.hasAnnotationOf<RestController>() ||
            it.hasAnnotationOf<Service>()
        }
}
```

```java
// ArchUnit (Java)
@Test
void domainShouldNotDependOnSpring() {
    noClasses()
        .that().resideInAPackage("..domain..")
        .should().dependOnClassesThat()
        .resideInAPackage("org.springframework..")
        .check(classes);
}
```

### Integration Tests
Use Testcontainers for real infrastructure:
- PostgreSQL for database
- Kafka for messaging
- WireMock for HTTP clients

## 📚 Domain Example: Transaction Categorization

The demo implements a **transaction categorization system**:

1. **Input**: Raw financial transaction arrives via Kafka
2. **Categorize**: Apply business rules using merchant directory
3. **Validate**: Check against category budgets
4. **Store**: Persist categorized transaction
5. **Query**: Expose REST endpoints for retrieval

**Demonstrates**:
- Event-driven architecture (Kafka listener adapter)
- Domain services with business rules
- Typed error handling throughout the flow
- Read/write repository separation (CQRS-lite)
- REST API adapter

## 🎓 Learning Resources

### For AI Assistants
See [`PROMPTS.md`](./PROMPTS.md) and the Markdown prompts under `prompts/` for detailed guidance on:
- Architecture patterns and conventions
- Code style requirements (Kotlin/Java)
- Testing strategies
- Common refactoring scenarios
- Request templates for new features

### Key Dependencies

| Technology | Java Version | Kotlin Version |
|------------|-------------|----------------|
| Language   | Java 25     | Kotlin 2.2.20  |
| Spring Boot | 3.5.6      | 3.5.6          |
| Testing    | JUnit 5, Mockito 5.20.0 | Kotest |
| Arch Tests | ArchUnit 1.4.1 | Konsist 0.17.3 |
| FP Library | Sealed interfaces + records | Arrow-kt 2.2.0 |
| Database   | PostgreSQL 42.7.5 | PostgreSQL 42.7.5 |
| Messaging  | Spring Kafka 3.2.2 | Spring Kafka |

## 🛠️ Development Guidelines

### What to Avoid ❌
- Framework annotations in domain layer
- Exceptions for control flow in domain
- Anemic domain models (just getters/setters)
- Direct database access from controllers
- Business logic in controllers or repositories

### What to Embrace ✅
- Pure functions where possible
- Explicit error types
- Value objects with validation
- Dependency inversion (ports/adapters)
- Deterministic, testable code
- Rich domain model

## 📖 Additional Context

To fully understand the motivation and design decisions, we recommend:
1. **[Watch the full talk](https://www.youtube.com/watch?v=kqNDeq-DrVM)** (conference presentation)
2. **Review the slides** (linked in talk description)
3. **Compare branches**: [layered](https://github.com/tibtof/fun-vs-framework/tree/layered) → [hexagonal](https://github.com/tibtof/fun-vs-framework/tree/hexagonal) → [main](https://github.com/tibtof/fun-vs-framework/tree/main)
4. **Read the [PR diff](https://github.com/tibtof/fun-vs-framework/pull/2)** to see the refactoring steps

## 🤝 Contributing

This is a demo project for educational purposes. Feel free to:
- Open issues for questions or discussions
- Submit PRs to improve examples
- Share your own refactoring experiences

## 📄 License

[Specify your license here]

---

**Remember**: Frameworks are accelerators, not architecture. Own your design.
