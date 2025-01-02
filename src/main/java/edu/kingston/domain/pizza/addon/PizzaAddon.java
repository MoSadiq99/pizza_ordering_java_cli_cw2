package edu.kingston.domain.pizza.addon;

public interface PizzaAddon {
    double getPrice();
    String getType();

    void setPrice(double price);
    void setType(String type);
}
