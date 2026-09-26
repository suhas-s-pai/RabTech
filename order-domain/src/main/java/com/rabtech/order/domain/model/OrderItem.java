package com.rabtech.order.domain.model;

import java.util.Objects;

public final class OrderItem {
    private final String product;
    private final double price;
    private final int quantity;

    public OrderItem(String product, double price, int quantity) {
        if (product == null || product.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be null or blank");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.product = product;
        this.price = price;
        this.quantity = quantity;
    }

    public String getProduct() {
        return product;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getSubtotal() {
        return price * quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return Double.compare(orderItem.price, price) == 0 &&
               quantity == orderItem.quantity &&
               Objects.equals(product, orderItem.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, price, quantity);
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "product='" + product + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}
