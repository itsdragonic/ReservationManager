import java.io.*;
import java.util.ArrayList;

/**
 * Reservation Manager Project
 *
 * This holds all the data for the project
 * Reservation and table data are stored in files (reservations.txt and seating.txt)
 * Has methods for creating ArrayLists for reservations and tables for use within code
 * Any changes made to reservations should be done through ReservationManager.java
 *
 * @author Jeremiah Tomasello
 *
 * @version November 6, 2025
 *
 */

public class Database implements InterfaceDatabase {
    private ArrayList<Table> tables;
    private ArrayList<Reservation> reservations;
    private final int maxReservationLength = 2; // Hours

    private final String reservationsFilename = "reservations.txt";
    private final String seatingFilename = "seating.txt";

    public Database() {
        this.tables = new ArrayList<>();
        this.reservations = new ArrayList<>();
    }

    // Call before CreateTables()
    public void createReservations() {
        // Fill reservations array
        try (BufferedReader br = new BufferedReader(new FileReader(reservationsFilename))) {
            // Iterate through reservations.txt for every current reservation
            String line = br.readLine();
            while (line != null) {
                String user = line.split(":")[0];
                int tableNum = Integer.parseInt(line.split(":")[1]);
                String day = line.split(":")[2];
                int time = Integer.parseInt(line.split(":")[3]);
                int partySize = Integer.parseInt(line.split(":")[4]);

                reservations.add(new Reservation(user, tableNum, day, time, partySize));

                line = br.readLine();
            }

        } catch (IOException e) {
            // Could be replaced with some kind of "getting reservations error"
            e.printStackTrace();
        }
    }

    // Make sure the reservation database has been created first
    public void createTables(ArrayList<Reservation> reservations) {
        // Fill tables array
        try (BufferedReader br = new BufferedReader(new FileReader(seatingFilename))) {
            int currentTablesIndex = 0;

            // Iterate through seating.txt to get seating arrangement
            String line = br.readLine();
            while (line != null) {
                int numTables = Integer.parseInt(line.split(":")[1]);
                int numSeats = Integer.parseInt(line.split(":")[3]);

                // Add the corresponding table size to tables by quantity
                for (int i = 0; i < numTables; i++) {
                    // Check if any of these tables hold reservations, then add them to an array to be added to the table
                    ArrayList<Reservation> tableReservations = new ArrayList<>();
                    for (Reservation reservation : reservations) {
                        if (reservation.getTableNum() == currentTablesIndex) {
                            tableReservations.add(reservation);
                        }
                    }

                    tables.add(new Table(currentTablesIndex, numSeats, tableReservations));
                    currentTablesIndex++;
                }

                line = br.readLine();
            }

        } catch (IOException e) {
            // Could be replaced with some kind of "getting tables error"
            e.printStackTrace();
        }
    }

    public boolean addReservation(Reservation reservation) {
        if (this.reservations == null) this.reservations = new ArrayList<>();
        this.reservations.add(reservation);

        // Create resources. Append mode true so we can add to preexisting reservations
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(reservationsFilename, true))) {
            // Check all tables for corresponding table and add reservation
            for (Table table : tables) {
                if (table.getTableNum() == reservation.getTableNum()) {
                    table.addReservation(reservation);
                }
            }

            bw.write(reservation.getReservationId() + "\n");
            return true;
        } catch (IOException e) {
            e.printStackTrace(); // Could be replaced with some kind of "adding reservation error"
            return false;
        }
    }

    public Reservation getReservation(String reservationId) {
        if (reservationId == null || reservations == null) return null;
        for (Reservation r : reservations) {
            if (r != null && reservationId.equals(r.getReservationId())) {
                return r;
            }
        }
        return null;
    }

    public boolean cancelReservation(Reservation reservation) {
        if (this.reservations != null) {
            reservations.removeIf(r -> r.compareWith(reservation));
        }

        // Create resources. Append mode false so we can neglect to re add the cancelled reservation
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(reservationsFilename, false))) {
            // Check all tables for corresponding table and remove it
            for (Table table : tables) {
                if (table.getTableNum() == reservation.getTableNum()) {
                    table.removeReservation(reservation);
                }
            }

            for (Reservation currentReservation : reservations) {
                // As long as it's not the cancelled reservation, readd it to reservations.txt
                if (!currentReservation.compareWith(reservation)) {
                    bw.write(currentReservation.getReservationId() + "\n");
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    // Iterate through tables ArrayList, if it finds a matching id to preexisting table, return it
    // Returns null if unsuccessful
    public Table getTableByIndex(int tableNum) {
        for (Table table : tables) {
            if (table.getTableNum() == (tableNum)) {
                return table;
            }
        }

        return null;
    }

    // Iterate through tables ArrayList, if it finds a table with the corresponding seat amount, add to return list
    public ArrayList<Table> getTablesBySeats(int seats) {
        ArrayList<Table> foundTables = new ArrayList<>();
        for (Table table : tables) {
            if (table.getSeats() == seats) {
                foundTables.add(table);
            }
        }
        return foundTables;
    }

    //Iterate through tables and find whether they have a reservation that conflicts with the current date/time
    public ArrayList<Table> getUnreservedTables(String day, int time) {
        ArrayList<Table> availableTables = new ArrayList<>();
        for (Table table : tables) {
            if (table.getReservations().isEmpty()) {
                availableTables.add(table);
            } else {
                boolean conflictingTime = false;
                for (Reservation reservation : table.getReservations()) {
                    if (reservation.getDay().equals(day) && Math.abs(reservation.getTime() - time) < maxReservationLength)  {
                        conflictingTime = true;
                    }
                }

                if (!conflictingTime) {
                    availableTables.add(table);
                }
            }
        }

        return availableTables;
    }
}
