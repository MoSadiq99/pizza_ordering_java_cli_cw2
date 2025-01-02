package edu.kingston.repository;

import edu.kingston.domain.order.Order;
import edu.kingston.domain.payment.LoyaltyProgram;
import edu.kingston.domain.pizza.Promotion;
import edu.kingston.domain.pizza.Pizza;
import edu.kingston.domain.pizza.PromotionDecorator;
import edu.kingston.domain.pizza.addon.*;
import edu.kingston.domain.user.FeedbackSystem;
import edu.kingston.domain.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppRepository {
    private static AppRepository instance;

    public static AppRepository getInstance() {
        if (instance == null) {
            instance = new AppRepository();
        }
        return instance;
    }

    private UserRepository userRepository = new UserRepository();
    private List<Order> orders = new ArrayList<>();
    private List<Pizza> pizzas = new ArrayList<>();
    private List<FeedbackSystem.Feedback> feedbacks = new ArrayList<>();
    private List<LoyaltyProgram> loyaltyPrograms = new ArrayList<>();
    private List<Pizza> favouritePizzas = new ArrayList<>();
    private List<Promotion> promotions = new ArrayList<>();

    private List<Topping> availableToppings = new ArrayList<>();
    private List<Crust> availableCrusts = new ArrayList<>();
    private List<Sauce> availableSauces = new ArrayList<>();
    private List<Cheese> availableCheeses = new ArrayList<>();
    private Map<String, List<PromotionDecorator>> addonPromotions = new HashMap<>();
    private FeedbackSystem feedbackSystem = new FeedbackSystem();

    // Getters and Setters
    public List<User> getUsers() {
        return userRepository.getUsers();
    }

    public void addUser(User user) {
        userRepository.addUser(user);
    }

    public List<Pizza> getFavouritePizzas() {
        return favouritePizzas;
    }

    public List<Promotion> getPromotions() {
        return promotions;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public List<Pizza> getPizzas() {
        return pizzas;
    }

    public FeedbackSystem getFeedbackSystem() {
        return feedbackSystem;
    }

    public List<FeedbackSystem.Feedback> getFeedbacks() {
        return feedbacks;
    }

    public List<LoyaltyProgram> getLoyaltyPrograms() {
        return loyaltyPrograms;
    }

    public List<Topping> getAvailableToppings() {
        return availableToppings;
    }

    public List<Crust> getAvailableCrusts() {
        return availableCrusts;
    }

    public List<Sauce> getAvailableSauces() {
        return availableSauces;
    }

    public List<Cheese> getAvailableCheeses() {
        return availableCheeses;
    }

    //? Promotions
    public void applyPromotionToAddon(Promotion promotion) {
        String targetType = promotion.getTargetAddonType();

        // Apply to all matching addons
        List<PizzaAddon> addons = getAddonsByType(targetType);
        if (addons != null) {
            for (PizzaAddon addon : addons) {
                PromotionDecorator decorator = new PromotionDecorator(addon, promotion);
                addonPromotions.computeIfAbsent(targetType, k -> new ArrayList<>())
                        .add(decorator);
            }
        }
    }

    public List<PizzaAddon> getAddonsByType(String type) {
        switch (type.toLowerCase()) {
            case "topping":
                return new ArrayList<>(availableToppings);
            case "crust":
                return new ArrayList<>(availableCrusts);
            case "sauce":
                return new ArrayList<>(availableSauces);
            case "cheese":
                return new ArrayList<>(availableCheeses);
            default:
                return null;
        }
    }

    public void addPromotion(Promotion promotion) {
        if (promotions.contains(promotion)) {
            System.out.println("Promotion already exists.");
            return;
        }
        promotions.add(promotion);
        applyPromotionToAddon(promotion);
        System.out.println("Promotion added and applied successfully.");
    }

    public void addOrder(Order order) {
        orders.add(order);
    }

    public void addFavouritePizza(Pizza pizza) {
        favouritePizzas.add(pizza);
    }
}
