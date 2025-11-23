import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Reservation Manager Project
 *
 * Client side interface for the project
 * Interacts with Server.java on the port 1234
 * Most implementation of functions can be seen through corresponding switch cases in Server.java
 *
 * @author Jeremiah Tomasello
 *
 * @version November 18, 2025
 *
 */

public class Client {
    private static Scanner scanner = new Scanner(System.in);
    private static User currentUser;
    private static boolean running = true;

    // This menu is what is always first shown
    public static void signedOutMenu(Socket socket) {
        System.out.println("\n1. Login");
        System.out.println("2. Sign Up");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");

        String choice = scanner.nextLine();

        if (choice.equals("1")) {
            login(socket);
        } else if (choice.equals("2")) {
            signUp(socket);
        } else if (choice.equals("3")) {
            running = false;
        } else {
            System.out.println("Error! Please enter a valid option.");
        }
    }

    public static void login(Socket socket) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());

            // Send login info to Server to confirm
            writer.println("login");
            writer.println(username);
            writer.println(password);
            writer.flush();

            // Compare server output
            String result = reader.readLine();

            if (result.equals("success")) {
                currentUser = new User(username, password, new ArrayList<Reservation>(), 0);
                System.out.println("Login successful! Welcome, " + username);
            } else {
                System.out.println("Invalid username or password. Please try again");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void signUp(Socket socket) {
        System.out.print("Enter new username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());

            // Send sign up info to Server to confirm
            writer.println("signUp");
            writer.println(username);
            writer.println(password);
            writer.flush();

            // Compare Server output
            String result = reader.readLine();

            if (result.equals("success")) {
                currentUser = new User(username, password, new ArrayList<Reservation>(), 0);
                System.out.println("Sign up successful!");
            } else {
                System.out.println("Sign up failed. Username may already exist, password may be less than 8 characters, " +
                        "or input is invalid.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // This menu is always shown when a user is currently logged in
    public static void mainMenu(Socket socket) {
        System.out.println("\n1. Add Reservation");
        System.out.println("2. Remove Reservation");
        System.out.println("3. View All Reservations");
        System.out.println("4. Logout");
        System.out.println("5. Delete Account");
        System.out.print("Choose an option: ");

        String choice = scanner.nextLine();

        switch (choice) {
            case "1":
                addReservation(socket);
                break;
            case "2":
                removeReservation(socket);
                break;
            case "3":
                viewReservations(socket);
                break;
            case "4":
                currentUser = null;
                System.out.println("Logged out successfully.");
                break;
            case "5":
                deleteAccount(socket);
                break;
            default:
                System.out.println("Error! Please enter a valid option.");
        }
    }

    public static void addReservation(Socket socket) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());

            // Gathers reservation info to create it in Server
            System.out.print("Enter date (MM/DD/YYYY): ");
            String date = scanner.nextLine();
            boolean valid = ReservationValidator.validReservation(date);
            if (!valid) {
                return;
            }
            System.out.print("Enter time (24-hour format, e.g., 1900): ");
            String time = scanner.nextLine();
            boolean validTime = ReservationValidator.validTime(time, date);
            if (!validTime) {
                return;
            }
            int resTime = Integer.parseInt(time);
            if (resTime < 800 || resTime >= 2200) {
                System.out.println("Please selected a time between 8AM and 10PM");
                return;
            }
            System.out.print("Enter number of guests: ");
            int guests;
            try {
                guests = Integer.parseInt(scanner.nextLine());
                if (guests < 1 || guests > 12) {
                    System.out.println("Invalid guest number. Reservation cancelled.");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid guest number. Reservation cancelled.");
                return;
            }


            System.out.print("Available Tables: ");

            // Get available tables, then display them here
            writer.println("addReservation");
            // Send reservation info to server to create it
            // Must be sent now as the Server will cross-check the current reservation info with current reservations
            // This will allow for a curated display of available tables
            writer.println(currentUser.getUsername());
            writer.println(date);
            writer.println(resTime);
            writer.println(guests);
            writer.flush();

            System.out.println(reader.readLine());
            System.out.print("Please enter the table number requested: ");

            int table;
            try {
                table = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid table number. Reservation cancelled.");
                return;
            }

            // Send remaining reservation info
            writer.println(table);
            writer.flush();

            // Display Server output on reservation creation
            System.out.println(reader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void removeReservation(Socket socket) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());

            // Repeat of view reservations code
            writer.println("viewReservations");
            writer.println(currentUser.getUsername());
            writer.flush();

            String allReservations = reader.readLine();
            System.out.println(allReservations);

            if (allReservations.equals("No reservations found")) {
                return;
            }

            // Format current reservations so it's just their ID's (so they can be sent back to Server)
            String[] reservationStrings = allReservations.substring(25).split("\\)");
            for (int i = 0; i < reservationStrings.length; i++) {
                reservationStrings[i] = reservationStrings[i].substring(reservationStrings[i].indexOf(" ") + 1,
                                reservationStrings[i].indexOf(','));
            }

            System.out.print("Enter the reservation number you would like to remove: ");
            try {
                int removedReservationNum = Integer.parseInt(scanner.nextLine());

                //Check if reservationNum is a valid number
                if (removedReservationNum <= 0 || removedReservationNum > reservationStrings.length)
                    throw new NumberFormatException();

                // Send reservationID to server to be removed
                writer.println("removeReservation");
                writer.println(reservationStrings[removedReservationNum - 1]);
                writer.flush();

                System.out.println(reader.readLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid reservation number. Reservation removal cancelled.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void viewReservations(Socket socket) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());

            // Send username to Server so it can display appropriate reservations
            writer.println("viewReservations");
            writer.println(currentUser.getUsername());
            writer.flush();

            System.out.println(reader.readLine());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteAccount(Socket socket) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());

            // Confirm w/ user
            System.out.print("Are you sure?\n[y / n]: ");
            if (scanner.nextLine().toLowerCase().equals("n")) {
                return;
            }

            // Send correct user to be deleted to Server
            writer.println("deleteAccount");
            writer.println(currentUser.getUsername());
            writer.flush();

            currentUser = null;

            System.out.println(reader.readLine());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Main method
    public static void main (String[] args) {
        try {
            // Network setup
            Socket socket = new Socket("localhost", 1234);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());

            // Starter code to repeat showing a menu based on whether the user is logged in
            System.out.println("Welcome to Reservation Manager!");
            while (running) {
                if (currentUser == null) {
                    signedOutMenu(socket);
                } else {
                    mainMenu(socket);
                }
            }


            //Close Client tools
            System.out.println("Thank you for using the Reservation Manager!");
            writer.close();
            reader.close();
        } catch (IOException e) {
            System.out.println("Couldn't connect to server!");
            e.printStackTrace();
        }
    }
}
