package edu.kingston.domain.pizza;

import edu.kingston.domain.pizza.addon.Cheese;
import edu.kingston.domain.pizza.addon.Crust;
import edu.kingston.domain.pizza.addon.Sauce;
import edu.kingston.domain.pizza.addon.Topping;

import java.util.List;

// Builder Pattern for Pizza Construction
public class Pizza {
    private final Crust crust;
    private final Sauce sauce;
    private final List<Topping> toppings;
    private final Cheese cheese;
    private final String name;

    private Pizza(PizzaBuilder builder) {
        this.crust = builder.crust;
        this.sauce = builder.sauce;
        this.toppings = builder.toppings;
        this.cheese = builder.cheese;
        this.name = builder.name;
    }

    public double calculateTotalPrice() {
        double total = crust.getPrice() + sauce.getPrice() + cheese.getPrice();
        total += toppings.stream().mapToDouble(Topping::getPrice).sum();
        return total;
    }

    // Getters
    public Crust getCrust() { return crust; }
    public Sauce getSauce() { return sauce; }
    public List<Topping> getToppings() { return toppings; }
    public Cheese getCheese() { return cheese; }
    public String getName() { return name; }

    // Builder inner class
    public static class PizzaBuilder {
        private Crust crust;
        private Sauce sauce;
        private List<Topping> toppings;
        private Cheese cheese;
        private String name;

        public PizzaBuilder crust(Crust crust) {
            this.crust = crust;
            return this;
        }

        public PizzaBuilder sauce(Sauce sauce) {
            this.sauce = sauce;
            return this;
        }

        public PizzaBuilder toppings(List<Topping> toppings) {
            this.toppings = toppings;
            return this;
        }

        public PizzaBuilder cheese(Cheese cheese) {
            this.cheese = cheese;
            return this;
        }

        public PizzaBuilder name(String name) {
            this.name = name;
            return this;
        }

        public Pizza build() {
            validate();
            return new Pizza(this);
        }

        public Crust getCrust() {
            return crust;
        }

        public Sauce getSauce() {
            return sauce;
        }

        public List<Topping> getToppings() {
            return toppings;
        }

        public Cheese getCheese() {
            return cheese;
        }

        public String getName() {
            return name;
        }

        private void validate() {
            if (getCrust() == null || getSauce() == null || getCheese() == null) {
                throw new IllegalStateException("Pizza must have crust, sauce, and cheese");
            }
        }
    }
}