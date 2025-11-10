import java.util.Scanner;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    private static Database db = new Database();
    private static ArrayList<User> users = new ArrayList<>();
    private static String currentUser;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Welcome to Reservation Manager!");

        while (true) {
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
                System.exit(0);
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

        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                currentUser = username;
                System.out.println("Login successful! Welcome, " + username);
                return;
            }
        }
        System.out.println("Invalid username or password.");
    }

    private static void signup() {
        System.out.print("Enter new username: ");
        String username = scanner.nextLine();

        for (User user : users) {
            if (user.getUsername().equals(username)) {
                System.out.println("Username already exists.");
                return;
            }
        }

        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        // Use the 4-arg constructor that exists in your User class
        users.add(new User(username, password, new ArrayList<Reservation>(), 0));
        System.out.println("Sign up successful! You can now log in.");
    }

    private static void showMainMenu() {
        System.out.println("\n1. Add Reservation");
        System.out.println("2. Remove Reservation");
        System.out.println("3. View All Reservations");
        System.out.println("4. Logout");
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

        // Correct parameter order: (String username, int tableNum, String day, int time, int partySize)
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

    private static void viewReservations() {
        System.out.println("\nAll Reservations (from reservations.txt):");
        // Database does not expose a getAllReservations() method, so read the file directly.
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