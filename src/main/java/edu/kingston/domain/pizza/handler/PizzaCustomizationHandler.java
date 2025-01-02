package edu.kingston.domain.pizza.handler;

import edu.kingston.domain.pizza.Pizza;

// Chain of Responsibility Pattern for Order Customization
public abstract class PizzaCustomizationHandler {
    protected PizzaCustomizationHandler nextHandler;

    public void setNextHandler(PizzaCustomizationHandler handler) {
        this.nextHandler = handler;
    }

    public abstract boolean handle(Pizza.PizzaBuilder pizzaBuilder);
}