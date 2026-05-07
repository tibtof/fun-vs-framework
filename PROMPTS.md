# AI Assistant Prompts — Minimal, Opinionated Setup

This document describes the simplified prompt setup for the "Fun vs Framework" project. It collapses prior scattered configurations into a single source of truth plus one optional planning guide, and a single policy file. This works across IntelliJ AI, Junie, Claude, Copilot, and VS Code.

## 📁 Minimal Structure

```
prompts/
├── system/
│   ├── chat_base.md          # Main personality + architecture rules (single source of truth)
│   └── agent_guidelines.md   # Optional: planning rules and templates
└── policies/
    └── content_policy.md     # Safety, code style, architecture policy

PROMPTS.md                    # How to plug into tools
```

Removed legacy prompt files (cleaned up after migration):
- `agents/` (e.g., `agents/main_agent.yml`)
- `prompt_specs/` (e.g., `prompt_specs/chat_main.yml`)
- `AI_PROMPT_STRUCTURE.md`

## 🎯 Goals

- One place for instructions: `prompts/system/*.md`
- One place for constraints: `prompts/policies/content_policy.md`
- One place for tool integration: `PROMPTS.md`

## 📄 Files

### `prompts/system/chat_base.md`

Foundational system prompt defining identity, core knowledge, and strict rules.

Contains:
- Identity and expertise areas
- Core philosophy (frameworks are tools, not architecture)
- Architecture rules and dependency directions
- Domain modeling and error handling rules
- Code generation guidelines and quick references
- Testing strategies

Use: Load this as the base system prompt in all tools.

### `prompts/system/agent_guidelines.md` (optional)

Concise planning guide for creating step‑by‑step plans before implementation.

Contains:
- Inside‑out development flow
- Dual implementation strategy (Java + Kotlin)
- Planning template and example skeleton
- Testing strategy per layer

Use: When you need a structured implementation plan or during refactors.

### `prompts/policies/content_policy.md`

Constraints and checklists that are enforced during generation and review.

Contains:
- Required patterns (hexagonal, typed errors, value objects, immutability)
- Prohibited patterns (framework in domain, exceptions for control flow, anemic models)
- Code style, naming, and testing policies
- Allowed dependencies per layer; concurrency policies
- Review checklist

Use: As an additional system/reference prompt to keep code compliant.

## 🔌 How to use with each tool

Below are minimal steps to point tools at the unified prompts.

### IntelliJ AI Assistant
- System prompt: load `prompts/system/chat_base.md`
- Additional context: add `prompts/policies/content_policy.md`
- For planning: optionally include `prompts/system/agent_guidelines.md`

### Junie (JetBrains autonomous programmer)
- Base prompt: `prompts/system/chat_base.md`
- Policy: `prompts/policies/content_policy.md`
- Planning (optional): `prompts/system/agent_guidelines.md`

### Claude (Custom instructions / Projects)
- Paste contents of `prompts/system/chat_base.md` into system instructions
- Add `prompts/policies/content_policy.md` as a second system message
- Keep `agent_guidelines.md` handy for plan requests

### GitHub Copilot / Copilot Chat
- Use the “Custom Instructions” or workspace README to reference:
  - `prompts/system/chat_base.md`
  - `prompts/policies/content_policy.md`
  - Optionally `prompts/system/agent_guidelines.md`

### VS Code + Extensions
- For prompt‑aware extensions, point to the same three files above
- Consider a workspace note linking to `PROMPTS.md`

## 🗑️ Cleanups

The legacy files have been removed to keep the structure clean:

1. `prompts/system/agent_planner.md` → Replaced by `prompts/system/agent_guidelines.md`
2. `prompt_specs/chat_main.yml` → Replaced by Markdown prompts directly
3. `agents/main_agent.yml` → Replaced by Markdown prompts directly
4. `AI_PROMPT_STRUCTURE.md` → Content covered by this document and the Markdown prompts

If you still have local tooling pointing to these old paths, update them to the new locations above.

## ✅ Migration Checklist

