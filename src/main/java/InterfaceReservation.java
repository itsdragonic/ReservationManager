/**
 * Reservation Project
 *
 * Interface for Reservation
 *
 * @author Jeremiah Tomasello
 *
 * @version November 10, 2025
 *
 */

public interface InterfaceReservation {
    String getName();
    int getTableNum();
    String getDay();
    int getTime();
    String getReservationId();
    boolean compareWith(Reservation reservation);
}
