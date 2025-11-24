import java.util.ArrayList;

/**
 * Reservation Manager Project
 *
 * Table class used to store table data for use in the database
 * Each table should be created with a unique table number/index to prevent errors with creating a reservation
 * Each table contains information about who is reserving the table (Reservation)
 *
 * @author Jeremiah Tomasello
 *
 * @version November 6, 2025
 *
 */

public class Table implements InterfaceTable {
    private int tableNum;
    private int seats;
    private ArrayList<Reservation> reservations;
    private int price;

    //Unique table
    public Table(int tableNum, int seats, ArrayList<Reservation> reservations, int price) {
        this.tableNum = tableNum;
        this.seats = seats;
        this.reservations = reservations;
        this.price = price;
    }

    public int getSeats() {
        return seats;
    }

    public int getTableNum() {
        return tableNum;
    }

    public ArrayList<Reservation> getReservations() {
        return reservations;
    }

    public int getPrice() {
        return price;
    }

    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
    }

    public void removeReservation(Reservation reservation) {
        reservations.remove(reservation);
    }
}
