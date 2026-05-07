# Agent Guidelines for Fun vs Framework Project

This document provides concise, opinionated guidance for planning and executing changes while enforcing the project’s architectural principles. It replaces the previous `agent_planner.md`.

## Role

You are a planning and execution guide for features, refactors, and fixes in this repository. Your plans must respect hexagonal architecture, functional programming, and typed error handling. Keep the domain pure. Frameworks live only at the edges.

## Planning Principles

1. Inside‑Out Development
   - Domain Model → Ports → Use Cases → Adapters → Tests
   - Never start from controllers or infrastructure.

2. Dual Implementation Strategy
   - Implement in both languages using idioms:
     - Java 25: sealed interfaces, records, explicit Result/Either
     - Kotlin 2.2: Arrow‑kt, context receivers, value classes

3. Test‑First Mindset
   - Plan tests per layer:
     - Domain tests (pure, no framework)
     - Use case tests (mock ports)
     - Architecture tests (enforce rules)
     - Integration tests (real infra via Testcontainers)

## Step‑By‑Step Planning Template

Use this template to produce plans before writing code.

### Step 1: Domain Analysis
- Identify core domain concepts and invariants
- Define value objects and entities
- Specify business rules and errors

Output:
- Types in `domain/model/`
- Validation rules and invariants
- Error hierarchy updates

### Step 2: Port Definition
- Inbound ports (use cases) in `domain/api/`
- Outbound ports (repositories/clients) in `domain/spi/`
- Enumerate error cases

Output:
- Interfaces for `domain/api/` and `domain/spi/`
- Error types (Kotlin sealed hierarchy / Java sealed interfaces)

### Step 3: Use Case Implementation
- Orchestration logic
- Typed error handling strategy
- Happy path and edge cases

Output:
- Domain services implementing ports
- Pure functions where possible

### Step 4: Adapter Implementation
- Input format (REST, Kafka, etc.) and DTO mapping
- Infra components (JPA, HTTP clients)
- Map domain errors to adapter responses

Output:
- Controllers in `controller/` (inbound adapters)
- Repositories/clients in `infra/` (outbound adapters)
- DTOs and mappers

### Step 5: Testing Strategy
- Domain tests for each model rule
- Use case tests (ports mocked)
- Architecture tests (dependencies and annotations)
- Integration tests (Testcontainers, WireMock when needed)

## Example Plan Skeleton

```markdown
# Feature: [Feature Name]

## Domain Model Changes
- [ ] Kotlin (`fvf4k/domain/model/`): create/update types
- [ ] Java   (`fvf4j/domain/...`): create/update types
- [ ] Error types updated in Kotlin `Errors.kt` / Java `Failures.java`

## Port Definitions
- [ ] Inbound port in Kotlin `domain/api/`
- [ ] Inbound port in Java   `domain/.../api/`
- [ ] Outbound port(s) in Kotlin `domain/spi/`
- [ ] Outbound port(s) in Java   `domain/.../spi/`

## Use Case Implementation
- [ ] Kotlin service with Arrow‑kt errors
- [ ] Java service with explicit Result/Either

## Adapters
- [ ] Controller(s) and DTO mapping
- [ ] Repository/client adapters

## Tests
- [ ] Domain tests
- [ ] Use case tests (ports mocked)
- [ ] Architecture tests (Konsist/ArchUnit)
- [ ] Integration tests (Testcontainers)
```

## When to Use This Guide
- Before implementing new features
- When refactoring and needing a crisp plan
- When aligning dual implementations (Java + Kotlin)

## See Also
- `prompts/system/chat_base.md` — personality, rules, and quick references
- `prompts/policies/content_policy.md` — allowed/prohibited patterns and checklists
