# ADR 0003: Multi-Module Maven Project Structure

## Status
Accepted

## Context
To enforce physical architectural boundaries between the pure domain logic and infrastructure adapters, single-flat package structures are insufficient because developers can accidentally import infrastructure classes inside domain classes.

## Decision
We structured the repository as a **Multi-Module Maven Project**:
- `order-management` (Parent POM)
  - `order-domain` (Jar module containing domain models, events, ports, and domain unit tests)
  - `order-infrastructure` (Jar module containing in-memory adapters, executable demo app, and adapter integration tests)

`order-domain` has **zero compile-scope dependencies**. `order-infrastructure` depends on `order-domain`.

## Consequences
### Positive
- Build system physically enforces that `order-domain` cannot reference `order-infrastructure` classes or external frameworks.
- Clean separation of test suites between domain unit tests and adapter integration tests.
- Scalable foundation for adding future modules (e.g. `order-web`, `order-persistence-jpa`).

### Negative
- Slightly more verbose build configuration (`pom.xml` files per module).
