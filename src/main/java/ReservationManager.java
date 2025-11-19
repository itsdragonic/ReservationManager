import java.util.ArrayList;

/**
 * Reservation Manager Project
 *
 * This class allows a user to edit data stored in the database
 *
 * @author Jeremiah Tomasello
 *
 * @version November 6, 2025
 *
 */

public class ReservationManager implements InterfaceReservationManager {
    public void createReservation(Database database, User user, int tableNum, String date, int time, int partySize) {
        Reservation newReservation = new Reservation(user.getUsername(), tableNum, date, time, partySize);
        user.addReservation(newReservation);
        database.addReservation(newReservation);
    }

    public void cancelReservation(Database database, User user, Reservation reservation) {
        user.removeReservation(reservation);
        database.cancelReservation(reservation);
    }
}
