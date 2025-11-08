import java.util.ArrayList;

/**
 * Reservation Manager Project
 *
 * Interface for Database
 *
 * @author Jeremiah Tomasello
 *
 * @version November 6, 2025
 *
 */

public interface InterfaceDatabase {
    void createReservations();
    void createTables(ArrayList<Reservation> reservations);

    void addReservation(Reservation reservation);
    void cancelReservation(Reservation reservation);

    Table getTableByIndex(int tableNum);
    ArrayList<Table> getTablesBySeats(int seats);
    ArrayList<Table> getUnreservedTables(String day, int time);
}
