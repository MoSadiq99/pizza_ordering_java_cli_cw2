package edu.kingston.cli;

import edu.kingston.domain.order.Order;
import edu.kingston.domain.order.OrderStatus;
import edu.kingston.domain.order.OrderStatusNotifier;
import edu.kingston.domain.payment.CreditCardPayment;
import edu.kingston.domain.payment.DigitalWalletPayment;
import edu.kingston.domain.payment.LoyaltyProgram;
import edu.kingston.domain.payment.PaymentStrategy;
import edu.kingston.domain.pizza.Pizza;
import edu.kingston.domain.pizza.Promotion;
import edu.kingston.domain.pizza.addon.*;
import edu.kingston.domain.pizza.handler.*;
import edu.kingston.domain.user.FeedbackSystem;
import edu.kingston.domain.user.User;
import edu.kingston.domain.user.command.Command;
import edu.kingston.domain.user.command.CommandManager;
import edu.kingston.domain.user.command.PlaceOrderCommand;
import edu.kingston.domain.user.command.SubmitFeedbackCommand;
import edu.kingston.repository.AppRepository;
import edu.kingston.service.MappingService;
import edu.kingston.service.NotificationService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class CustomerCLI {
    private static final Scanner scanner = new Scanner(System.in);
    private static User currentUser;
    private static final AppRepository appRepository = AppRepository.getInstance();
    private static CommandManager commandManager = new CommandManager();

    private static boolean isMenuInitialized = false;

    public CustomerCLI(User user) {
        currentUser = user;
    }

    //! Main Menu
    public void mainMenu() {
        while (true) {
            System.out.println("\n+-----------------------------+");
            System.out.println("|         NAPPOLY PIZZA       |");
            System.out.println("+-----------------------------+");
            System.out.println("|        Taste the Best       |");
            System.out.println("+-----------------------------+\n");
            System.out.println("1. Create Custom Pizza     [#1]");
            System.out.println("2. View Top Rated Pizzas   [#2]");
            System.out.println("3. View Favourite Pizzas   [#3]");
            System.out.println("4. Place Order             [#4]");
            System.out.println("5. View Order History      [#5]");
            System.out.println("6. Order Tracking          [#6]");
            System.out.println("7. Loyalty Program         [#7]");
            System.out.println("8. User Actions            [#8]");
            System.out.println("9. Logout                  [#9]");
            System.out.println("10. Exit                   [#10]");
            System.out.print("\nEnter your choice: ");

            int choice = getIntInput();
            switch (choice) {
                case 1 -> createCustomPizza();
                case 2 -> viewTopRatedPizzas();
                case 3 -> viewFavouritePizzas();
                case 4 -> placeOrder();
                case 5 -> {
                    viewOrderHistory();
                    System.out.print("Would you like to provide feedback on an order? (y/n): ");
                    String feedbackChoice = scanner.nextLine().trim().toLowerCase();
                    if (feedbackChoice.equals("y")) {
                        submitFeedback();
                    }
                }
                case 6 -> dynamicOrderTracking();
                case 7 -> viewLoyaltyProgram();
                case 8 -> userActionsMenu();
                case 9 -> MainCLI.mainMenu();
                case 10 -> {
                    System.out.println("Thank you for using Pizza Ordering System. Goodbye!");
                    return;
                }
                default -> System.out.println("\n!!! Invalid choice. Please try again. !!!");
            }
        }
    }

    // Create Custom Pizza Method
    private static void createCustomPizza() {
        if (currentUser == null) {
            System.out.println("Please login first!");
            return;
        }

        Pizza.PizzaBuilder pizzaBuilder = new Pizza.PizzaBuilder();

        // Chain of Responsibility Pattern Setup with added handlers
        PizzaCustomizationHandler crustHandler = new CrustCustomizationHandler();
        PizzaCustomizationHandler sauceHandler = new SauceCustomizationHandler();
        PizzaCustomizationHandler cheeseHandler = new CheeseCustomizationHandler();
        PizzaCustomizationHandler toppingHandler = new ToppingCustomizationHandler();

        // Crust Selection
        System.out.println("---------------------------");
        System.out.println("--- Create Custom Pizza ---");
        System.out.println("---------------------------");
        System.out.println("\n\t\t--- Select Crust ---\n");
        System.out.printf("%-20s %10s\n", "\tCrust", "Price");
        System.out.println("---------------------------------------");
        listAddonsWithPromotions(AppRepository.getInstance().getAvailableCrusts(), AppRepository.getInstance().getPromotions());
        while (true) {
            System.out.print("\nChoose crust: ");
            int crustChoice = getIntInput() - 1;

            try {
                pizzaBuilder.crust(AppRepository.getInstance().getAvailableCrusts().get(crustChoice));
                break;
            } catch (IndexOutOfBoundsException e) {
                System.out.println("\n!!! Invalid choice. Please try again. !!!");
            }
        }

        // Before moving to the next selection, validate the crust using Chain of Responsibility
        if (!crustHandler.handle(pizzaBuilder)) {
            return;
        }

        // Sauce Selection
        System.out.println("\n\t\t--- Select Sauce ---\n");
        System.out.printf("%-20s %10s\n", "\tSauce", "Price");
        System.out.println("---------------------------------------");
        listAddonsWithPromotions(AppRepository.getInstance().getAvailableSauces(), AppRepository.getInstance().getPromotions());
        while (true) {
            System.out.print("\nChoose sauce: ");
            int sauceChoice = getIntInput() - 1;

            try {
                pizzaBuilder.sauce(appRepository.getAvailableSauces().get(sauceChoice));
                break;
            } catch (IndexOutOfBoundsException e) {
                System.out.println("\n!!! Invalid choice. Please try again. !!!");
            }
        }

        // Validate sauce selection via Chain of Responsibility
        if (!sauceHandler.handle(pizzaBuilder)) {
            return; // Stop if validation fails
        }

        // Cheese Selection
        System.out.println("\n\t\t--- Select Cheese ---\n");
        System.out.printf("%-20s %10s\n", "\tCheese", "Price");
        System.out.println("---------------------------------------");
        listAddonsWithPromotions(AppRepository.getInstance().getAvailableCheeses(), AppRepository.getInstance().getPromotions());
        while (true) {
            System.out.print("\nChoose cheese: ");
            int cheeseChoice = getIntInput() - 1;

            try {
                pizzaBuilder.cheese(appRepository.getAvailableCheeses().get(cheeseChoice));
                break;
            } catch (IndexOutOfBoundsException e) {
                System.out.println("\n!!! Invalid choice. Please try again. !!!");
            }
        }

        // Validate cheese selection via Chain of Responsibility
        if (!cheeseHandler.handle(pizzaBuilder)) {
            return; // Stop if validation fails
        }

        // Topping Selection (validate via Chain of Responsibility)
        List<Topping> selectedToppings = new ArrayList<>();
        while (true) {
            System.out.println("\n\t" + "--- Select Toppings (Max 5) ---\n");
            listAddonsWithPromotions(AppRepository.getInstance().getAvailableToppings(), AppRepository.getInstance().getPromotions());
            System.out.println("0. Finish Topping Selection");

            System.out.print("\nChoose topping (or 0 to finish): ");
            int toppingChoice = getIntInput();

            if (toppingChoice == 0) break;

            if (toppingChoice < 1 || toppingChoice > appRepository.getAvailableToppings().size()) {
                System.out.println("\n!! Invalid choice !!");
                continue;
            }

            Topping selectedTopping = appRepository.getAvailableToppings().get(toppingChoice - 1);

            if (selectedToppings.contains(selectedTopping)) {
                System.out.println("\nTopping already added!");
                continue;
            }

            if (selectedToppings.size() >= 5) {
                System.out.println("\n!!! Maximum 5 toppings allowed !!!");
                break;
            }

            selectedToppings.add(selectedTopping);
        }


        pizzaBuilder.toppings(selectedToppings);

        // Validate toppings selection via Chain of Responsibility
        if (!toppingHandler.handle(pizzaBuilder)) {
            return; // Stop if validation fails
        }

        // Name the Pizza
        System.out.print("\nName your pizza: ");
        String pizzaName = scanner.nextLine();
        pizzaBuilder.name(pizzaName);

        // Build the Pizza
        Pizza customPizza = pizzaBuilder.build();
        appRepository.addFavouritePizza(customPizza);
        currentUser.addFavoritePizza(customPizza);

        System.out.println("\n*** Pizza Created Successfully ***\n");
        System.out.println("-----------------------------------");
        System.out.printf("%-15s : %s\n", "Pizza Name", customPizza.getName());
        System.out.printf("%-15s : $%.2f\n", "Total Price", customPizza.calculateTotalPrice());
        System.out.println("-----------------------------------");
    }

    //! View Top Rated Pizzas
    private void viewTopRatedPizzas() {
        List<Pizza> topPizzas = AppRepository.getInstance().getFeedbackSystem().getTopRatedPizzas(5); // Top 5 pizzas
        if (topPizzas.isEmpty()) {
            System.out.println("\nNo feedback available yet to determine top-rated pizzas.");
            return;
        }
        System.out.println("\n--- Top Rated Pizzas ---");
        for (int i = 0; i < topPizzas.size(); i++) {
            Pizza pizza = topPizzas.get(i);
            double averageRating = AppRepository.getInstance().getFeedbackSystem().calculateAverageRating(pizza);
            System.out.printf("%d. %s (Avg. Rating: %.2f)\n", i + 1, pizza.getName(), averageRating);
        }
    }

    //! View Favourite Pizzas
    private static void viewFavouritePizzas() {
        if (appRepository.getFavouritePizzas().isEmpty()) {
            System.out.println("\n!! No favourite pizzas found !!\n");
            return;
        }

        System.out.println("\n--- Your Favourite Pizzas ---\n");
        System.out.printf("%-5s %-20s %-10s\n", "No.", "Pizza Name", "Price ($)");
        System.out.println("------------------------------------------");

        for (int i = 0; i < appRepository.getFavouritePizzas().size(); i++) {
            Pizza pizza = appRepository.getFavouritePizzas().get(i);
            System.out.printf("%-5d %-20s %-10.2f\n",
                    i + 1,
                    pizza.getName(),
                    pizza.calculateTotalPrice()
            );
        }
        System.out.println("------------------------------------------");
    }

    //! Place Order
    public static void placeOrder() {
        if (currentUser == null) {
            System.out.println("\nPlease login first!");
            return;
        }

        System.out.println("\n+----------------------+\n|--- Pizza Ordering ---|\n+----------------------+");

        List<Pizza> orderPizzas = new ArrayList<>();

        //? Pizza Selection
        while (true) {
            viewFavouritePizzas();
            if (appRepository.getFavouritePizzas().isEmpty()) {
                createCustomPizza();
                viewFavouritePizzas();
            }
            if (!appRepository.getFavouritePizzas().isEmpty()) {
                System.out.println("\nEnter 'a' to Add a new Custom Pizza");
                System.out.println("Enter 'q' to Cancel Order");
                System.out.println("Enter 'f' to Finish Ordering");
                System.out.print("Add Pizza from your favourites to Order by its number: ");
            }

            String pizzaChoiceStr = scanner.nextLine();

            if (pizzaChoiceStr.equalsIgnoreCase("f")) break;

            if (pizzaChoiceStr.equalsIgnoreCase("a")) {
                createCustomPizza();
                continue;
            }

            if (pizzaChoiceStr.equalsIgnoreCase("q")) {
                System.out.println("\n!! Order cancelled !!");
                return;
            }

            try {
                int pizzaChoice = Integer.parseInt(pizzaChoiceStr);

                if (pizzaChoice < 1 || pizzaChoice > appRepository.getFavouritePizzas().size()) {
                    System.out.println("\n!!! Invalid choice !!!");
                    continue;
                }

                orderPizzas.add(appRepository.getFavouritePizzas().get(pizzaChoice - 1));
                Pizza selectedPizza = appRepository.getFavouritePizzas().get(pizzaChoice - 1);
                System.out.printf("\nPizza added to order: %s ($%.2f)\n",
                        selectedPizza.getName(), selectedPizza.calculateTotalPrice());

                //? List Selected Pizzas and Total Amount after each addition
                System.out.println("\n--- Selected Pizzas ---");
                double totalAmount = 0.0;
                for (int i = 0; i < orderPizzas.size(); i++) {
                    Pizza pizza = orderPizzas.get(i);
                    double pizzaPrice = pizza.calculateTotalPrice();
                    totalAmount += pizzaPrice;
                    System.out.printf("%d. %s - $%.2f\n", i + 1, pizza.getName(), pizzaPrice);
                }
                System.out.printf("\nCurrent Total Amount: $%.2f\n", totalAmount);
            } catch (NumberFormatException e) {
                System.out.println("\nInvalid choice! Please enter a valid number or option.");
            }
        }

        if (orderPizzas.isEmpty()) {
            System.out.println("\n!! Order cancelled !!");
            return;
        }

        //? Final List of Selected Pizzas and Total Amount
        System.out.println("\n--- Final Selected Pizzas ---");
        double totalAmount = 0.0;
        for (int i = 0; i < orderPizzas.size(); i++) {
            Pizza pizza = orderPizzas.get(i);
            double pizzaPrice = pizza.calculateTotalPrice();
            totalAmount += pizzaPrice;
            System.out.printf("%d. %s - $%.2f\n", i + 1, pizza.getName(), pizzaPrice);
        }
        System.out.printf("\nFinal Total Amount: $%.2f\n", totalAmount);


        //? Cancel Order
        if (orderPizzas.isEmpty()) {
            System.out.println("\n!! Order cancelled !!");
            return;
        }

        //? Order Type
        System.out.println("\n--- Select Order Type ---");
        System.out.println("1. Pickup");
        System.out.println("2. Delivery");
        System.out.print("\nChoose order type: ");

        int orderTypeChoice = getIntInput();
        Order.OrderType orderType = (orderTypeChoice == 2) ? Order.OrderType.DELIVERY : Order.OrderType.PICKUP;

        //? Create Order
        Order order;
        try {
            order = new Order.OrderBuilder()
                    .customer(currentUser)
                    .pizzas(orderPizzas)
                    .orderType(orderType)
                    .build();
        } catch (IllegalStateException e) {
            System.out.println("Error creating order: " + e.getMessage());
            return;
        }

        //? Apply Loyalty Points
        double discount = 0.0;

        if (currentUser.getLoyaltyProgram().getPoints() > 0) {
            System.out.println("\n--- Loyalty Points ---");
            System.out.printf("\nYou have %d loyalty points.\n", currentUser.getLoyaltyProgram().getPoints());
            System.out.printf("Order total: $%.2f\n", order.calculateTotal());
            System.out.print("Enter loyalty points to redeem (or 0 to skip): ");
            int pointsToRedeem = getIntInput();

            try {
                if (pointsToRedeem > 0) {
                    discount = currentUser.getLoyaltyProgram().redeemPointsForDiscount(order.calculateTotal(), pointsToRedeem);
                    System.out.printf("Discount applied: $%.2f\n", discount);
                }
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("\nNo loyalty points available for redemption.");
        }

        double remainingAmount = order.calculateTotal() - discount;

        //? Select Payment Method
        PaymentStrategy paymentMethod = null;
        if (remainingAmount > 0) {
            System.out.println("\n--- Payment Method ---");
            System.out.println("1. Credit Card");
            System.out.println("2. Digital Wallet");
            System.out.print("\nChoose payment method: ");
            int paymentChoice = getIntInput();

            switch (paymentChoice) {
                case 1 -> paymentMethod = new CreditCardPayment(1, "1234-5678-9012-3456", "123");
                case 2 -> paymentMethod = new DigitalWalletPayment(2, "wallet123");
                default -> {
                    System.out.println("Invalid payment method.");
                    return;
                }
            }
        }

        if (paymentMethod != null) {
            order.setPaymentMethod(paymentMethod);
        }
        order.setTotalAmountAfterDiscount(remainingAmount);

        //? Process Order with Command Pattern
        PlaceOrderCommand placeOrderCommand = new PlaceOrderCommand(order, currentUser);
        commandManager.executeCommand(placeOrderCommand);

        //? Add Order to Repository
        AppRepository.getInstance().addOrder(order);

        //? Confirmation
        System.out.println("\nOrder placed successfully!\n");
        System.out.printf( "%-25s $%.2f\n", "Order Total: ", order.calculateTotal());
        System.out.printf( "%-25s $%.2f\n", "Discount: ", discount);
        System.out.printf( "%-25s $%.2f\n", "Final Amount: ", order.getTotalAmountAfterDiscount());

        //? Get delivery type and estimated delivery time
        if (orderType == Order.OrderType.DELIVERY) {
            System.out.print("\nEnter your address: ");
            String userAddress = scanner.nextLine();
            String shopAddress = "1600 Amphitheatre Parkway, Mountain View, CA 94043, USA";

            MappingService mappingService = new MappingService();
            String deliveryEstimate = mappingService.getDeliveryEstimate(shopAddress, userAddress);

            System.out.printf("\nEstimated delivery time: %s\n", deliveryEstimate);
        }

        // ? Real-time Tracking
        System.out.print("\nWould you like to track your order in real-time? (y/n): ");
        String choice = scanner.nextLine().trim().toLowerCase();
        if (choice.equals("y")) {
            dynamicOrderTracking(order); // Call real-time tracking after order placement
        }
    }

    //! View Order History
    private static void viewOrderHistory() {
        if (currentUser == null) {
            System.out.println("Please login first!");
            return;
        }

        List<Order> orderHistory = currentUser.getOrderHistory();
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

    //! Submit Feedback
    private void submitFeedback() {
        viewOrderHistory(); // Allow user to select an order
        System.out.print("Enter order number to provide feedback: ");
        int orderIndex = getIntInput() - 1;

        if (orderIndex < 0 || orderIndex >= currentUser.getOrderHistory().size()) {
            System.out.println("Invalid order selection.");
            return;
        }

        Order order = currentUser.getOrderHistory().get(orderIndex);

        System.out.print("Enter rating (1-5): ");
        int rating = getIntInput();

        if (rating < 1 || rating > 5) {
            System.out.println("Rating must be between 1 and 5.");
            return;
        }

        System.out.print("Enter your comment: ");
        String comment = scanner.nextLine();

        // Create and execute the feedback command
        try {
            FeedbackSystem feedbackSystem = AppRepository.getInstance().getFeedbackSystem();
            SubmitFeedbackCommand feedbackCommand = new SubmitFeedbackCommand(feedbackSystem, order, rating, comment);

            commandManager.executeCommand(feedbackCommand);

            System.out.println("Thank you for your feedback!");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    //! View Loyalty Program
    private static void viewLoyaltyProgram() {
        if (currentUser == null) {
            System.out.println("Please login first!");
            return;
        }

        LoyaltyProgram loyaltyProgram = currentUser.getLoyaltyProgram();
        if (loyaltyProgram == null) {
            System.out.println("You are not enrolled in the loyalty program.");
            return;
        }

        System.out.println("\n--- Loyalty Program ---");
        System.out.printf("Points Earned: %d\n", loyaltyProgram.getPoints());
//        System.out.printf("Discount Available: $%.2f\n", loyaltyProgram.getDiscount());

        System.out.println("\nWould you like to redeem your points for a discount?");
        System.out.println("1. Yes");
        System.out.println("2. No");
        System.out.print("Enter your choice: ");
        int choice = getIntInput();

        if (choice == 1) {
            try {
//                double discount = loyaltyProgram.redeemPoints();
//                System.out.printf("You have redeemed your points for a $%.2f discount!\n", discount);
            } catch (Exception e) {
                System.out.println("Error redeeming points: " + e.getMessage());
            }
        } else {
            System.out.println("Returning to main menu.");
        }
    }

    //! Utility method to list addons with promotions
    public static void listAddonsWithPromotions(List<? extends PizzaAddon> addons, List<Promotion> promotions) {
        boolean hasPromotions = false;

        // Check if any addon in the group has a valid promotion
        for (PizzaAddon addon : addons) {
            for (Promotion promotion : promotions) {
                if (promotion.appliesToAddon(addon) && promotion.isWithinPromotionPeriod()) {
                    hasPromotions = true;
                    break;
                }
            }
            if (hasPromotions) break; // Exit early if we found a promotion
        }

        // Print the headers dynamically based on promotion applicability
        if (hasPromotions) {
            System.out.printf("%-5s %-20s %10s %15s\n", "No.", "Type", "Base Price", "Promo Price");
        } else {
            System.out.printf("%-5s %-20s %10s\n", "No.", "Type", "Base Price");
        }
        System.out.println("------------------------------------------------");

        // Iterate through addons and display their details
        int index = 1; // Initialize numbering
        for (PizzaAddon addon : addons) {
            double basePrice = addon.getPrice();
            double promoPrice = basePrice;

            // Check and apply promotions
            for (Promotion promotion : promotions) {
                if (promotion.appliesToAddon(addon) && promotion.isWithinPromotionPeriod()) {
                    promoPrice = promotion.applyDiscount(basePrice);
                    break;
                }
            }

            // Print details based on whether the addon has a promotion
            if (hasPromotions) {
                if (promoPrice < basePrice) {
                    System.out.printf("%-5d %-20s %10.2f %15.2f\n", index++, addon.getType(), basePrice, promoPrice);
                } else {
                    System.out.printf("%-5d %-20s %10.2f %15s\n", index++, addon.getType(), basePrice, "-");
                }
            } else {
                System.out.printf("%-5d %-20s %10.2f\n", index++, addon.getType(), basePrice);
            }
        }

        System.out.println("------------------------------------------------");
    }


    //! Order Tracking
    private static void dynamicOrderTracking(Order order) {


        NotificationService notificationService = new NotificationService();
        OrderStatusNotifier notifier = new OrderStatusNotifier(notificationService);
        order.addObserver(notifier);

        boolean keepTracking = true;

        while (keepTracking) {
            orderTracking(order);

            System.out.print("\nEnter 'q' to stop tracking or any other key to refresh: ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("q")) {
                keepTracking = false; // Break the loop
            } else {
                try {
                    Thread.sleep(100); // Refresh after 5 seconds
                } catch (InterruptedException e) {
                    System.err.println("Tracking interrupted: " + e.getMessage());
                    keepTracking = false; // Exit on interruption
                }
            }
        }

        System.out.println("Order tracking stopped.");
    }

    private static void orderTracking(Order order) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        System.out.println("\n--- Active Order Tracking ---");
        System.out.printf("Order ID: %s\n", order.getOrderId());
        System.out.println("---------------------------------------------");

        System.out.printf("%-20s | %-20s\n", "Status", "Timestamp");
        System.out.println("---------------------------------------------");

        for (OrderStatus status : OrderStatus.values()) {
            LocalDateTime timestamp = order.getStatusTimestamp(status);
            if (timestamp != null) {
                System.out.printf("%-20s | %s\n", status, timestamp.format(formatter));
            } else {
                System.out.printf("%-20s | Not yet updated\n", status);
            }
        }

        System.out.println("---------------------------------------------");
    }

    //? List tracking information for all orders
    private static void dynamicOrderTracking() {
        boolean keepTracking = true;

        while (keepTracking) {
            // Display the current tracking information
            orderTracking();

            // Prompt for user input
            System.out.print("\nPress Enter to refresh or enter 'q' to go back to the main menu: ");
            String input = scanner.nextLine().trim();

            // Handle user input
            if (input.equalsIgnoreCase("q")) {
                keepTracking = false; // Exit the loop
            } else {
                try {
                    Thread.sleep(100); // Add a slight delay before refreshing
                } catch (InterruptedException e) {
                    System.err.println("Tracking interrupted: " + e.getMessage());
                    keepTracking = false; // Exit on interruption
                }
            }
        }

        System.out.println("Returning to the main menu...");
    }

    private static void orderTracking() {
        // Check if there are no active orders
        if (AppRepository.getInstance().getOrders().isEmpty()) {
            System.out.println("\nNo active orders to track.");
            return;
        }

        // Define a formatter for the timestamps
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        System.out.println("\n--- Active Order Tracking ---");

        // Get and iterate through the orders
        List<Order> orders = AppRepository.getInstance().getOrders();
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);

            // Display order details
            System.out.printf("\nOrder %d: %s\n", i + 1, order.getOrderId());
            System.out.println("---------------------------------------------");
            System.out.printf("%-20s | %-20s\n", "Status", "Timestamp");
            System.out.println("---------------------------------------------");

            // Display statuses and timestamps
            for (OrderStatus status : OrderStatus.values()) {
                LocalDateTime timestamp = order.getStatusTimestamp(status);
                if (timestamp != null) {
                    System.out.printf("%-20s | %s\n", status, timestamp.format(formatter));
                } else {
                    System.out.printf("%-20s | Not yet updated\n", status);
                }
            }
            System.out.println("---------------------------------------------");
        }
    }

    //? Simulate Order Updates
    private static void simulateOrderUpdates(Order order) {
        new Thread(() -> {
            try {
                Thread.sleep(10000); // Wait 10 seconds for PLACED to PREPARING
                order.setStatus(OrderStatus.PREPARING);
                Thread.sleep(10000);
                order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
                Thread.sleep(15000);
                order.setStatus(OrderStatus.DELIVERED);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //! Utility method to handle integer input with error checking
    private static int getIntInput() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }

    public void userActionsMenu() {
        while (true) {
            System.out.println("\n+----------------------+");
            System.out.println("|---- User Actions ----|");
            System.out.println("+----------------------+\n");
            System.out.println("1. Submit Feedback");
            System.out.println("2. Undo Last Action");
            System.out.println("3. View Action Log");
            System.out.println("4. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = getIntInput();
            switch (choice) {
                case 1 -> submitFeedback();
                case 2 -> undoLastAction();
                case 3 -> viewActionLog();
                case 4 -> mainMenu();
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }


    // Undo Last Action
    private void undoLastAction() {
        System.out.println("\nAttempting to undo the last action...");
        if (commandManager.getActionLog().isEmpty()) {
            System.out.println("No actions to undo.");
            return;
        }
        Command lastCommand = commandManager.undoLastCommand();
        if (lastCommand != null) {
            System.out.println("Successfully undone: " + lastCommand.toString());
        } else {
            System.out.println("No actions were undone.");
        }
    }


    // View Action Log
    private void viewActionLog() {
        System.out.println("\n--- Action Log ---");
        List<String> logs = commandManager.getActionLog();
        if (logs == null || logs.isEmpty()) {
            System.out.println("No actions logged.");
        } else {
            System.out.println("Recent actions:");
            logs.forEach(System.out::println);
        }
    }

    //! Initialize options
    static void initializeMenu() {

        if (isMenuInitialized) {
            return; //To Skip if already initialized
        }
        
        appRepository.getAvailableCrusts().addAll(Arrays.asList(
                new Crust("Thin", 2.0),
                new Crust("Thick", 3.0),
                new Crust("Stuffed", 4.0)
        ));

        appRepository.getAvailableSauces().addAll(Arrays.asList(
                new Sauce("Classic Tomato", 1.5),
                new Sauce("Sweet Chilli", 2.0),
                new Sauce("White Garlic", 2.0),
                new Sauce("Spicy BBQ", 2.5)
        ));

        appRepository.getAvailableCheeses().addAll(Arrays.asList(
                new Cheese("Mozzarella", 2.5),
                new Cheese("Cheddar", 3.0),
                new Cheese("Parmesan", 3.5)
        ));

        appRepository.getAvailableToppings().addAll(Arrays.asList(
                new Topping("Pepperoni", 1.0),
                new Topping("Mushrooms", 0.75),
                new Topping("Olives", 0.50),
                new Topping("Bell Peppers", 0.75),
                new Topping("Onions", 0.50),
                new Topping("Sausage", 1.25)
        ));

        isMenuInitialized = true;
    }
}
