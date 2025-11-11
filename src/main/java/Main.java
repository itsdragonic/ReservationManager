import java.util.Scanner;
import java.util.ArrayList;
import java.io.*;

/**
 * Main.java
 *
 * Current way for interacting with the database is through the console.
 *
 * @author Fredrick Zawislak
 * @version 11/10/2025
 */

public class Main {
    private static Database db = new Database();
    private static ArrayList<User> users = new ArrayList<>();
    private static String currentUser;
    private static Scanner scanner = new Scanner(System.in);
    private static boolean running = true;

    public static void main(String[] args) {
        System.out.println("Welcome to Reservation Manager!");

        while (running) {
            if (currentUser == null) {
                showLoginMenu();
            } else {
                showMainMenu();
            }
        }
    }

    private static void showLoginMenu() {
        System.out.println("\n1. Login");
        System.out.println("2. Sign Up");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");

        String choice = scanner.nextLine();

        switch (choice) {
            case "1":
                login();
                break;
            case "2":
                signup();
                break;
            case "3":
                System.out.println("Goodbye!");
                running = false;
                return;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }

    private static void login() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User temp = new User("", "", new ArrayList<Reservation>(), 0);
        boolean ok = temp.login(username, password);
        if (ok) {
            currentUser = username;
            boolean found = false;
            for (User u : users) {
                if (u.getUsername().equals(username)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                users.add(new User(username, password, new ArrayList<Reservation>(), 0));
            }
            System.out.println("Login successful! Welcome, " + username);
        } else {
            System.out.println("Invalid username or password.");
        }
    }

    private static void signup() {
        System.out.print("Enter new username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User newUser = new User(username, password, new ArrayList<Reservation>(), 0);
        boolean success = newUser.createAccount(username, password);

        if (success) {
            users.add(newUser);
            System.out.println("Sign up successful! You can now log in.");
        } else {
            System.out.println("Sign up failed. Username may already exist or input is invalid.");
        }
    }

    private static void showMainMenu() {
        System.out.println("\n1. Add Reservation");
        System.out.println("2. Remove Reservation");
        System.out.println("3. View All Reservations");
        System.out.println("4. Logout");
        System.out.println("5. Delete Account");
        System.out.print("Choose an option: ");

        String choice = scanner.nextLine();

        switch (choice) {
            case "1":
                addReservation();
                break;
            case "2":
                removeReservation();
                break;
            case "3":
                viewReservations();
                break;
            case "4":
                currentUser = null;
                System.out.println("Logged out successfully.");
                break;
            case "5":
                deleteAccountChoice();
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }

    private static void addReservation() {
        System.out.print("Enter customer name: ");
        String name = scanner.nextLine();
        System.out.print("Enter date (MM/DD/YYYY): ");
        String date = scanner.nextLine();
        System.out.print("Enter time (24-hour format, e.g., 1900): ");
        int time;
        try {
            time = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid time format. Reservation cancelled.");
            return;
        }
        System.out.print("Enter number of guests: ");
        int guests;
        try {
            guests = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid guest number. Reservation cancelled.");
            return;
        }
        System.out.print("Enter table number: ");
        int table;
        try {
            table = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid table number. Reservation cancelled.");
            return;
        }

        Reservation res = new Reservation(name, table, date, time, guests);
        if (db.addReservation(res)) {
            System.out.println("Reservation added successfully!");
        } else {
            System.out.println("Failed to add reservation.");
        }
    }

    private static void removeReservation() {
        System.out.print("Enter reservation ID to remove: ");
        String id = scanner.nextLine();
        Reservation toRemove = db.getReservation(id);
        if (toRemove == null) {
            System.out.println("Reservation not found.");
            return;
        }
        if (db.cancelReservation(toRemove)) {
            System.out.println("Reservation removed.");
        } else {
            System.out.println("Failed to remove reservation.");
        }
    }

    private static void deleteAccountChoice() {
        if (currentUser == null) {
            System.out.println("No one is logged in.");
            return;
        }
        System.out.print("Are you sure you want to delete your account? Type 'YES' to confirm: ");
        String confirm = scanner.nextLine();
        if (!"YES".equalsIgnoreCase(confirm)) {
            System.out.println("Account deletion cancelled.");
            return;
        }

        // Find user in memory (if present) and call deleteAccount
        User target = null;
        for (User u : users) {
            if (u.getUsername().equals(currentUser)) {
                target = u;
                break;
            }
        }
        // If not found in memory, create a temp User to call deleteAccount
        if (target == null) target = new User(currentUser, "", new ArrayList<Reservation>(), 0);

        boolean deleted = target.deleteAccount(currentUser);
        if (deleted) {
            users.removeIf(u -> u.getUsername().equals(currentUser));
            System.out.println("Account deleted successfully.");
            currentUser = null;
        } else {
            System.out.println("Account deletion failed (account may not exist).");
        }
    }

    private static void viewReservations() {
        System.out.println("\nAll Reservations (from reservations.txt):");
        try (BufferedReader br = new BufferedReader(new FileReader("reservations.txt"))) {
            String line;
            boolean any = false;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                any = true;
            }
            if (!any) {
                System.out.println("No reservations found.");
            }
        } catch (IOException e) {
            System.out.println("No reservations file found or error reading reservations.");
        }
    }
}