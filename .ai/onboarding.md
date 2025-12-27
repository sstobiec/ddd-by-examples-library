# Project Onboarding: Library (DDD by Examples)

## Welcome

Welcome to the **Library** project! This repository is a comprehensive example of a library system built using **Domain-Driven Design (DDD)**, **Event Storming**, and **Hexagonal Architecture**. It serves as a practical reference for implementing complex business rules, bounded contexts, and reactive patterns in a modular monolith structure.

## Project Overview & Structure

The core functionality revolves around managing library resources, including book cataloging, patron profiles, hold requests, and checkouts. The project is organized as a **modular monolith**, meaning different bounded contexts reside in the same repository but are separated by packages to ensure modularity and potential future extraction into microservices.

## Core Modules

### `lending` (`src/main/java/io/pillopl/library/lending`)

- **Role:** The core business domain responsible for managing the complex lifecycle of book lending, including patron holds, checkouts, and returns. It implements a **Hexagonal Architecture** to isolate domain logic from infrastructure.
- **Structure:**
  - **Domain (Core):** `Patron` (Aggregate Root), `PatronEvent` (Domain Events), `PlacingOnHoldPolicy` (Business Rules). The `Patron` aggregate does not modify state directly but emits events.
  - **Application:** `PlacingOnHold` (Service) - Orchestrates the flow using Vavr's `Try` and pattern matching.
  - **Infrastructure:** `PatronsDatabaseRepository` (Persistence), `PatronDatabaseEntity` (State reconstruction via events).
  - **Web:** `PatronProfileController` (REST API).
- **Key Files:** `Patron.java`, `PlacingOnHold.java`, `PatronEvent.java`, `PatronProfileController.java`, `PatronDatabaseEntity.java`.
- **Recent Focus:**
  - **Encapsulation:** Enforcing "OOP hermetization rules" within the domain model (e.g., `Patron.java`).
  - **API Evolution:** Refinement of `PatronProfileController` towards a cleaner RESTful design, moving away from complex HATEOAS wrappers.
  - **Testing:** Enhancing coverage for edge cases like duplicate holds and hold expiration.

### `catalogue` (`src/main/java/io/pillopl/library/catalogue`)

- **Role:** A simpler context dedicated to maintaining the inventory of books and physical instances. It follows a **CRUD-like architecture**.
- **Structure:** Organized around the `Catalogue` service, with a domain model consisting of `Book`, `BookInstance`, and `ISBN`.
- **Key Files:** `Catalogue.java`, `BookInstance.java`, `CatalogueDatabase.java`.
- **Recent Focus:** Foundational setup and integration with the shared event publication system (e.g., `MeteredDomainEventsPublisher`).

### `commons` (`src/main/java/io/pillopl/library/commons`)

- **Role:** Shared infrastructure layer providing building blocks for the DDD architecture, particularly for event-driven communication and result handling.
- **Structure:** Contains generic definitions for `DomainEvent`, event publishers, and functional result types (`Result`, `BatchResult`).
- **Key Files:** `DomainEvent.java`, `MeteredDomainEventsPublisher.java`.
- **Recent Focus:** Robust infrastructure support, specifically adding metrics to event publishing and standardizing domain event handling.

## Key Contributors

- **Jakub Pilimon:** Primary architect and contributor. Driven the core domain logic (`lending`), DDD implementation patterns, and initial project structure.
- **Maciej Szarlinski:** Active in build configuration, infrastructure/commons improvements, and event publishing mechanisms (`MeteredDomainEventsPublisher`).
- **bslota:** Contributed to Spring configuration and domain logic implementation.
- **Kamil Witkowski:** Focused on enforcing OOP encapsulation and "hermetization" rules within the domain model.
- **Krzysztof Wedrowicz:** Worked on refining the Web/API layer (`PatronProfileController`), specifically simplifying endpoint responses and HATEOAS usage.
- **Marcin Świerczyński:** Contributed to fixing and testing specific domain scenarios (e.g., duplicate holds).

## Overall Takeaways & Recent Focus

1.  **Refining Domain Logic & Encapsulation:** There is a concerted effort to strictly encapsulate domain state. Recent changes highlight a move to "OOP hermetization," ensuring aggregates like `Patron` enforce invariants strictly and interact only via well-defined methods and events.
2.  **Living Documentation:** The high churn rate in `README.md` and `docs/design-level.md` parallels code changes, indicating a disciplined practice of keeping Event Storming artifacts in sync with the implementation ("Model-Code Gap" closure).
3.  **Functional Core, Imperative Shell:** The use of **Vavr** (`Try`, `Either`, `Option`) is pervasive in the `lending` module, particularly in application services (`PlacingOnHold`), effectively creating a functional core within the Java application.
4.  **API Simplification:** The git history of `PatronProfileController` shows a shift towards more standard RESTful responses, reducing the complexity of HATEOAS wrappers used in earlier versions.

## Potential Complexity/Areas to Note

-   **Event-Sourced State Reconstruction:** `PatronDatabaseEntity.java` contains a unique `handle(PatronEvent)` method. It reconstructs or updates the persistence state by "playing" domain events. This dual-model approach (Domain Aggregate vs. Persistence Entity) requires careful synchronization.
-   **Vavr Pattern Matching:** The `PlacingOnHold` service extensively uses Vavr's pattern matching (`Match(result).of(...)`). Developers unfamiliar with this functional style might find the control flow non-standard for Java.
-   **Strict Hexagonal Boundaries:** The separation between `Patron` (Domain), `PlacingOnHold` (Application), and `PatronProfileController` (Web) is rigid. Navigating the call stack requires understanding these architectural layers.

## Questions for the Team

1.  **API Strategy:** We noticed a removal of some HATEOAS wrappers in `PatronProfileController`. Is the long-term goal to move towards standard REST (JSON) only, or is HATEOAS still a priority for new endpoints?
2.  **Event Handling Pattern:** `PatronDatabaseEntity` manually handles events to update state. Is this "state reconstruction" pattern preferred over mapping the Aggregate state directly to the Entity?
3.  **Hermetization Rules:** What specific "OOP hermetization" violations were identified recently that led to the refactoring in the `Patron` aggregate?
4.  **Schema Migrations:** How do we handle schema migrations for `spring-data-jdbc` given the use of custom SQL scripts in `src/main/resources`?
5.  **Event Failure Handling:** What is the strategy for handling event failures or "dead letters" in the `StoreAndForwardDomainEventPublisher`?

## Next Steps

1.  **Action Item:** **Trace the Core Flow:** Start by tracing the `placeOnHold` command. Follow the path: `PatronProfileController` -> `PlacingOnHold` (Application Service) -> `Patron` (Aggregate) -> `PatronEvent` (Event).
2.  **Action Item:** **Compare Model to Code:** Open `docs/design-level.md` side-by-side with `src/main/java/io/pillopl/library/lending/patron/model/Patron.java`. Observe how the "sticky notes" (Policies, Commands, Events) map directly to Java classes and methods.
3.  **Action Item:** **Understand the Functional Styles:** Review `PlacingOnHold.java` to understand how `Vavr`'s `Try` and `Either` are used for error handling instead of exceptions.
4.  **Action Item:** **Review Persistence Logic:** Examine `PatronDatabaseEntity.java` to understand how the system bridges the gap between the Event-driven domain model and the relational database state.
