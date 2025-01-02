package edu.kingston.domain.pizza.handler;

import edu.kingston.domain.pizza.Pizza;

public class CheeseCustomizationHandler extends PizzaCustomizationHandler {
    @Override
    public boolean handle(Pizza.PizzaBuilder pizzaBuilder) {
        // Validate cheese selection
        if (pizzaBuilder.getCheese() == null) {
            System.out.println("Cheese must be selected");
            return false;
        }
        // If valid, pass to next handler or return true
        return nextHandler == null || nextHandler.handle(pizzaBuilder);
    }
}
