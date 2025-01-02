package edu.kingston.cli;

import edu.kingston.domain.order.Order;
import edu.kingston.domain.pizza.Pizza;
import edu.kingston.domain.pizza.Promotion;
import edu.kingston.domain.pizza.addon.*;
import edu.kingston.repository.AppRepository;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

public class AdminCLI {
    private static final Scanner scanner = new Scanner(System.in);

    private static final AppRepository appRepository = AppRepository.getInstance();

    List<Topping> toppings;

    static void mainMenu() {

        while (true) {

            System.out.println("\n+-----------------------------------------+");
            System.out.println("|         NAPPOLY PIZZA ADMIN PANEL       |");
            System.out.println("+-----------------------------------------+\n");

            System.out.println("1. View Order History      [#1]");
            System.out.println("2. View Loyalty Program    [#2]");
            System.out.println("3. Seasonal Offers         [#3]");
            System.out.println("4. Update Addons           [#4]");
            System.out.println("5. Logout                  [#5]");
            System.out.println("6. Exit                    [#6]");
            System.out.print("\nEnter your choice: ");

            int choice = getIntInput();
            switch (choice) {
                case 1:
                    viewOrderHistory();
                    break;
                case 2:
                    viewLoyaltyProgram();
                    break;
                case 3:
                    seasonalOffers();
                    break;
                case 4:
                    updateAddons();
                    break;
                case 5:
                    MainCLI.mainMenu();
                case 6:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void viewOrderHistory() {
        List<Order> orderHistory = appRepository.getOrders();
        if (orderHistory.isEmpty()) {
            System.out.println("No order history found.");
            return;
        }

        System.out.println("\n--- Order History ---");
        System.out.println("=====================================");

        for (int i = 0; i < orderHistory.size(); i++) {
            Order order = orderHistory.get(i);

            System.out.printf("%-15s : %d\n", "Order Number : ", i + 1);
            System.out.println("-------------------------------------");
            System.out.printf("%-15s : %s\n", "Order ID", order.getOrderId());
            System.out.printf("%-15s : %s\n", "Order Type", order.getOrderType());
            System.out.println("\nItems:");
            System.out.println("-------------------------------------");

            for (Pizza pizza : order.getPizzas()) {
                System.out.printf("\t%-15s : $%.2f\n",
                        pizza.getName(),
                        pizza.calculateTotalPrice()
                );
            }
            System.out.println("-------------------------------------");
            System.out.printf("%-15s : $%.2f\n", "Total", order.calculateTotal());
            System.out.println("=====================================");
        }

    }

    private static void viewLoyaltyProgram() {

    }

    private static void seasonalOffers() {
        while (true) {
            System.out.println("\n--- Seasonal Offers ---");
            System.out.println("1. View All Offers");
            System.out.println("2. Add New Promotion");
            System.out.println("3. Edit Existing Promotion");
            System.out.println("4. Remove Promotion");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter your choice: ");
            int choice = getIntInput();

            switch (choice) {
                case 1 -> viewAllOffers();
                case 2 -> addNewOffer();
                case 3 -> editOffer();
                case 4 -> removeOffer();
                case 5 -> {
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void removeOffer() {
        System.out.println("\n--- Remove Offer ---");

        // Retrieve promotions from the repository
        List<Promotion> promotions = appRepository.getPromotions();

        // Check if promotions list is empty
        if (promotions.isEmpty()) {
            System.out.println("\nNo offers found.");
            return;
        }

        // Iterate through the promotions list
        for (int i = 0; i < promotions.size(); i++) {
            Promotion promotion = promotions.get(i);

            // Display active offers
            if (promotion.isActive()) {
                System.out.println("Active Offer: " + (i + 1));
                System.out.printf("%d. %-15s %5.2f%% off - %s\n",
                        i + 1,
                        promotion.getName(),
                        promotion.getDiscountPercentage(),
                        promotion.getDescription()
                );
            }
        }

        System.out.print("\nEnter the number of the offer you want to remove: ");
        int choice = getIntInput();

        if (choice > 0 && choice <= promotions.size()) {
            Promotion promotion = promotions.get(choice - 1);
            promotion.setActive(false);
            System.out.println("Offer removed successfully.");
        } else {
            System.out.println("Invalid offer number. Please try again.");
        }
    }

    private static void editOffer() {
        System.out.println("\n--- Edit Offer ---");

        // Retrieve promotions from the repository
        List<Promotion> promotions = appRepository.getPromotions();

        // Check if promotions list is empty
        if (promotions.isEmpty()) {
            System.out.println("\nNo offers found.");
            return;
        }

        // Iterate through the promotions list
        for (int i = 0; i < promotions.size(); i++) {
            Promotion promotion = promotions.get(i);

            // Display active offers
            if (promotion.isActive()) {
                System.out.println("Active Offer: " + (i + 1));
                System.out.printf("%d. %-15s %5.2f%% off - %s\n",
                        i + 1,
                        promotion.getName(),
                        promotion.getDiscountPercentage(),
                        promotion.getDescription()
                );
            }
        }

        System.out.print("\nEnter the number of the offer you want to edit: ");
        int choice = getIntInput();

        if (choice > 0 && choice <= promotions.size()) {
            Promotion promotion = promotions.get(choice - 1);
            editOfferDetails(promotion);
        } else {
            System.out.println("Invalid offer number. Please try again.");
        }
    }

    private static void editOfferDetails(Promotion promotion) {
        System.out.println("\n--- Edit Offer Details ---");
        System.out.print("\nEnter offer name or Enter to skip: ");
        String name = scanner.nextLine();
        if (name.isEmpty()) {
            name = promotion.getName();
        }
        System.out.print("Enter offer description or Enter to skip: ");
        String description = scanner.nextLine();
        if (description.isEmpty()) {
            description = promotion.getDescription();
        }
        System.out.print("Enter Discount Percentage or Enter to skip: ");
        double discount = getDoubleInput();
        if (discount == 0) {
            discount = promotion.getDiscountPercentage();
        }

        System.out.println("\nSelect addon type for promotion or Enter to skip:");
        System.out.println("1. Topping");
        System.out.println("2. Crust");
        System.out.println("3. Sauce");
        System.out.println("4. Cheese");
        System.out.print("Enter choice: ");
        int addonChoice = getIntInput();

        String addonType;
        switch (addonChoice) {
            case 1 -> addonType = "topping";
            case 2 -> addonType = "crust";
            case 3 -> addonType = "sauce";
            case 4 -> addonType = "cheese";
            default -> {
                System.out.println("Invalid choice.");
                return;
            }
        }

        // Fetch the available addons to allow selection
        List<PizzaAddon> addons = AppRepository.getInstance().getAddonsByType(addonType);
        if (addons.isEmpty()) {
            System.out.println("No addons available for the selected type.");
            return;
        }

        System.out.println("\nAvailable addons:");
        for (int i = 0; i < addons.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, addons.get(i).getType());
        }
        System.out.print("Select the specific addon to apply promotion: ");
        int addonIndex = getIntInput() - 1;

        if (addonIndex < 0 || addonIndex >= addons.size()) {
            System.out.println("\nInvalid addon selection.");
            return;
        }
        String targetAddonName = addons.get(addonIndex).getType();

        LocalDate startDate = null;
        LocalDate endDate = null;

        // Prompt for start date
        while (startDate == null) {
            System.out.print("\nEnter start date (YYYY-MM-DD): ");
            try {
                startDate = LocalDate.parse(scanner.nextLine());
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please enter a valid date in YYYY-MM-DD format.");
            }
        }

        // Prompt for end date
        while (endDate == null || endDate.isBefore(startDate)) {
            System.out.print("Enter end date (YYYY-MM-DD): ");
            try {
                endDate = LocalDate.parse(scanner.nextLine());
                if (endDate.isBefore(startDate)) {
                    System.out.println("\nEnd date cannot be before start date. Please try again.");
                }
            } catch (DateTimeParseException e) {
                System.out.println("\n !! Invalid date format. Please enter a valid date in YYYY-MM-DD format.");
            }
        }

        promotion.setName(name);
        promotion.setDescription(description);
        promotion.setDiscountPercentage(discount);
        promotion.setStartDate(startDate);
        promotion.setEndDate(endDate);
        promotion.setTargetAddonType(addonType);
        promotion.setTargetAddonName(targetAddonName);

        System.out.println("\nOffer details updated successfully.");
    }

    private static boolean getBooleanInput() {
        String input = scanner.nextLine();
        return Boolean.parseBoolean(input);
    }

    private static void viewAllOffers() {
        System.out.println("\n--- All Offers ---");

        // Retrieve promotions from the repository
        List<Promotion> promotions = appRepository.getPromotions();

        // Check if promotions list is empty
        if (promotions.isEmpty()) {
            System.out.println("\nNo offers found.");
            return;
        }

        // Iterate through the promotions list
        for (int i = 0; i < promotions.size(); i++) {
            Promotion promotion = promotions.get(i);

            // Display active offers
            if (promotion.isActive()) {
                System.out.println("Active Offer: " + (i + 1));
                System.out.printf("%d. %-15s %5.2f%% off - %s\n",
                        i + 1,
                        promotion.getName(),
                        promotion.getDiscountPercentage(),
                        promotion.getDescription()
                );
            }
            // Display expired offers
            else {
                System.out.println("Expired Offer: " + (i + 1));
                System.out.printf("%d. %-15s %5.2f%% off - %s\n",
                        i + 1,
                        promotion.getName(),
                        promotion.getDiscountPercentage(),
                        promotion.getDescription()
                );
            }
        }
    }

    //! Add New Offer
    private static void addNewOffer() {
        System.out.println("\n--- Add New Offer ---");
        System.out.print("\nEnter offer name: ");
        String name = scanner.nextLine();
        System.out.print("Enter offer description: ");
        String description = scanner.nextLine();
        System.out.print("Enter Discount Percentage: ");
        double discount = getDoubleInput();

        System.out.println("\nSelect addon type for promotion:");
        System.out.println("1. Topping");
        System.out.println("2. Crust");
        System.out.println("3. Sauce");
        System.out.println("4. Cheese");
        System.out.print("Enter choice: ");
        int addonChoice = getIntInput();

        String addonType;
        switch (addonChoice) {
            case 1 -> addonType = "topping";
            case 2 -> addonType = "crust";
            case 3 -> addonType = "sauce";
            case 4 -> addonType = "cheese";
            default -> {
                System.out.println("Invalid choice.");
                return;
            }
        }

        // Fetch the available addons to allow selection
        List<PizzaAddon> addons = AppRepository.getInstance().getAddonsByType(addonType);
        if (addons.isEmpty()) {
            System.out.println("No addons available for the selected type.");
            return;
        }

        System.out.println("\nAvailable addons:");
        for (int i = 0; i < addons.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, addons.get(i).getType());
        }
        System.out.print("Select the specific addon to apply promotion: ");
        int addonIndex = getIntInput() - 1;

        if (addonIndex < 0 || addonIndex >= addons.size()) {
            System.out.println("\nInvalid addon selection.");
            return;
        }
        String targetAddonName = addons.get(addonIndex).getType();

        LocalDate startDate = null;
        LocalDate endDate = null;

        // Prompt for start date
        while (startDate == null) {
            System.out.print("\nEnter start date (YYYY-MM-DD): ");
            try {
                startDate = LocalDate.parse(scanner.nextLine());
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please enter a valid date in YYYY-MM-DD format.");
            }
        }

        // Prompt for end date
        while (endDate == null || endDate.isBefore(startDate)) {
            System.out.print("Enter end date (YYYY-MM-DD): ");
            try {
                endDate = LocalDate.parse(scanner.nextLine());
                if (endDate.isBefore(startDate)) {
                    System.out.println("\nEnd date cannot be before start date. Please try again.");
                }
            } catch (DateTimeParseException e) {
                System.out.println("\n !! Invalid date format. Please enter a valid date in YYYY-MM-DD format.");
            }
        }

        Promotion promotion = new Promotion(name, description, discount, startDate, endDate, addonType, targetAddonName);
        AppRepository.getInstance().addPromotion(promotion);
        System.out.println("Promotion added successfully.");
    }

    //! Update Addons Selection
    private static void updateAddons() {
        while (true) {
            System.out.println("\n--- Update Addons ---");
            System.out.println("1. Topping");
            System.out.println("2. Crust");
            System.out.println("3. Cheese");
            System.out.println("4. Sauce");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter your choice: ");
            int choice = getIntInput();

            switch (choice) {
                case 1 -> updateAddonList(
                        appRepository.getAvailableToppings(), //! Pass the toppings list
                        "Toppings",
                        name -> new Topping(name, 0.0) // Lambda to create a Topping instance
                );
                case 2 -> updateAddonList(
                        appRepository.getAvailableCrusts(), // Pass the crusts list
                        "Crusts",
                        name -> new Crust(name, 0.0) // Lambda to create a Crust instance
                );
                case 3 -> updateAddonList(
                        appRepository.getAvailableCheeses(), // Pass the cheeses list
                        "Cheeses",
                        name -> new Cheese(name, 0.0) // Lambda to create a Cheese instance
                );
                case 4 -> updateAddonList(
                        appRepository.getAvailableSauces(), // Pass the sauces list
                        "Sauces",
                        name -> new Sauce(name, 0.0) // Lambda to create a Sauce instance
                );
                case 5 -> {
                    System.out.println("Returning to Main Menu...");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    //! Update Addon List Options
    private static void updateAddonList(List<? extends PizzaAddon> addonsList, String addonType, Function<String, PizzaAddon> addonCreator) {
        while (true) {
            System.out.println("\n--- Update " + addonType + " ---");
            System.out.println("1. View All " + addonType);
            System.out.println("2. Add New " + addonType);
            System.out.println("3. Edit Existing " + addonType);
            System.out.println("4. Remove " + addonType);
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter your choice: ");
            int choice = getIntInput();

            switch (choice) {
                case 1 -> {
                    System.out.println("\nAvailable " + addonType + ":");
                    if (addonsList.isEmpty()) {
                        System.out.println("No " + addonType + " available.");
                    } else {
                        for (int i = 0; i < addonsList.size(); i++) {
                            System.out.println((i + 1) + ". " + addonsList.get(i).getType() + " - $" + addonsList.get(i).getPrice());
                        }
                    }
                }
                case 2 -> {
                    System.out.print("Enter name for new " + addonType + ": ");
                    String name = getStringInput();
                    System.out.print("Enter price for new " + addonType + ": ");
                    double price = getDoubleInput();

                    PizzaAddon newAddon = addonCreator.apply(name);
                    newAddon.setPrice(price);
                    ((List<PizzaAddon>) addonsList).add(newAddon); // Explicit cast to modify list.
                    System.out.println(addonType + " added successfully.");
                }
                case 3 -> {
                    System.out.print("Enter the number of the " + addonType + " to edit: ");
                    int index = getIntInput() - 1;
                    if (index >= 0 && index < addonsList.size()) {
                        System.out.print("Enter new name for " + addonsList.get(index).getType() + ": ");
                        String newType = getStringInput();
                        System.out.print("Enter new price for " + addonsList.get(index).getType() + ": ");
                        double newPrice = getDoubleInput();

                        addonsList.get(index).setType(newType);
                        addonsList.get(index).setPrice(newPrice);
                        System.out.println(addonType + " updated successfully.");
                    } else {
                        System.out.println("Invalid selection.");
                    }
                }
                case 4 -> {
                    System.out.print("Enter the number of the " + addonType + " to remove: ");
                    int index = getIntInput() - 1;
                    if (index >= 0 && index < addonsList.size()) {
                        System.out.println(addonsList.get(index).getType() + " removed successfully.");
                        ((List<PizzaAddon>) addonsList).remove(index); // Explicit cast to modify list.
                    } else {
                        System.out.println("Invalid selection.");
                    }
                }
                case 5 -> {
                    System.out.println("Returning to Main Menu...");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }


    private static int getIntInput() {
        Scanner scanner = new Scanner(System.in);
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next();
        }
        return scanner.nextInt();
    }

    private static String getStringInput() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine().trim();
    }

    private static double getDoubleInput() {
        Scanner scanner = new Scanner(System.in);
        while (!scanner.hasNextDouble()) {
            System.out.println("Invalid input. Please enter a valid number.");
            scanner.next();
        }
        return scanner.nextDouble();
    }
}
