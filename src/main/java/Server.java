import java.io.*;
import java.net.*;
import java.util.ArrayList;

/**
 * Reservation Manager Project
 *
 * Server side interface for the project
 * Interacts with Client.java on the port 1234
 *
 * @author Jeremiah Tomasello
 *
 * @version November 18, 2025
 *
 */

public class Server {
    private static Database db = new Database();
    private static ArrayList<User> users = new ArrayList<>();
    private static boolean running = true;
    /*
    Must contain Thread-safe measures
     */


    public static void main(String[] args) {
        try {
            // Server setup
            ServerSocket serverSocket = new ServerSocket(1234);

            System.out.println("Waiting for the client to connect...");
            Socket socket = serverSocket.accept();
            System.out.println("Client connected!");

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());

            // Main code
            // Always checks for commands sent from Client
            // All commands (beginning & end) are documented w/ print statements
            while (true) {
                String command = reader.readLine();
                System.out.println("Client request: " + command);

                switch (command) {
                    case "login": {
                        String username = reader.readLine();
                        String password = reader.readLine();

                        // Check whether the inputted user credentials match any existing ones
                        User testUser = new User("", "", new ArrayList<Reservation>(), 0);
                        boolean ok = testUser.login(username, password);

                        // Send back result to Client
                        if (ok) {
                            writer.write("success");
                        } else {
                            writer.write("failure");
                        }
                        writer.println();
                        writer.flush();

                        System.out.println("Finished login");
                        break;
                    }

                    case "signUp": {
                        String username = reader.readLine();
                        String password = reader.readLine();

                        // Check whether the inputted user credentials match any existing ones
                        User newUser = new User(username, password, new ArrayList<Reservation>(), 0);
                        boolean success = newUser.createAccount(username, password);

                        // Send back result to Client
                        if (success) {
                            writer.write("success");
                        } else {
                            writer.write("failure");
                        }
                        writer.println();
                        writer.flush();

                        System.out.println("Finished sign up");
                        break;
                    }

                    case "addReservation": {
                        String name = reader.readLine();
                        String date = reader.readLine();
                        int time = Integer.parseInt(reader.readLine());
                        int guests = Integer.parseInt(reader.readLine());

                        // Only return tables that are unreserved AND the same number or greater seats per guest
                        for (Table t : db.getUnreservedTables(date, time)) {
                            for (int s = guests; s <= db.getBiggestSeating(); s++) {
                                for (Table t2 : db.getTablesBySeats(s)) {
                                    if (t.getTableNum() == t2.getTableNum()) {
                                        writer.write(String.format("Table: %d (%dS), ", t.getTableNum(), t.getSeats()));
                                    }
                                }
                            }
                        }

                        writer.println();
                        writer.flush();

                        int table = Integer.parseInt(reader.readLine());

                        // Send back result of adding the reservation to Client
                        Reservation res = new Reservation(name, table, date, time, guests);
                        if (db.addReservation(res)) {
                            writer.println("Reservation added successfully!");
                        } else {
                            writer.println("Failed to add reservation. Please try again");
                        }

                        writer.flush();

                        System.out.println("Finished adding reservation");
                        break;
                    }

                    case "removeReservation": {
                        String removedReservation = reader.readLine();
                        System.out.println(removedReservation);

                        // Test to see if you can remove the reservation identified
                        try {
                            db.cancelReservation(db.getReservation(removedReservation));
                            writer.println("Successfully removed reservation");
                        } catch (Exception e) {
                            writer.println("Failed to remove reservation. Please try again");
                        }

                        writer.flush();

                        System.out.println("Finished removing reservation");
                        break;
                    }

                    case "viewReservations": {
                        String username = reader.readLine();
                        String allReservations = "";

                        // Iterate through all reservations and format them to be sent back to Client
                        int reservationCounter = 1;
                        for (Reservation r : db.getReservations()) {
                            if (r.getName().equals(username)) {
                                allReservations += reservationCounter + ".) " + r.getReservationId() + ", ";
                                reservationCounter++;
                            }
                        }

                        // Check if there are no reservations
                        if (allReservations.equals("")) {
                            allReservations += "No reservations found";
                        } else {
                            allReservations = "Current reservations: " + allReservations;
                        }

                        // Send back resulting reservations (if any)
                        writer.println(allReservations);
                        writer.flush();

                        System.out.println("Finished viewing reservations");
                        break;
                    }

                    case "deleteAccount": {
                        String username = reader.readLine();
                        User tempUser = new User("", "", null, 0);

                        tempUser.deleteAccount(username);

                        // Iterating through the current reservations until all deleted account's reservations are gone
                        // There must be a for loop broken inside a while loop
                        // b/c for loop iterates through something that gets potentially edited each cycle changed
                        boolean foundReservation = true;
                        while (foundReservation) {
                            foundReservation = false;
                            for (Reservation r : db.getReservations()) {
                                if (r.getName().equals(username)) {
                                    db.cancelReservation(r);
                                    foundReservation = true;
                                    break;
                                }
                            }
                        }

                        //Conclude command on Client side
                        writer.println("Successfully deleted account");
                        writer.flush();

                        System.out.println("Finished deleting account");
                        break;
                    }

                    // Error code if a command isn't accounted for in the cases above
                    // More a debugging tool than anything else
                    default:
                        System.out.println("Something went wrong");
                        System.out.println("Unknown Command: " + command);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
