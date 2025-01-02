package edu.kingston.domain.user.command;

import edu.kingston.domain.order.Order;
import edu.kingston.domain.user.User;

public class PlaceOrderCommand implements Command {
    private Order order;
    private User user;

    public PlaceOrderCommand(Order order, User user) {
        this.order = order;
        this.user = user;
    }

    @Override
    public void execute() {
        // Process the order
        user.addOrder(order);
        order.getPaymentMethod().processPayment(order);
    }

    @Override
    public void undo() {
        user.getOrderHistory().remove(order);
        System.out.println("Order " + order.getOrderId() + " has been canceled.");
    }

    @Override
    public String getDescription() {
        return "Place Order: " + order.getOrderId();
    }
}