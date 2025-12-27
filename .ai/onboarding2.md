# Project Onboarding: ddd-by-examples-library

## Welcome

Welcome to the **ddd-by-examples-library** project! This repository serves as a comprehensive, real-world example of **Domain-Driven Design (DDD)**, **Hexagonal Architecture**, and **Event Sourcing** concepts applied to a Library system. It is designed not just as a working application but as an educational resource to demonstrate how to bridge the gap between complex business requirements (discovered via Event Storming) and code.

## Project Overview & Structure

The project is structured as a **Modular Monolith**, where different Bounded Contexts are separated by packages but deployable as a single unit. It uses **Java 11** and **Spring Boot**, but avoids "Spring magic" in the domain layer to keep business logic pure.

### Key Directories

- `src/main/java/io/pillopl/library`: Source code root.
- `src/test` & `src/integration-test`: Comprehensive testing suites (Unit & Integration).
- `docs/`: Extensive documentation including Event Storming stickies, C4 architecture diagrams, and design decisions.
- `monitoring/`: Configuration for Prometheus and Grafana.

## Core Modules

The application is split into Bounded Contexts, found under `io.pillopl.library`:

### 1. `lending` (The Core Domain)
- **Role**: Handles the complex business logic of borrowing books, placing holds, and managing patron rules. This is where the heavy DDD patterns are applied.
- **Key Files/Areas**:
    - `patron/model/Patron.java`: The Aggregate Root for a user. Enforces rules like maximum holds.
    - `patron/model/PlacingOnHoldPolicy.java`: Functional implementation of business rules (e.g., "Regular patrons cannot hold restricted books").
    - `book/model`: specific states of a book (`AvailableBook`, `BookOnHold`).
- **Recent Focus**: Active maintenance and refinement of domain policies.

### 2. `catalogue` (Supporting Subdomain)
- **Role**: Manages the inventory of books (ISBNs, Titles, Authors).
- **Key Files/Areas**: `Catalogue.java`, `Book.java`.
- **Recent Focus**: Stable; acts as a simpler CRUD-like reference implementation compared to Lending.

### 3. `commons`
- **Role**: Shared infrastructure for the building blocks of the system.
- **Key Files/Areas**: `events/`, `aggregates/`, `commands/`. Defines interfaces like `DomainEvent` and `Result`.

## Key Contributors

- **Jakub Pilimon**: Original creator and primary architect.
- **Sebastian Stobiecki**: Recent maintainer focusing on project revival and documentation.
- **Community**: Various contributors fixing bugs and improving architecture (e.g., Marcio Vinicius, Marcin Zięba).

## Overall Takeaways & Recent Focus

- **Revival (Dec 2025)**: The project has seen recent activity after a dormant period, likely focusing on updating dependencies, documentation, and ensuring the project runs on modern environments.
- **Documentation First**: The project maintains excellent sync between documentation (`docs/`) and code. The "Model-Code Gap" is minimized intentionally.
- **Functional Java**: There is a strong push towards Functional Programming using the **Vavr** library (`Either`, `Option`, `Try`) instead of standard exceptions for business logic flow.

## Potential Complexity/Areas to Note

1.  **Vavr & Monads**: If you are used to standard Java/Spring exception handling, the use of `Either<Rejection, Allowance>` for control flow in `PlacingOnHoldPolicy` and Aggregates might take time to get used to.
2.  **Hexagonal Architecture**: Dependencies point *inward*. The Domain model (`model` packages) knows nothing about the Database (`infrastructure` packages) or the Web. Interfaces (Ports) are defined in Domain and implemented in Infrastructure.
3.  **Eventual vs Immediate Consistency**: The system is designed to toggle between these modes. You will see tests verifying behaviors with `pollingConditions.eventually` to handle asynchronous event propagation.

## Questions for the Team

1.  What is the current strategy for database schema migrations? (I see `.sql` files in resources but no clear Flyway/Liquibase setup).
2.  Are we moving towards breaking the monolith into microservices, or is the modular monolith the target architecture?
3.  How do we handle "schema evolution" for the Domain Events if we decide to persist them long-term?
4.  Is the `commons` module intended to be extracted as a separate library eventually?
5.  What is the status of the "Reactive" experiments hinted at in some parts of the code/docs?

## Next Steps

1.  **Read the README**: It is exceptionally detailed. Read the sections on "Domain Description" and "Functional Thinking" closely.
2.  **Run the Tests**: Execute `./mvnw clean verify`. The tests are written in **Spock** (Groovy) and act as living documentation.
3.  **Explore `Patron.java`**: Open `src/main/java/io/pillopl/library/lending/patron/model/Patron.java` to understand how an Aggregate Root protects its invariants using Policies.
4.  **Start the App**: Run `docker-compose up` to see the database and monitoring stack, then start the Spring Boot app.
5.  **Check `docs/`**: Browse the Event Storming images to visualize the flows before tracing them in code.

## Development Environment Setup

1.  **Java**: Ensure you have **Java 11** installed.
2.  **Maven**: Use the included wrapper `./mvnw`.
3.  **Docker**: Required for running the database and monitoring stack (Prometheus/Grafana).
    - Run `docker-compose up` to start infrastructure.
4.  **IDE**: IntelliJ IDEA is recommended, with **Lombok** plugin installed.

## Helpful Resources

- **Project README**: [README.md](./README.md)
- **Architecture Diagrams**: `docs/c4/component-diagram.png`
- **Vavr Documentation**: [vavr.io](https://www.vavr.io) (Essential for understanding the functional parts)
- **Spock Framework**: [spockframework.org](https://spockframework.org) (For understanding the tests)
