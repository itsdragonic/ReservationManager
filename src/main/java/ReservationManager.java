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
    /*
    * Instructions for new user methods:
    * The User class needs to have an ArrayList variable to hold all the Reservations it currently has booked
    * There needs to be corresponding getter/setter methods for editing this ArrayList
    * Additionally, users also must have a reservationNumber variable so they can have unique reservations to be differentiated between
    * Thanks!!!
    * */


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