- [ ] Update your tool’s system prompt to `prompts/system/chat_base.md`
- [ ] Add `prompts/policies/content_policy.md` as policy/secondary prompt
- [ ] Optionally include `prompts/system/agent_guidelines.md` for planning
- [ ] Remove any references to `agents/` and `prompt_specs/`
- [ ] Delete legacy files when all consumers are updated

## FAQ

Q: Why remove YAML agent specs?
A: They duplicated information and drifted from the single source of truth. Markdown prompts are human‑centric and portable across tools.

Q: Can we keep both for a while?
A: Yes. They’re marked as deprecated here; once you’re confident, delete them.

### About the old YAML files
We previously used YAML specs like `agents/main_agent.yml` and `prompt_specs/chat_main.yml`. These have been removed in favor of the portable Markdown prompts in `prompts/`. Use this `PROMPTS.md` plus the Markdown files under `prompts/` as the single source of truth.

## 🚀 How to Use These Prompts

### For Human Users

#### Option 1: Direct Copy-Paste
Copy the content of `prompts/system/chat_base.md` and paste it as a system prompt when starting a conversation with an AI assistant.

#### Option 2: Reference in Conversation
```
"Please follow the guidelines in prompts/system/chat_base.md
and prompts/policies/content_policy.md when helping me implement [feature]."
```

#### Option 3: Use Planning Guide
```
"Use the planning template from prompts/system/agent_guidelines.md
to help me plan the implementation of [feature]."
```

### For AI Assistants

#### At Conversation Start
Load the following prompts:
1. `prompts/system/chat_base.md` (always)
2. `prompts/policies/content_policy.md` (always)
3. `prompts/system/agent_guidelines.md` (when planning is needed)

#### During Feature Implementation
Follow this workflow:

1. **Understand requirements**
   - Ask clarifying questions
   - Identify domain concepts
   - Determine error scenarios

2. **Plan implementation**
   - Use `agent_guidelines.md` template
   - Create step-by-step plan
   - Consider both Java and Kotlin

3. **Generate code**
   - Start with domain models
   - Define ports
   - Implement use cases
   - Add adapters

4. **Validate against policies**
   - Check `content_policy.md`
   - Ensure no violations
   - Add architecture tests

5. **Review and refine**
  - Use code review checklist from `prompts/policies/content_policy.md`
  - Verify test coverage
  - Ensure documentation

### For Tool Integration

#### Claude, ChatGPT, or Similar
1. Create a custom instruction set with content from `chat_base.md`
2. Use `content_policy.md` as a code review guide
3. Optionally load `agent_guidelines.md` for planning workflows

#### Cursor, Copilot, or IDE Assistants
1. Add `.cursorrules` or `.github/copilot-instructions.md` with key rules from `content_policy.md`
2. Reference project structure and examples from `chat_base.md`

#### Autonomous Agents
Use the same Markdown prompts directly (no YAML configs):
1. Load `prompts/system/chat_base.md` as the base system prompt
2. Add `prompts/policies/content_policy.md` as enforcement policy
3. Optionally include `prompts/system/agent_guidelines.md` for planning workflows

## 📚 Quick Reference

### Common Request Patterns

#### "Add a new feature"
```
1. Load: agent_guidelines.md
2. Create: Implementation plan
3. Follow: Feature implementation workflow
4. Check: Content policy compliance
5. Verify: Architecture tests pass
```

#### "Refactor this code"
```
1. Check: Current code against content_policy.md
2. Identify: Violations (framework coupling, anemic models, etc.)
3. Plan: Refactoring steps using agent_guidelines.md
4. Execute: Inside-out refactoring
5. Validate: Architecture tests
```

#### "Review this code"
```
1. Use: Code review checklist from prompts/policies/content_policy.md
2. Check: Layer boundaries
3. Verify: Error handling
4. Validate: Immutability
5. Confirm: Tests exist
```

### Key Architectural Rules

From `chat_base.md` and `content_policy.md`:

✅ **Always**:
- Domain depends on nothing
- Use typed errors (Raise/Either in Kotlin, Result in Java)
- Value objects for primitives
- Immutable domain models
- Architecture tests

