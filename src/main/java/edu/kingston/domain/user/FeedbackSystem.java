package edu.kingston.domain.user;

import edu.kingston.domain.order.Order;
import edu.kingston.domain.pizza.Pizza;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FeedbackSystem {
    private List<Feedback> feedbacks = new ArrayList<>();

    public void submitFeedback(Order order, int rating, String comment) {
        // Validate rating
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        Feedback feedback = new Feedback(order, rating, comment);
        feedbacks.add(feedback);
    }

    public double calculateAverageRating(Pizza pizza) {
        return feedbacks.stream()
                .filter(f -> isPizzaInOrder(f.getOrder(), pizza))
                .mapToInt(Feedback::getRating)
                .average()
                .orElse(0.0);
    }

    private boolean isPizzaInOrder(Order order, Pizza targetPizza) {
        return order.getPizzas().stream()
                .anyMatch(pizza -> pizza.getName().equals(targetPizza.getName()));
    }

    //? Get Top Rated Pizzas
    public List<Pizza> getTopRatedPizzas(int limit) {
        // Complex recommendation logic
        return feedbacks.stream()
                .flatMap(f -> f.getOrder().getPizzas().stream()
                        .map(pizza -> Map.entry(pizza, f.getRating()))
                )
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey, // Group by pizza
                        Collectors.averagingInt(Map.Entry::getValue) // Average the ratings
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<Pizza, Double>comparingByValue().reversed()) // Sort by average rating
                .limit(limit) // Limit the number of results
                .map(Map.Entry::getKey) // Extract the pizza objects
                .collect(Collectors.toList());
    }

    //? Remove Feedback
    public void removeFeedback(Order order) {
        feedbacks.removeIf(f -> f.getOrder().equals(order));
    }

    //? Inner Feedback class
    public static class Feedback {
        private final Order order;
        private final int rating;
        private final String comment;
        private final LocalDateTime timestamp;

        public Feedback(Order order, int rating, String comment) {
            this.order = order;
            this.rating = rating;
            this.comment = comment;
            this.timestamp = LocalDateTime.now();
        }

        // Getters
        public Order getOrder() { return order; }
        public int getRating() { return rating; }
        public String getComment() { return comment; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }
}