package edu.kingston.domain.user;

import edu.kingston.domain.pizza.Pizza;
import edu.kingston.domain.order.Order;
import edu.kingston.domain.payment.LoyaltyProgram;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User {
    private final String userId;
    private String username;
    private String email;
    private String password;
    private String role;
    private List<Pizza> favoritePizzas;
    private LoyaltyProgram loyaltyProgram;
    private List<Order> orderHistory;

    // Constructor for Registration
    public User(String username, String email, String password, String role) {
        this.userId = UUID.randomUUID().toString();
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.favoritePizzas = new ArrayList<>();
        this.loyaltyProgram = new LoyaltyProgram();
        this.orderHistory = new ArrayList<>();
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public List<Pizza> getFavoritePizzas() { return favoritePizzas; }
    public LoyaltyProgram getLoyaltyProgram() { return loyaltyProgram; }
    public List<Order> getOrderHistory() { return orderHistory; }

    // Methods to add favorite pizzas and orders
    public void addFavoritePizza(Pizza pizza) {
        favoritePizzas.add(pizza);
    }

    public void addOrder(Order order) {
        orderHistory.add(order);
        // Accumulate loyalty points for the order
        loyaltyProgram.accumulatePoints(order.calculateTotal());
    }
}
