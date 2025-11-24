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

public class Server implements InterfaceServer, Runnable {
    private static Database db = new Database();      // Shared DB (synchronized)
    private Socket socket;                            // Each thread handles 1 client
    private static boolean running = true;

    // Constructor for each client thread
    public Server(Socket socket) {
        this.socket = socket;
    }

    public static void main(String[] args) {
        new Server(null).startServer();        // Only startServer should handle listening
    }

    @Override
    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(1234)) {
            System.out.println("Server started on port 1234");

            while (running) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected!");

                // Each client in its own thread
                Thread t = new Thread(new Server(clientSocket));
                t.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void endServer() {
        running = false;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );
            PrintWriter writer = new PrintWriter(socket.getOutputStream());

            while (true) {
                String command = reader.readLine();
                if (command == null) break;

                System.out.println("Client request: " + command);

                switch (command) {

                    case "login": {
                        String username = reader.readLine();
                        String password = reader.readLine();

                        // Check whether the inputted user credentials match any existing ones
                        User testUser = new User("", "", new ArrayList<Reservation>(), 0);
                        boolean ok;

                        synchronized (db) { // now thread-safe
                            ok = testUser.login(username, password);
                        }

                        writer.println(ok ? "success" : "failure");
                        writer.flush();

                        break;
                    }

                    case "signUp": {
                        String username = reader.readLine();
                        String password = reader.readLine();

                        // Check whether the inputted user credentials match any existing ones
                        User newUser = new User(username, password, new ArrayList<Reservation>(), 0);
                        boolean success;

                        synchronized (db) {
                            success = newUser.createAccount(username, password);
                        }

                        writer.println(success ? "success" : "failure");
                        writer.flush();
                        break;
                    }

                    case "getTimes": {
                        int open, close;
                        synchronized (db) {
                            open = db.getOpenTime();
                            close = db.getCloseTime();
                        }

                        writer.println(open + ":" + close);
                        writer.flush();
                        break;
                    }

                    case "addReservation": {
                        String name = reader.readLine();
                        String date = reader.readLine();
                        int time = Integer.parseInt(reader.readLine());
                        int guests = Integer.parseInt(reader.readLine());

                        ArrayList<Integer> availableTables = new ArrayList<>();

                        // Only return tables that are unreserved AND the same number or greater seats per guest
                        synchronized (db) {
                            for (Table t : db.getUnreservedTables(date, time)) {
                                for (int s = guests; s <= db.getBiggestSeating(); s++) {
                                    for (Table t2 : db.getTablesBySeats(s)) {
                                        if (t.getTableNum() == t2.getTableNum()) {
                                            writer.write(String.format("Table: %d (%dS), ",
                                                    t.getTableNum(), t.getSeats()));
                                            availableTables.add(t.getTableNum());
                                        }
                                    }
                                }
                            }
                        }

                        writer.println();
                        writer.flush();

                        int table = Integer.parseInt(reader.readLine());

                        // Send back result of adding the reservation to Client
                        Reservation res = new Reservation(name, table, date, time, guests);

                        synchronized (db) {
                            if (db.addReservation(res) && availableTables.contains(table)) {
                                writer.println("Reservation added successfully!");
                            } else {
                                writer.println("Failed to add reservation. Please try again");
                            }
                        }

                        writer.flush();
                        break;
                    }

                    case "removeReservation": {
                        String resId = reader.readLine();

                        // Test to see if you can remove the reservation identified
                        synchronized (db) {
                            try {
                                db.cancelReservation(db.getReservation(resId));
                                writer.println("Successfully removed reservation");
                            } catch (Exception e) {
                                writer.println("Failed to remove reservation. Please try again");
                            }
                        }

                        writer.flush();
                        break;
                    }

                    case "viewReservations": {
                        String username = reader.readLine();
                        String allReservations = "";

                        // Iterate through all reservations and format them to be sent back to Client
                        synchronized (db) {
                            int n = 1;
                            for (Reservation r : db.getReservations()) {
                                if (r.getName().equals(username)) {
                                    allReservations += n + ".) " + r.getReservationId() + ", ";
                                    n++;
                                }
                            }
                        }

                        // Check if there are no reservations
                        if (allReservations.equals("")) {
                            allReservations = "No reservations found";
                        } else {
                            allReservations = "Current reservations: " + allReservations;
                        }

                        // Send back resulting reservations (if any)
                        writer.println(allReservations);
                        writer.flush();
                        break;
                    }

                    case "deleteAccount": {
                        String username = reader.readLine();

                        synchronized (db) {
                            User tempUser = new User("", "", null, 0);
                            tempUser.deleteAccount(username);

                            // Iterating through the current reservations until all deleted account's reservations are gone
                            // There must be a for loop broken inside a while loop
                            // b/c for loop iterates through something that gets potentially edited each cycle changed
                            boolean found = true;
                            while (found) {
                                found = false;
                                for (Reservation r : db.getReservations()) {
                                    if (r.getName().equals(username)) {
                                        db.cancelReservation(r);
                                        found = true;
                                        break;
                                    }
                                }
                            }
                        }

                        writer.println("Successfully deleted account");
                        writer.flush();
                        break;
                    }

                    // Error code if a command isn't accounted for in the cases above
                    // More a debugging tool than anything else
                    default:
                        System.out.println("Unknown command: " + command);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}