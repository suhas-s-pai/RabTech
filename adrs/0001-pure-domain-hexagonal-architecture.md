# ADR 0001: Pure Domain Model with Hexagonal Architecture (Ports and Adapters)

## Status
Accepted

## Context
In RabTech Academy Task 02, the Order Management system requires a domain model that enforces core business rules around order placement, confirmation, payment, line items, and cancellation. Traditional monolithic framework implementations couple business rules directly with Spring annotations, JPA entities, database logic, or web frameworks. This creates fragile codebases that are difficult to test, hard to refactor, and bound to specific framework lifecycles.

## Decision
We decided to adopt **Hexagonal Architecture (Ports and Adapters)** and construct a **Pure Domain Model**:
1. The domain module (`order-domain`) is completely free of external framework dependencies (no Spring, no JPA, no Hibernate, no HTTP abstractions).
2. Domain logic depends only on Java standard library (Java 17).
3. Explicit **Ports** are defined in the domain as Java interfaces:
   - `OrderRepository` for persistence operations.
   - `NotificationPort` for publishing domain events.
   - `TimePort` for providing time instances.
4. **Adapters** are created in the infrastructure module (`order-infrastructure`) to implement these ports:
   - `InMemoryOrderRepository`
   - `InMemoryNotificationAdapter`
   - `InMemoryTimeAdapter`

## Consequences
### Positive
- **High Testability**: Core business rules can be thoroughly tested in milliseconds without booting Spring contexts or mock databases.
- **Framework Independence**: Upgrades or replacements of frameworks (e.g., migrating from Spring to Quarkus or raw JDBC) will not impact core domain rules.
- **Separation of Concerns**: Infrastructure concerns are cleanly separated from business logic.

### Negative
- Requires mapping between domain models and database persistence models when a database is introduced in future tasks.
