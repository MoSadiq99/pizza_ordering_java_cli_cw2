package edu.kingston.domain.pizza.addon;

public class Topping implements PizzaAddon {
    private String type;
    private double price;

    public Topping(String type, double price) {
        this.type = type;
        this.price = price;
    }

    @Override
    public String getType() {
        return type + " Topping";
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }
}