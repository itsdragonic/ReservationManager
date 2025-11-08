/**
 * Reservation Manager Project
 *
 * Interface for ReservationManager.java
 *
 * @author Jeremiah Tomasello
 *
 * @version November 6, 2025
 *
 */

public interface InterfaceReservationManager {
    public void createReservation(Database database, User user, int tableNum, String date, int time, int partySize);
    public void cancelReservation(Database database, User user, Reservation reservation);
}