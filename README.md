# RabTech Academy Multi-Module System

A production-grade Java 17 multi-module repository containing RabTech Academy Task 02 (DDD Order Management System) and Task 03 (Core Java OOP, Collections & Exception Hierarchy).

---

# RabTech Order Management System - Task 02

A pure Java 17 Domain-Driven Design (DDD) and Hexagonal Architecture implementation for RabTech Academy Task 02.

## Overview

This project implements an **Order Aggregate** with a strict state machine, immutable value objects, domain events, output ports, and in-memory adapters. The system is designed to be completely independent of frameworks (no Spring, JPA, HTTP, or databases in the domain layer).

---

## Architecture & Project Structure

The project is organized as a multi-module Maven project to physically separate domain concerns from infrastructure adapters:

```
OrderManagement/
├── pom.xml                        # Parent POM (Modules: order-domain, order-infrastructure, task-03-core-java)
├── README.md                      # Comprehensive project documentation
├── .gitignore                     # Git ignore rules
├── mvnw / mvnw.cmd                # Maven Wrapper scripts
├── adrs/                          # Architectural Decision Records
│   ├── 0001-pure-domain-hexagonal-architecture.md
│   ├── 0002-order-aggregate-state-machine-and-events.md
│   └── 0003-multi-module-maven-project-structure.md
├── order-domain/                  # Pure Domain Module (Task 02)
│   └── src/
│       ├── main/java/com/rabtech/order/domain/
│       │   ├── model/             # Order, OrderId, OrderItem, OrderStatus
│       │   ├── event/             # Domain Events (OrderConfirmed, PaymentRecorded, OrderCancelled)
│       │   └── port/              # Ports (OrderRepository, NotificationPort, TimePort)
│       └── test/java/com/rabtech/order/domain/
│           └── model/             # Comprehensive JUnit 5 Domain Tests
├── order-infrastructure/          # Infrastructure Module (Task 02)
│   └── src/
│       ├── main/java/com/rabtech/order/infrastructure/
│       │   ├── adapter/           # In-Memory Adapters (InMemoryOrderRepository, etc.)
│       │   └── Main.java          # Demo Executable Application
│       └── test/java/com/rabtech/order/infrastructure/
│           └── adapter/           # JUnit 5 Adapter Tests
└── task-03-core-java/             # Core Java OOP & Collections Module (Task 03)
    └── src/
        ├── main/java/com/rabtech/task03/
        │   ├── model/             # Employee, Developer, Manager, Project record, EmployeeSummary record
        │   ├── exception/         # Checked & Unchecked Custom Exception Hierarchy
        │   └── service/           # EmployeeManagementEngine (Collections & Streams Engine)
        └── test/java/com/rabtech/task03/
            ├── model/             # EmployeeTest (OOP, Inheritance, Polymorphism, Validation)
            └── service/           # EmployeeManagementEngineTest & StreamAndLambdaOperationsTest
```

---

## Aggregate: Order (Task 02)

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

## Ports & In-Memory Adapters (Task 02)

| Port (Interface) | In-Memory Adapter | Purpose |
|---|---|---|
| `OrderRepository` | `InMemoryOrderRepository` | Persistence port for saving and querying orders |
| `NotificationPort` | `InMemoryNotificationAdapter` | Notification port for publishing domain events |
| `TimePort` | `InMemoryTimeAdapter` | Time port for deterministic timestamping in domain events |

---

## Domain Events (Task 02)

- **`OrderConfirmed`**: Recorded when an order is confirmed.
- **`PaymentRecorded`**: Recorded when payment is processed.
- **`OrderCancelled`**: Recorded when an order is cancelled.

---

# RabTech Employee Management System - Task 03

A complete Java 17 Object-Oriented Programming (OOP), Collections Framework, Java Streams/Lambdas, and Custom Exception Hierarchy implementation for RabTech Academy Task 03.

## Domain Model & OOP Concepts

Task 03 demonstrates core Object-Oriented Programming (OOP) principles:

