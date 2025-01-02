package edu.kingston.cli;

import edu.kingston.domain.user.User;
import edu.kingston.repository.AppRepository;

import java.util.Scanner;

public class MainCLI {
    private static Scanner scanner = new Scanner(System.in);

     static void mainMenu() {
        CustomerCLI.initializeMenu();
        System.out.println("\n+---------------------------------------+");
        System.out.println("|        Welcome to NAPPOLY PIZZA       |");
        System.out.println("+---------------------------------------+");
        System.out.println("\n 1. Login           [#1]");
        System.out.println(" 2. Register        [#2]");
        System.out.println(" 3. Exit            [#3]");

        System.out.print("\nEnter your choice: ");
        int choice = getIntInput();
        switch (choice) {
            case 1 -> loginMenu();
            case 2 -> registerMenu();
            case 3 -> {
                System.out.println("Goodbye!");
                System.exit(0);
            }
            default -> System.out.println("Invalid choice. Please try again.");
        }
    }

    private static void loginMenu() {
        System.out.println("\n--- Login ---");
        System.out.print("\nEnter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        // Validate credentials
        User user = AppRepository.getInstance().getUsers().stream()
                .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
                .findFirst().orElse(null);

        if (user == null) {
            System.out.println("\nInvalid credentials. Try again.");
            mainMenu();
            return;
        }

        // Redirect to appropriate menu
        if (user.getRole().equalsIgnoreCase("ADMIN")) {
            AdminCLI.mainMenu();

        } else {
            System.out.println("\nLogin successful!");
            System.out.println("\nWelcome, " + user.getUsername() + "!");
            CustomerCLI pizzaCLI = new CustomerCLI(user);
            pizzaCLI.mainMenu();
        }
    }

    private static void registerMenu() {
        System.out.println("\n--- Register ---");
        System.out.print("\nEnter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        String role = "USER";

        User newUser = new User(username, email, password, role);
        AppRepository.getInstance().addUser(newUser);
        System.out.println("Registration successful! Please login.");
        mainMenu();
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

    public static void main(String[] args) {
        mainMenu();
    }
}
