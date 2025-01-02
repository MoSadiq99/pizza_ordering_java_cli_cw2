package edu.kingston.domain.pizza.handler;

import edu.kingston.domain.pizza.Pizza;

public class ToppingCustomizationHandler extends PizzaCustomizationHandler {
    private static final int MAX_TOPPINGS = 5;

    @Override
    public boolean handle(Pizza.PizzaBuilder pizzaBuilder) {
        // Validate number of toppings
        if (pizzaBuilder.getToppings() != null && pizzaBuilder.getToppings().size() > MAX_TOPPINGS) {
            System.out.println("Maximum " + MAX_TOPPINGS + " toppings allowed");
            return false;
        }

        // If valid, pass to next handler or return true
        return nextHandler == null || nextHandler.handle(pizzaBuilder);
    }
}