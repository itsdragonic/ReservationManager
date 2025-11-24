import java.util.ArrayList;

/**
 * Table Project
 *
 * Interface for Table
 *
 * @author Jeremiah Tomasello
 *
 * @version November 10, 2025
 *
 */

public interface InterfaceTable {
    int getSeats();
    int getTableNum();
    ArrayList<Reservation> getReservations();
    int getPrice();
    void addReservation(Reservation reservation);
    void removeReservation(Reservation reservation);
}
