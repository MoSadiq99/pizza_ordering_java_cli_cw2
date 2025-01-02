package edu.kingston.domain.payment;

import edu.kingston.domain.order.Order;
import edu.kingston.domain.user.User;

public class CreditCardPayment implements PaymentStrategy {
    private final int id;
    private String cardNumber;
    private String cvv;

    public CreditCardPayment(int id, String cardNumber, String cvv) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.cvv = cvv;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public boolean processPayment(Order order) {
        // Simulated payment processing
        System.out.println("Processing credit card payment of $" + order.calculateTotal());
        return true;
    }

    public double calculateDiscount(User user) {
        // Basic loyalty discount
        return user.getLoyaltyProgram().getPoints() > 100 ? 0.1 : 0;
    }
}