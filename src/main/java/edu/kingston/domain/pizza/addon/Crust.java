package edu.kingston.domain.pizza.addon;

public class Crust implements PizzaAddon {
    private String type;
    private double price;

    public Crust(String type, double price) {
        this.type = type;
        this.price = price;
    }

    // Getters and setters
    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public String getType() {
        return type + " Crust";
    }

    @Override
    public void setPrice(double price) { this.price = price; }

    @Override
    public void setType(String type) { this.type = type; }
}
