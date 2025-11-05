import java.util.List;

public interface InterfaceReservation {
    String setReservation(String username, String date, String time, int partySize); // return reservationId
    List<String> getReservations(String username);
    boolean cancelReservation(String reservationId, String username);
}