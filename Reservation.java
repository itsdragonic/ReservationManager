/**
 * Reservation Manager Project
 *
 * Reservation class for use in the database
 * This class holds all the data necessary for a reservation
 *
 * @author Jeremiah Tomasello
 *
 * @version November 6, 2025
 *
 */

public class Reservation {
    private String userBooking;
    private int tableNum;
    private String day;
    private int time; // in hour (24-hour clock) expire time can be this value + some variable
    private int partySize;

    public Reservation(String username, int tableNum, String day, int time, int partySize) {
        userBooking = username;
        this.tableNum = tableNum;
        this.day = day;
        this.time = time;
        this.partySize = partySize;
    }

    public int getTableNum() {
        return tableNum;
    }

    public String getDay() {
        return day;
    }

    public int getTime() {
        return time;
    }

    // This should be unique to every reservation
    public String getReservationId() {
        // Example: Johnny:22:Sunday:16:4
        return String.format("%s:%d:%s:%d:%d", userBooking, tableNum, day, time, partySize);
    }

    public boolean compareWith(Reservation reservation) {
        return getReservationId().equals(reservation.getReservationId());
    }
}
