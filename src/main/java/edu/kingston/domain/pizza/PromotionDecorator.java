package edu.kingston.domain.pizza;

import edu.kingston.domain.pizza.addon.PizzaAddon;

public class PromotionDecorator implements PizzaAddon {
    private PizzaAddon addon;
    private Promotion promotion;

    public PromotionDecorator(PizzaAddon addon, Promotion promotion) {
        this.addon = addon;
        this.promotion = promotion;
    }

    @Override
    public double getPrice() {
        if (promotion != null && promotion.isWithinPromotionPeriod()) {
            return promotion.applyDiscount(addon.getPrice());
        }
        return addon.getPrice();
    }

    @Override
    public String getType() {
        return addon.getType();
    }

    @Override
    public void setPrice(double price) {
        addon.setPrice(price);
    }

    @Override
    public void setType(String type) {
        addon.setType(type);
    }
}
