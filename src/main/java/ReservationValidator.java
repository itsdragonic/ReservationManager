import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

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

        // automatically checks if it is within the same year

        /*if (reservation.getYear() != today.getYear()) {
            System.out.println("Please enter a day within the same year.");
            return false;
        }

         */

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

    public static String getCurrentDate() {
        LocalDate today = LocalDate.now();
        return today.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
    }

    // More user-friendly allows users to pick a day within 7 days instead of typing it
    public static String[] getNextSevenDays() {
        String[] week = new String[8];
        LocalDate today = LocalDate.now();
        for (int i = 0; i < 8; i++) {
            LocalDate goon = today.plusDays(i);
            week[i] = goon.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        }
        return week;
    }



    // Returns an arrayList of all the available times for the open hours and if the reservation is on the
    // current day it only shows times after the current time
    public static ArrayList<String> getAvailableTime(String date, String openTime, String closeTime) {
        ArrayList<String> availableTimes = new ArrayList<>();
        LocalTime localTime = LocalTime.now();
        LocalDate today = LocalDate.now();
        LocalDate reservationDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("MM/dd/yyyy"));

        LocalTime open = LocalTime.parse(openTime, DateTimeFormatter.ofPattern("HHmm"));
        LocalTime close = LocalTime.parse(closeTime, DateTimeFormatter.ofPattern("HHmm"));

        while (open.isBefore(close)) {
            if (localTime.isAfter(open) && (reservationDate.isEqual(today))) {
                open = open.plusMinutes(15);
            } else {
                availableTimes.add(open.format(DateTimeFormatter.ofPattern("HH:mm")));
                open = open.plusMinutes(15);
            }
        }
        return availableTimes;


    }
}




