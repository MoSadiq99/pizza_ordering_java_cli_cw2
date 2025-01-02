package edu.kingston.domain.pizza;

import edu.kingston.domain.pizza.addon.PizzaAddon;

import java.time.LocalDate;

public class Promotion {
    private String name;
    private String description;
    private double discountPercentage;
    private LocalDate startDate;
    private LocalDate endDate;
    private String targetAddonType; // Type of addon (e.g., "cheese", "crust")
    private String targetAddonName; // Specific addon name (e.g., "Mozzarella")
    private boolean isActive;

    public Promotion(String name, String description, double discountPercentage,
                     LocalDate startDate, LocalDate endDate, String targetAddonType, String targetAddonName) {
        this.name = name;
        this.description = description;
        this.discountPercentage = discountPercentage;
        this.startDate = startDate;
        this.endDate = endDate;
        this.targetAddonType = targetAddonType;
        this.targetAddonName = targetAddonName;
        this.isActive = isWithinPromotionPeriod();
    }

    public boolean isWithinPromotionPeriod() {
        LocalDate now = LocalDate.now();
        return !now.isBefore(startDate) && !now.isAfter(endDate);
    }

    public boolean appliesToAddon(PizzaAddon addon) {
        return addon.getType().equalsIgnoreCase(targetAddonName);
    }


    public double applyDiscount(double originalPrice) {
        if (isActive) {
            return originalPrice * (1 - (discountPercentage / 100));
        }
        return originalPrice;
    }

    // Getters and setters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getDiscountPercentage() { return discountPercentage; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public String getTargetAddonType() { return targetAddonType; }
    public boolean isActive() { return isActive; }

    public void setActive(boolean b) {
        isActive = b;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setTargetAddonType(String targetAddonType) {
        this.targetAddonType = targetAddonType;
    }

    public void setTargetAddonName(String targetAddonName) {
        this.targetAddonName = targetAddonName;
    }

    public String getTargetAddonName() {
        return targetAddonName;
    }
}

