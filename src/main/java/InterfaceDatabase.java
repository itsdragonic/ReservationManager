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
    int getOpenTime();
    int getCloseTime();
    void setOpenTime(int time);
    void setCloseTime(int time);
    void createTime();

    void createReservations();
    void createTables(ArrayList<Reservation> reservations);

    boolean addReservation(Reservation reservation);
    Reservation getReservation(String reservationId);
    boolean cancelReservation(Reservation reservation);

    Table getTableByIndex(int tableNum);
    ArrayList<Table> getTablesBySeats(int seats);
    ArrayList<Table> getUnreservedTables(String day, int time);
}
