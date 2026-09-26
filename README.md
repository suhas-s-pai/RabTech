# RabTech Order Management System - Task 02

A pure Java 17 Domain-Driven Design (DDD) and Hexagonal Architecture implementation for RabTech Academy Task 02.

## Overview

This project implements an **Order Aggregate** with a strict state machine, immutable value objects, domain events, output ports, and in-memory adapters. The system is designed to be completely independent of frameworks (no Spring, JPA, HTTP, or databases in the domain layer).

---

## Architecture & Project Structure

The project is organized as a multi-module Maven project to physically separate domain concerns from infrastructure adapters:

```
OrderManagement/
├── pom.xml                        # Parent POM
├── README.md                      # Project documentation
├── .gitignore                     # Git ignore rules
├── mvnw / mvnw.cmd                # Maven Wrapper scripts
├── adrs/                          # Architectural Decision Records
│   ├── 0001-pure-domain-hexagonal-architecture.md
│   ├── 0002-order-aggregate-state-machine-and-events.md
│   └── 0003-multi-module-maven-project-structure.md
├── order-domain/                  # Pure Domain Module
│   └── src/
│       ├── main/java/com/rabtech/order/domain/
│       │   ├── model/             # Order, OrderId, OrderItem, OrderStatus
│       │   ├── event/             # Domain Events (OrderConfirmed, PaymentRecorded, OrderCancelled)
│       │   └── port/              # Ports (OrderRepository, NotificationPort, TimePort)
│       └── test/java/com/rabtech/order/domain/
│           └── model/             # Comprehensive JUnit 5 Domain Tests
└── order-infrastructure/          # Infrastructure Module
    └── src/
        ├── main/java/com/rabtech/order/infrastructure/
        │   ├── adapter/           # In-Memory Adapters (InMemoryOrderRepository, etc.)
        │   └── Main.java          # Demo Executable Application
        └── test/java/com/rabtech/order/infrastructure/
            └── adapter/           # JUnit 5 Adapter Tests
```

---

## Aggregate: Order

### States & Allowed Transitions

```
[ DRAFT ]  --(confirm)-->  [ CONFIRMED ]  --(pay)-->  [ PAID ]
    |                            |
(cancel)                      (cancel)
    v                            v
[ CANCELLED ]               [ CANCELLED ]
```

### Business Rules Enforced

1. **Rule 1**: An order must contain at least one line item before confirmation (`Order.confirm()`).
2. **Rule 2**: `OrderItem` quantity must be a positive whole number (`quantity > 0`).
3. **Rule 3**: A cancelled order cannot be paid (`Order.pay()` throws `IllegalStateException`).
4. **Rule 4**: A paid order cannot return to `DRAFT` or be cancelled.
5. **Rule 5**: Order total is dynamically derived from immutable line prices and quantities (`item.price * item.quantity`).

---

## Ports & In-Memory Adapters

| Port (Interface) | In-Memory Adapter | Purpose |
|---|---|---|
| `OrderRepository` | `InMemoryOrderRepository` | Persistence port for saving and querying orders |
| `NotificationPort` | `InMemoryNotificationAdapter` | Notification port for publishing domain events |
| `TimePort` | `InMemoryTimeAdapter` | Time port for deterministic timestamping in domain events |

---

## Domain Events

- **`OrderConfirmed`**: Recorded when an order is confirmed.
- **`PaymentRecorded`**: Recorded when payment is processed.
- **`OrderCancelled`**: Recorded when an order is cancelled.

---

## Prerequisites

- **Java JDK 17** or higher

---

## How to Build & Run Tests

### Using Maven Wrapper (Recommended)

```bash
# Run all unit tests across all modules
./mvnw clean test        # Linux/macOS
.\mvnw.cmd clean test    # Windows
```

### Build and Package JARs

```bash
.\mvnw.cmd clean package
```

### Run the Demo Application (`Main`)

```bash
java -cp "order-infrastructure/target/classes;order-domain/target/classes" com.rabtech.order.infrastructure.Main
```

---

## Test Coverage Summary

- **Total Unit Tests**: 29
- **Domain Tests**: 23 tests (`OrderTest`, `OrderItemTest`, `OrderIdTest`)
- **Infrastructure Tests**: 6 tests (`InMemoryOrderRepositoryTest`, `InMemoryNotificationAdapterTest`, `InMemoryTimeAdapterTest`)
- **Pass Rate**: 100%

---

## Architectural Decision Records (ADRs)

Detailed ADRs explaining key design choices are located in the [`adrs/`](file:///e:/RabTech/OrderManagement/adrs) folder:
- [ADR 0001: Pure Domain Model with Hexagonal Architecture](adrs/0001-pure-domain-hexagonal-architecture.md)
- [ADR 0002: Order Aggregate State Machine and Domain Events](adrs/0002-order-aggregate-state-machine-and-domain-events.md)
- [ADR 0003: Multi-Module Maven Project Structure](adrs/0003-multi-module-maven-project-structure.md)