❌ **Never**:
- Spring annotations in `domain/`
- Exceptions for business logic
- Mutable domain state
- Business logic in controllers/repositories
- Primitive obsession

### Error Handling Patterns

**Kotlin**:
```kotlin
context(Raise<DomainError>)
fun operation(): Result {
    ensure(condition) { DomainError.ValidationError("msg") }
    val value = dependency.call().bind()
    return result
}
```

**Java**:
```java
Result<T, DomainError> operation() {
    if (!condition) {
        return new Failure<>(new ValidationError("msg"));
    }
    return new Success<>(result);
}
```

### Value Object Pattern

**Kotlin**:
```kotlin
@JvmInline
value class TransactionId(val value: UUID) {
    init {
        require(value != UUID.NIL) { "ID cannot be nil" }
    }
}
```

**Java**:
```java
record TransactionId(UUID value) {
    public TransactionId {
        if (value.equals(UUID.NIL)) {
            throw new IllegalArgumentException("ID cannot be nil");
        }
    }
}
```

## 🔧 Customization

### Adding New Patterns

To add new patterns or rules:

1. **For architectural rules**: Update `prompts/policies/content_policy.md`
2. **For code examples**: Add examples to `prompts/system/chat_base.md` (or reference a separate examples doc if you create one)
3. **For workflows**: Update `prompts/system/agent_guidelines.md`
4. **For general guidance**: Update `prompts/system/chat_base.md`

### Extending for Other Languages

To add support for another language (e.g., Scala, TypeScript):

1. Add language spec and examples to `prompts/system/chat_base.md`
2. Keep error handling examples alongside the language section in `chat_base.md`
3. Add any language-specific constraints to `prompts/policies/content_policy.md`

### Project-Specific Adaptations

If adapting this for your own project:

1. Update project paths in `chat_base.md`
2. Adjust architectural rules in `content_policy.md` to your needs
3. Keep the core principles (ports & adapters, typed errors, domain purity)

## 📖 Further Reading

- **Main README**: [`README.md`](./README.md) — Project overview and setup
- (Removed) AI Assistant Guide: content consolidated in this document and the prompts under `prompts/`
- **Talk**: [YouTube](https://www.youtube.com/watch?v=kqNDeq-DrVM) — Full conference presentation
- **Code**: Browse `/fvf4j` (Java) and `/fvf4k` (Kotlin) for examples

## 🤝 Contributing to Prompts

If you find ways to improve these prompts:

1. Test changes with actual AI assistants
2. Verify they enforce architecture correctly
3. Add examples for clarity
4. Update this documentation
5. Submit a PR with rationale

## 💡 Tips for Best Results

### For Users
1. **Be specific**: "Add a REST endpoint for querying transactions by category" vs "add query feature"
2. **Reference layers**: "In the domain layer, create..." helps assistant understand context
3. **Ask for plans**: "Create a plan first" triggers agent_guidelines.md workflow
4. **Mention both languages**: "Implement in both Java and Kotlin" ensures dual implementation

### For AI Assistants
1. **Load all relevant prompts**: Don't skip `content_policy.md`
2. **Ask clarifying questions**: Better to ask than violate architecture
3. **Show examples**: Users learn from code, not just explanations
4. **Validate before generating**: Check against policies first
5. **Think inside-out**: Domain first, adapters last

## 🎓 Learning Path

For new AI assistants (or developers) learning the project:

1. **Read**: `chat_base.md` — Core concepts
2. **Study**: `content_policy.md` — Rules and examples
3. **Practice**: Use `agent_guidelines.md` to plan a simple feature
4. **Review**: Existing code in `/fvf4j` and `/fvf4k`
5. **Validate**: Run architecture tests to see enforcement in action

## 📞 Support

For questions or issues with prompts:
- Open an issue on GitHub
- Reference specific prompt file and section
- Provide example of unexpected behavior
- Suggest improvement if you have one

---

**Remember**: These prompts exist to help AI assistants write better code, not to restrict creativity. When in doubt, ask questions, explain trade-offs, and always prioritize clear, maintainable, testable code over clever solutions.
