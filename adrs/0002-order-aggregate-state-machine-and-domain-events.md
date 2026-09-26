# ADR 0002: Order Aggregate State Machine and Domain Events

## Status
Accepted

## Context
The Order aggregate represents the central domain entity managing order lifecycles and business invariants. Strict constraints dictate how orders transition between states (`DRAFT`, `CONFIRMED`, `PAID`, `CANCELLED`) and how side effects (such as notifications) are communicated to external components.

## Decision
1. **Explicit State Transitions**: State transitions are strictly controlled via domain methods on the `Order` aggregate (`confirm()`, `pay()`, `cancel()`). Direct mutation of `OrderStatus` is prohibited.
2. **Business Rule Enforcement**:
   - **Rule 1**: Confirmation requires at least one line item (`items.isEmpty()` check).
   - **Rule 2**: `OrderItem` quantities must be positive whole numbers (`quantity > 0`).
   - **Rule 3**: A `CANCELLED` order cannot be paid.
   - **Rule 4**: A `PAID` order cannot return to `DRAFT` or be cancelled.
   - **Rule 5**: Order totals are dynamically calculated from immutable line items (`subtotal = price * quantity`).
3. **Immutable Value Objects**: Introduced `OrderId` and `OrderItem` as immutable value objects.
4. **Domain Events**: Created immutable event records (`OrderConfirmed`, `PaymentRecorded`, `OrderCancelled`) implemented via Java 17 records. The `Order` aggregate records these events upon successful state transitions, which are dispatched via `NotificationPort`.

## Consequences
### Positive
- Aggregate state is guaranteed to be valid at all times; invalid state transitions throw immediate `IllegalStateException` or `IllegalArgumentException`.
- Auditability and event-driven integration are enabled through immutable domain events.
- Domain rules are explicit and self-documenting in code.

### Negative
- Clients must handle checked domain runtime exceptions when attempting invalid operations.
