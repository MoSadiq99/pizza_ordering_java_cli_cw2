package edu.kingston.domain.payment;

import edu.kingston.domain.order.Order;

public interface PaymentStrategy {
    int getId();
    boolean processPayment(Order order);
}