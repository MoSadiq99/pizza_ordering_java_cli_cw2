package edu.kingston.domain.order;

public interface OrderStatusObserver {
    void updateOrderStatus(Order order, OrderStatus status);
}
