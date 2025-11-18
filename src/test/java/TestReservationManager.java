import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

import java.io.*;
import java.util.ArrayList;

/**
 * TestReservationManager.java
 *
 * Unit tests for the ReservationManager class
 *
 * @author Fredrick Zawislak
 * @version 11/10/2025
 */

public class TestReservationManager {

    private ReservationManager manager;
    private Database db;
    private User user;

    private static final String USERNAME = "Charlie";
    private static final String PASSWORD = "password123";
    private static final int TABLE = 0;
    private static final String DATE = "11/11/2025";
    private static final int TIME = 1830;
    private static final int PARTY = 3;

    private final File reservationsFile = new File("src/main/reservations.txt");
    private final File seatingFile = new File("src/main/seating.txt");
    private final File usernamesFile = new File("usernames.txt");

    @Before
    public void setUp() throws Exception {
        // Clean environment
        if (reservationsFile.exists()) reservationsFile.delete();
        if (seatingFile.exists()) seatingFile.delete();
        if (usernamesFile.exists()) usernamesFile.delete();

        // Create minimal seating file so Database constructor works (creates table 0 and 1)
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(seatingFile))) {
            bw.write("Quantity:2:Seats:4\n");
        }

        // Create minimal usernames file so User methods that read it won't crash if called
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(usernamesFile))) {
            bw.write(USERNAME + ": " + PASSWORD + "\n");
        }

        db = new Database();
        manager = new ReservationManager();

        // create a "real" User instance using your User constructor
        user = new User(USERNAME, PASSWORD, new ArrayList<Reservation>(), 0);
    }

    @After
    public void tearDown() {
        if (reservationsFile.exists()) reservationsFile.delete();
        if (seatingFile.exists()) seatingFile.delete();
        if (usernamesFile.exists()) usernamesFile.delete();

        manager = null;
        db = null;
        user = null;
    }

    // create reservation: should add to user and DB
    @Test
    public void createReservation() {
        manager.createReservation(db, user, TABLE, DATE, TIME, PARTY);

        ArrayList<Reservation> ur = user.getReservations();
        assertEquals("user should have one reservation", 1, ur.size());

        Reservation r = ur.get(0);
        assertEquals(USERNAME, r.getName());
        assertEquals(TABLE, r.getTableNum());
        assertEquals(DATE, r.getDay());
        assertEquals(TIME, r.getTime());

        Reservation dbR = db.getReservation(r.getReservationId());
        assertNotNull("db should contain reservation", dbR);
        assertEquals(r.getReservationId(), dbR.getReservationId());

        Table t = db.getTableByIndex(TABLE);
        assertNotNull("table should exist", t);

        boolean found = false;
        for (Reservation tr : t.getReservations()) {
            if (tr.getReservationId().equals(r.getReservationId())) { found = true; break; }
        }
        assertTrue("table should contain reservation", found);
    }

    // cancel reservation: should remove from user and DB
    @Test
    public void cancelReservation() {
        manager.createReservation(db, user, TABLE, DATE, TIME, PARTY);
        Reservation r = user.getReservations().get(0);

        manager.cancelReservation(db, user, r);

        assertEquals("user should have zero reservations", 0, user.getReservations().size());
        assertNull("db should not have reservation", db.getReservation(r.getReservationId()));

        Table t = db.getTableByIndex(TABLE);
        assertNotNull(t);
        for (Reservation tr : t.getReservations()) {
            assertNotEquals("table should not contain cancelled reservation", r.getReservationId(), tr.getReservationId());
        }
    }

    // null args should cause NPE when used
    @Test
    public void nullArgsHandling() {
        try {
            manager.createReservation(null, user, TABLE, DATE, TIME, PARTY);
            fail("expected NPE for null db");
        } catch (NullPointerException e) {
            // expected
        }

        try {
            manager.createReservation(db, null, TABLE, DATE, TIME, PARTY);
            fail("expected NPE for null user");
        } catch (NullPointerException e) {
            // expected
        }
    }

    // cancel an orphan reservation (never added) should not crash
    @Test
    public void orphanReservations() {
        Reservation orphan = new Reservation("Nobody", 99, "01/01/2020", 1200, 1);

        assertEquals(0, user.getReservations().size());
        assertNull(db.getReservation(orphan.getReservationId()));

        try {
            manager.cancelReservation(db, user, orphan);
        } catch (Exception e) {
            fail("cancelReservation threw: " + e.getMessage());
        }

        assertEquals(0, user.getReservations().size());
        assertNull(db.getReservation(orphan.getReservationId()));
    }
}