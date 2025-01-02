package edu.kingston.domain.pizza.handler;

import edu.kingston.domain.pizza.Pizza;

public class SauceCustomizationHandler extends PizzaCustomizationHandler {
    @Override
    public boolean handle(Pizza.PizzaBuilder pizzaBuilder) {
        // Validate sauce selection
        if (pizzaBuilder.getSauce() == null) {
            System.out.println("Sauce must be selected");
            return false;
        }
        // If valid, pass to next handler or return true
        return nextHandler == null || nextHandler.handle(pizzaBuilder);
    }
}
