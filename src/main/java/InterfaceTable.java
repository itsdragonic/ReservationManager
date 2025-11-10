import java.util.ArrayList;

public interface InterfaceTable {
    int getSeats();
    int getTableNum();
    ArrayList<Reservation> getReservations();
    void addReservation(Reservation reservation);
    void removeReservation(Reservation reservation);
}
