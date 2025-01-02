package edu.kingston.domain.pizza.handler;

import edu.kingston.domain.pizza.Pizza;

public class CrustCustomizationHandler extends PizzaCustomizationHandler {
    @Override
    public boolean handle(Pizza.PizzaBuilder pizzaBuilder) {
        // Validate crust selection
        if (pizzaBuilder.getCrust() == null) {
            System.out.println("Crust must be selected");
            return false;
        }

        // If valid, pass to next handler or return true
        return nextHandler == null || nextHandler.handle(pizzaBuilder);
    }
}