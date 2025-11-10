public interface InterfaceReservation {
    String getName();
    int getTableNum();
    String getDay();
    int getTime();
    String getReservationId();
    boolean compareWith(Reservation reservation);
}
