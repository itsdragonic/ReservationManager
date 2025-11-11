import java.util.ArrayList;

/**
 * InterfaceUser.java
 *
 * Interface for User
 *
 * @author Maximus Puckett
 *
 * @version November 10, 2025
 *
 */

public interface InterfaceUser {
    boolean createAccount(String username, String password);
    boolean login(String username, String password);
    boolean deleteAccount(String username);

    ArrayList<Reservation> getReservations();
    void setReservations(ArrayList<Reservation> reservations);
    int getReservationNumber();

    void setReservationNumber(int reservationNumber);
    String getUsername();
    String getPassword();
    void addReservation(Reservation reservation);
    void removeReservation(Reservation reservation);
}