- **Abstraction & Encapsulation**: Abstract base class [`Employee`](file:///e:/RabTech/OrderManagement/task-03-core-java/src/main/java/com/rabtech/task03/model/Employee.java) encapsulates private fields (`id`, `name`, `department`, `salary`) with strict validation rules in constructors and setters.
- **Inheritance**: Subclasses [`Developer`](file:///e:/RabTech/OrderManagement/task-03-core-java/src/main/java/com/rabtech/task03/model/Developer.java) and [`Manager`](file:///e:/RabTech/OrderManagement/task-03-core-java/src/main/java/com/rabtech/task03/model/Manager.java) extend `Employee`, adding role-specific attributes (`programmingLanguage`, `teamSize`).
- **Polymorphism**: The abstract methods `getRole()` and `calculateBonus()` are polymorphically implemented:
  - `Developer`: `calculateBonus() = salary * 0.15`
  - `Manager`: `calculateBonus() = salary * 0.20 + (teamSize * 500.0)`
- **Java 17 Records**: Immutable value containers [`Project`](file:///e:/RabTech/OrderManagement/task-03-core-java/src/main/java/com/rabtech/task03/model/Project.java) (`id`, `name`, `clientName`) and [`EmployeeSummary`](file:///e:/RabTech/OrderManagement/task-03-core-java/src/main/java/com/rabtech/task03/model/EmployeeSummary.java).

---

## Collections Framework Integration

The [`EmployeeManagementEngine`](file:///e:/RabTech/OrderManagement/task-03-core-java/src/main/java/com/rabtech/task03/service/EmployeeManagementEngine.java) combines all three Java Collections types:

- **`Map<String, Employee>`**: `LinkedHashMap` for fast $O(1)$ employee lookups by ID and guaranteeing ID uniqueness.
- **`Set<Employee>`**: `HashSet` to maintain unique employee object references.
- **`Set<Project>` per Employee**: `Map<String, Set<Project>>` to track assigned projects per employee and prevent duplicate project assignments.
- **`List<Employee>`**: Returned for ordered stream query outputs.

---

## Streams & Lambdas Business Operations

The engine implements advanced Java Streams and Functional Programming queries:

1. **Department Filtering**: `filterByDepartment(String dept)`
2. **Salary Threshold Filtering**: `filterByMinimumSalary(double minSalary)`
3. **Salary Sorting**: `sortBySalary(boolean ascending)` using `Comparator.comparingDouble(Employee::getSalary)`
4. **Name Sorting**: `sortByName()` using `Comparator.comparing(Employee::getName, String.CASE_INSENSITIVE_ORDER)`
5. **Department Grouping**: `groupByDepartment()` using `Collectors.groupingBy(Employee::getDepartment)`
6. **Role Counting**: `countByRole()` using `Collectors.groupingBy(Employee::getRole, Collectors.counting())`
7. **Employee Summary**: `generateSummary()` utilizing `mapToDouble()` for total budget sum and average salary calculation.

---

## Custom Exception Hierarchy

| Exception | Type | Inheritance | Usage Trigger |
|---|---|---|---|
| `EmployeeNotFoundException` | **CHECKED** | `java.lang.Exception` | Thrown when querying, removing, or assigning projects to an unrecorded employee ID |
| `DuplicateEmployeeException` | **UNCHECKED** | `java.lang.RuntimeException` | Thrown when attempting to add an employee whose ID already exists |
| `InvalidEmployeeException` | **UNCHECKED** | `java.lang.RuntimeException` | Thrown when employee attributes (ID, name, department, salary, project) fail validation rules |
| `DuplicateAssignmentException` | **UNCHECKED** | `java.lang.RuntimeException` | Thrown when attempting to assign an employee to a project they are already assigned to |

---

## How to Build & Run All Tests

### Prerequisites
- **Java JDK 17** or higher

### Run All Unit Tests Across All Modules (Task 02 + Task 03)

```bash
# Windows (PowerShell / CMD)
.\mvnw.cmd clean test

# Linux / macOS
./mvnw clean test
```

### Build & Package JARs

```bash
.\mvnw.cmd clean package
```

---

## Test Coverage Summary

- **Total Unit Tests Across Project**: **55 Tests** (100% Pass Rate)
  - **Task 02 Domain Tests**: 23 tests
  - **Task 02 Infrastructure Tests**: 6 tests
  - **Task 03 Tests**: **26 tests** (`EmployeeTest`, `EmployeeManagementEngineTest`, `StreamAndLambdaOperationsTest`)

---

## Architectural Decision Records (ADRs)

Detailed ADRs explaining key design choices are located in the [`adrs/`](file:///e:/RabTech/OrderManagement/adrs) folder:
- [ADR 0001: Pure Domain Model with Hexagonal Architecture](adrs/0001-pure-domain-hexagonal-architecture.md)
- [ADR 0002: Order Aggregate State Machine and Domain Events](adrs/0002-order-aggregate-state-machine-and-events.md)
- [ADR 0003: Multi-Module Maven Project Structure](adrs/0003-multi-module-maven-project-structure.md)
