import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Reservation Validator
 *
 * Methods to validate the reservation date and time
 * Checks the reservation to see if it is within 7 days and not in the past
 *
 *
 * @author Maximus Puckett
 *
 * @version November 21, 2025
 *
 */


public class ReservationValidator implements InterfaceReservationValidator {

    // Checks the formatting of the date and to make sure it's within 7 days
    public static boolean validReservation(String date) {
        LocalDate today = LocalDate.now();
        LocalDate sevenDays = today.plusDays(7);

        LocalDate reservation;

        try {
            reservation = LocalDate.parse(date, DateTimeFormatter.ofPattern("MM/dd/yyyy"));

        } catch (DateTimeParseException e) {
            System.out.println("Invalid Date Format");
            return false;
        }

        if (reservation.getYear() != today.getYear()) {
            System.out.println("Please enter a day within the same year.");
            return false;
        }

        if (reservation.isAfter(sevenDays) || reservation.isBefore(today)) {
            System.out.println("Please enter a day within 7 days");
            return false;
        } else {
            return true;

        }

    }
    // Checks the time and makes sure it is formatted correctly and also checks to make sure the reservation isn't in the past
    public static boolean validTime(String time, String date) {
        LocalTime localTime = LocalTime.now();
        LocalDate localDate = LocalDate.now();
        LocalTime reservationTime;
        LocalDate reservationDate;

        reservationDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("MM/dd/yyyy"));

        try {
            reservationTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HHmm"));
        } catch (DateTimeParseException e) {
            System.out.println("Invalid Time Format");
            return false;
        }
        if (reservationDate.equals(localDate) && reservationTime.isBefore(localTime)) {
            System.out.println("Reservation can't be in the past");
            return false;
        } else {
            return true;
        }
    }
}

