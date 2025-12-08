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
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

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

                        if (ok && username.equals("admin")) {
                            writer.println("admin");
                        } else {
                            writer.println(ok ? "success" : "failure");
                        }
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
                                            // changed formatting
                                            writer.write(String.format("Table: %d (%d Seats $%d), ",
                                                    t.getTableNum(), t.getSeats(), t.getPrice()));
                                            availableTables.add(t.getTableNum());
                                        }
                                    }
                                }
                            }
                        }

                        writer.println();
                        writer.flush();

                        String tableString = reader.readLine();
                        System.out.println(tableString);
                        if (tableString.equals("cancel")) {
                            writer.println("Reservation cancelled!");
                            writer.flush();
                            break;
                        }

                        int table;

                        try {
                            table = Integer.parseInt(tableString);
                        } catch (NumberFormatException e) {
                            writer.println("Invalid table selection.");
                            writer.flush();
                            break;
                        }

                        // Send back result of adding the reservation to Client
                        Reservation res = new Reservation(name, table, date, time, guests);

                        synchronized (db) {
                            if (db.addReservation(res) && availableTables.contains(table)) {
                                //writer.println("Reservation added successfully!");
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

                    case "joinEvent": {
                        String username = reader.readLine();
                        String eventName = reader.readLine();

                        Event target = null;

                        synchronized (db) {
                            for (Event e : db.getEvents()) {
                                if (e.name.equals(eventName)) {
                                    target = e;
                                    break;
                                }
                            }
                        }

                        if (target == null) {
                            writer.println("Event not found.");
                            writer.flush();
                            break;
                        }

                        // Find which of this event’s tables are available
                        ArrayList<Integer> available = new ArrayList<>();

                        synchronized (db) {
                            for (Integer tableNum : target.tables) {
                                Table t = db.getTableByIndex(tableNum);
                                boolean ok = true;
                                for (Reservation r : t.getReservations()) {
                                    if (r.getDay().equals(target.date)
                                            && Math.abs(r.getTime() - target.time) < 2) {
                                        ok = false;
                                        break;
                                    }
                                }
                                if (ok) available.add(tableNum);
                            }
                        }

                        if (available.isEmpty()) {
                            writer.println("Sorry, all tables are reserved!");
                            writer.flush();
                            break;
                        }

                        // Send available tables back
                        writer.print("Available tables: ");
                        for (int t : available) writer.print(t + " ");
                        writer.println();
                        writer.flush();

                        int chosen = Integer.parseInt(reader.readLine());
                        if (!available.contains(chosen)) {
                            writer.println("Invalid table selection.");
                            writer.flush();
                            break;
                        }

                        // Add reservation
                        Reservation newRes = new Reservation(username, chosen,
                                target.date, target.time, 1);

                        synchronized (db) {
                            boolean success = db.addReservation(newRes);
                            writer.println(success ? "Joined event!" :
                                    "Failed to join event.");
                        }

                        writer.flush();
                        break;
                    }

                    case "getHours": {
                        int open, close;
                        synchronized (db) {
                            open = db.getOpenTime();
                            close = db.getCloseTime();
                        }

                        writer.println(open + ":" + close);
                        writer.flush();
                        break;
                    }

                    case "setHours": {
                        String newOpenTime = reader.readLine();
                        String newCloseTime = reader.readLine();

                        int openInt = Integer.parseInt(newOpenTime);
                        int closeInt = Integer.parseInt(newCloseTime);

                        synchronized (db) {
                            db.setOpenTime(openInt);
                            db.setCloseTime(closeInt);

                            // Update restaurant.txt file
                            try {
                                // Read all lines from restaurant.txt
                                ArrayList<String> lines = new ArrayList<>();
                                BufferedReader br = new BufferedReader(new FileReader("restaurant.txt"));
                                String line = br.readLine();

                                while (line != null) {
                                    lines.add(line);
                                    line = br.readLine();
                                }
                                br.close();

                                // Modify first line with new hours
                                if (!lines.isEmpty()) {
                                    lines.set(0, "open:" + openInt + ":close:" + closeInt);
                                } else {
                                    lines.add("open:" + openInt + ":close:" + closeInt);
                                }

                                // Write back to file
                                BufferedWriter bw = new BufferedWriter(new FileWriter("restaurant.txt", false));
                                for (int i = 0; i < lines.size(); i++) {
                                    bw.write(lines.get(i));
                                    if (i < lines.size() - 1) {
                                        bw.newLine();
                                    }
                                }
                                bw.close();

                                writer.println("Hours updated successfully! Open: " + newOpenTime + " Close: " + newCloseTime);

                            } catch (IOException e) {
                                e.printStackTrace();
                                writer.println("Failed to update hours in file.");
                            }
                        }

                        writer.flush();
                        synchronized (db) {
                            db.reload();
                        }

                        break;
                    }

                    case "getSeating": {
                        try {
                            BufferedReader br = new BufferedReader(new FileReader("seating.txt"));
                            StringBuilder seatingData = new StringBuilder();
                            String line = br.readLine();
                            boolean first = true;

                            while (line != null) {
                                if (!line.trim().isEmpty() && line.contains(":")) {
                                    // Parse: Quantity:15:Seats:1:Price:25
                                    String[] parts = line.split(":");
                                    int quantity = Integer.parseInt(parts[1]);
                                    int seats = Integer.parseInt(parts[3]);
                                    int price = Integer.parseInt(parts[5]);

                                    if (!first) seatingData.append("|");
                                    seatingData.append(seats).append(":").append(quantity).append(":").append(price);
                                    first = false;
                                }
                                line = br.readLine();
                            }
                            br.close();

                            if (seatingData.length() == 0) {
                                writer.println("No seating data");
                            } else {
                                writer.println(seatingData.toString());
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            writer.println("Error reading seating data");
                        }
                        writer.flush();
                        break;
                    }

                    case "addSeating": {
                        String seatsStr = reader.readLine();
                        String quantityStr = reader.readLine();
                        String priceStr = reader.readLine();

                        int seats = Integer.parseInt(seatsStr);
                        int quantity = Integer.parseInt(quantityStr);
                        int price = Integer.parseInt(priceStr);

                        try {
                            // Check if seat size already exists
                            BufferedReader br = new BufferedReader(new FileReader("seating.txt"));
                            String line = br.readLine();
                            boolean exists = false;

                            while (line != null) {
                                if (line.contains("Seats:" + seats)) {
                                    exists = true;
                                    break;
                                }
                                line = br.readLine();
                            }
                            br.close();

                            if (exists) {
                                writer.println("Seat size " + seats + " already exists! Use edit to modify.");
                            } else {
                                // Append new seating
                                BufferedWriter bw = new BufferedWriter(new FileWriter("seating.txt", true));
                                bw.newLine();
                                bw.write("Quantity:" + quantity + ":Seats:" + seats + ":Price:" + price);
                                bw.close();

                                writer.println("Successfully added " + quantity + " tables with " + seats + " seats at $" + price);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            writer.println("Failed to add seating.");
                        }

                        writer.flush();
                        synchronized (db) {
                            db.reload();
                        }

                        break;
                    }

                    case "updateSeating": {
                        String seatsStr = reader.readLine();
                        String quantityStr = reader.readLine();

                        int seats = Integer.parseInt(seatsStr);
                        int newQuantity = Integer.parseInt(quantityStr);

                        try {
                            // Read all lines
                            ArrayList<String> lines = new ArrayList<>();
                            BufferedReader br = new BufferedReader(new FileReader("seating.txt"));
                            String line = br.readLine();
                            boolean found = false;

                            while (line != null) {
                                if (line.contains("Seats:" + seats + ":")) {
                                    // Update this line
                                    String[] parts = line.split(":");
                                    int price = Integer.parseInt(parts[5]);
                                    lines.add("Quantity:" + newQuantity + ":Seats:" + seats + ":Price:" + price);
                                    found = true;
                                } else {
                                    lines.add(line);
                                }
                                line = br.readLine();
                            }
                            br.close();

                            if (!found) {
                                writer.println("Seat size not found.");
                            } else {
                                // Write back to file
                                BufferedWriter bw = new BufferedWriter(new FileWriter("seating.txt", false));
                                for (int i = 0; i < lines.size(); i++) {
                                    bw.write(lines.get(i));
                                    if (i < lines.size() - 1) bw.newLine();
                                }
                                bw.close();

                                writer.println("Successfully updated quantity to " + newQuantity + " for " + seats + "-seat tables");
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            writer.println("Failed to update seating.");
                        }

                        writer.flush();
                        synchronized (db) {
                            db.reload();
                        }

                        break;
                    }

                    case "updatePrice": {
                        String seatsStr = reader.readLine();
                        String priceStr = reader.readLine();

                        int seats = Integer.parseInt(seatsStr);
                        int newPrice = Integer.parseInt(priceStr);

                        try {
                            // Read all lines
                            ArrayList<String> lines = new ArrayList<>();
                            BufferedReader br = new BufferedReader(new FileReader("seating.txt"));
                            String line = br.readLine();
                            boolean found = false;

                            while (line != null) {
                                if (line.contains("Seats:" + seats + ":")) {
                                    // Update this line
                                    String[] parts = line.split(":");
                                    int quantity = Integer.parseInt(parts[1]);
                                    lines.add("Quantity:" + quantity + ":Seats:" + seats + ":Price:" + newPrice);
                                    found = true;
                                } else {
                                    lines.add(line);
                                }
                                line = br.readLine();
                            }
                            br.close();

                            if (!found) {
                                writer.println("Seat size not found.");
                            } else {
                                // Write back to file
                                BufferedWriter bw = new BufferedWriter(new FileWriter("seating.txt", false));
                                for (int i = 0; i < lines.size(); i++) {
                                    bw.write(lines.get(i));
                                    if (i < lines.size() - 1) bw.newLine();
                                }
                                bw.close();

                                writer.println("Successfully updated price to $" + newPrice + " for " + seats + "-seat tables");
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            writer.println("Failed to update price.");
                        }

                        writer.flush();
                        synchronized (db) {
                            db.reload();
                        }

                        break;
                    }

                    case "getAvailableTables": {
                        String date = reader.readLine();
                        int time = Integer.parseInt(reader.readLine());

                        try {
                            BufferedReader br = new BufferedReader(new FileReader("seating.txt"));
                            StringBuilder tablesData = new StringBuilder();
                            String line = br.readLine();
                            boolean first = true;
                            int tableIndex = 0;

                            synchronized (db) {
                                while (line != null) {
                                    if (!line.trim().isEmpty() && line.contains(":")) {
                                        // Parse: Quantity:15:Seats:1:Price:25
                                        String[] parts = line.split(":");
                                        int quantity = Integer.parseInt(parts[1]);
                                        int seats = Integer.parseInt(parts[3]);
                                        int price = Integer.parseInt(parts[5]);

                                        // For each table of this type
                                        for (int i = 0; i < quantity; i++) {
                                            Table t = db.getTableByIndex(tableIndex);

                                            // Check if table is available at this date/time
                                            boolean available = true;
                                            if (t != null) {
                                                for (Reservation r : t.getReservations()) {
                                                    if (r.getDay().equals(date) && Math.abs(r.getTime() - time) < 2) {
                                                        available = false;
                                                        break;
                                                    }
                                                }
                                            }

                                            if (available) {
                                                if (!first) tablesData.append("|");
                                                tablesData.append(tableIndex).append(":").append(seats).append(":").append(price);
                                                first = false;
                                            }

                                            tableIndex++;
                                        }
                                    }
                                    line = br.readLine();
                                }
                            }
                            br.close();

                            if (tablesData.length() == 0) {
                                writer.println("No tables available");
                            } else {
                                writer.println(tablesData.toString());
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            writer.println("Error reading table data");
                        }

                        writer.flush();
                        break;
                    }

                    case "createEvent": {
                        String eventName = reader.readLine();
                        String date = reader.readLine();
                        int time = Integer.parseInt(reader.readLine());
                        int numTables = Integer.parseInt(reader.readLine());

                        ArrayList<Integer> tables = new ArrayList<>();
                        for (int i = 0; i < numTables; i++) {
                            tables.add(Integer.parseInt(reader.readLine()));
                        }

                        try {
                            // Append event to restaurant.txt
                            BufferedWriter bw = new BufferedWriter(new FileWriter("restaurant.txt", true));
                            bw.newLine();
                            bw.write("event:" + eventName + ":" + date + ":" + time);
                            for (int table : tables) {
                                bw.write(":" + table);
                            }
                            bw.close();

                            // Add to database events list
                            synchronized (db) {
                                db.getEvents().add(new Event(eventName, date, time, tables));
                            }

                            writer.println("Event '" + eventName + "' created successfully with " + numTables + " table(s)!");

                        } catch (IOException e) {
                            e.printStackTrace();
                            writer.println("Failed to create event.");
                        }

                        writer.flush();
                        synchronized (db) {
                            db.reload();
                        }

                        break;
                    }

                    case "getEvents": {
                        synchronized (db) {
                            ArrayList<Event> events = db.getEvents();

                            if (events.isEmpty()) {
                                writer.println("No events found");
                            } else {
                                StringBuilder eventsData = new StringBuilder();
                                boolean first = true;

                                for (Event event : events) {
                                    if (!first) eventsData.append("|");
                                    eventsData.append(event.name).append(":").append(event.date).append(":").append(event.time);
                                    first = false;
                                }

                                writer.println(eventsData.toString());
                            }
                        }

                        writer.flush();
                        break;
                    }

                    case "deleteEvent": {
                        String eventName = reader.readLine();

                        try {
                            // Read all lines from restaurant.txt
                            ArrayList<String> lines = new ArrayList<>();
                            BufferedReader br = new BufferedReader(new FileReader("restaurant.txt"));
                            String line = br.readLine();
                            boolean found = false;

                            while (line != null) {
                                // Skip the line that contains this event
                                if (line.contains("event:" + eventName + ":")) {
                                    found = true;
                                } else {
                                    lines.add(line);
                                }
                                line = br.readLine();
                            }
                            br.close();

                            if (!found) {
                                writer.println("Event not found.");
                            } else {
                                // Write back to file
                                BufferedWriter bw = new BufferedWriter(new FileWriter("restaurant.txt", false));
                                for (int i = 0; i < lines.size(); i++) {
                                    bw.write(lines.get(i));
                                    if (i < lines.size() - 1) bw.newLine();
                                }
                                bw.close();

                                // Remove from database events list
                                synchronized (db) {
                                    db.getEvents().removeIf(e -> e.name.equals(eventName));
                                }

                                writer.println("Event '" + eventName + "' deleted successfully!");
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            writer.println("Failed to delete event.");
                        }

                        writer.flush();
                        synchronized (db) {
                            db.reload();
                        }

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