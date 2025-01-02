package edu.kingston.domain.pizza.addon;

public class Sauce implements PizzaAddon {
    private String type;
    private double price;

    public Sauce(String type, double price) {
        this.type = type;
        this.price = price;
    }

    @Override
    public double getPrice() {
        return this.price;
    }

    @Override
    public String getType() {
        return this.type + " Sauce";
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
