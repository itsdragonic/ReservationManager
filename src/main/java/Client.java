import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {
    private static Scanner scanner = new Scanner(System.in);
    private static User currentUser;
    private static boolean running = true;

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

            writer.println("login");
            writer.println(username);
            writer.println(password);
            writer.flush();

            String result = reader.readLine();

            if (result.equals("success")) {
                currentUser = new User("", "", new ArrayList<Reservation>(), 0);
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

            writer.println("signUp");
            writer.println(username);
            writer.println(password);
            writer.flush();

            String result = reader.readLine();

            if (result.equals("success")) {
                currentUser = new User("", "", new ArrayList<Reservation>(), 0);
                System.out.println("Sign up successful!");
            } else {
                System.out.println("Sign up failed. Username may already exist, password may be less than 8 characters, " +
                        "or input is invalid.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

            // Get available tables, then display them here
            writer.println("showTables");
            writer.println(date);
            writer.println(Integer.toString(time));
            writer.flush();

            System.out.println(reader.readLine());

            int table;
            try {
                table = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid table number. Reservation cancelled.");
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void removeReservation(Socket socket) {

    }

    public static void viewReservations(Socket socket) {

    }

    public static void deleteAccount(Socket socket) {

    }

    public static void main (String[] args) {
        try {
            Socket socket = new Socket("localhost", 1234);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());

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


            /*
            System.out.println("What do you want to send to the server?");
            String response = scan.nextLine();

            writer.write(response);
            writer.println();
            writer.flush();
            System.out.printf("Sent to server:\n%s\n", response);

            String s1 = reader.readLine();
            System.out.printf("Recieved from server:\n%s\n", s1);
            */
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
