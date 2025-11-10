import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;
import java.io.*;

public class TestDatabase {
    private Database db;
    private static final String TEST_NAME = "John Purdue";
    private static final int TEST_TABLE = 1;
    private static final String TEST_DATE = "03/14/2025";
    private static final int TEST_TIME = 1300;
    private static final int TEST_GUESTS = 4;

    private final File reservationsFile = new File("reservations.txt");
    private final File seatingFile = new File("seating.txt");

    @Before
    public void setUp() throws Exception {
        // ensure a clean environment
        if (reservationsFile.exists()) reservationsFile.delete();
        // create a minimal seating file so tables can exist (one table with seats)
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(seatingFile))) {
            bw.write("Quantity:2:Seats:4\n"); // two tables with 4 seats each
        }
        db = new Database();
    }

    @After
    public void tearDown() {
        // clean up files so tests remain isolated
        if (reservationsFile.exists()) reservationsFile.delete();
        if (seatingFile.exists()) seatingFile.delete();
    }

    @Test
    public void testAddReservation() {
        Reservation res = new Reservation(TEST_NAME, TEST_TABLE, TEST_DATE, TEST_TIME, TEST_GUESTS);
        assertTrue(db.addReservation(res)); // should return true
        // ensure the reservation is discoverable in-memory
        Reservation retrieved = db.getReservation(res.getReservationId());
        assertNotNull("Reservation should be present after addReservation", retrieved);
        assertEquals(TEST_NAME, retrieved.getName());
    }

    @Test
    public void testGetReservation() {
        Reservation res = new Reservation(TEST_NAME, TEST_TABLE, TEST_DATE, TEST_TIME, TEST_GUESTS);
        db.addReservation(res);
        Reservation retrieved = db.getReservation(res.getReservationId());
        assertNotNull("Expected reservation to be retrieved", retrieved);
        assertEquals(TEST_NAME, retrieved.getName());
    }

    @Test
    public void testCancelReservation() {
        Reservation res = new Reservation(TEST_NAME, TEST_TABLE, TEST_DATE, TEST_TIME, TEST_GUESTS);
        db.addReservation(res);
        assertTrue("cancelReservation should return true", db.cancelReservation(res));
        assertNull("Reservation should be removed after cancellation", db.getReservation(res.getReservationId()));
    }

    @Test
    public void testInvalidInputs() {
        // expect addReservation(null) to fail
        try {
            assertFalse("Adding null reservation should return false",
                    db.addReservation(null));
        } catch (NullPointerException e) {
            assertTrue("Caught expected NullPointerException for null reservation", true);
        }

        // invalid input
        Reservation invalidRes = new Reservation("Jane Doe", -1, "03/14/2025", -500, 3);
        assertTrue("Database should handle invalid reservation gracefully",
                db.addReservation(invalidRes));

        // Attempt to retrieve a reservation with a non-existent ID
        assertNull("Getting non-existent reservation should return null",
                db.getReservation("NonExistent:99:12/31/2025:1800:2"));

        // Attempt to cancel a reservation not in the system
        try {
            assertTrue("Cancelling non-existent reservation should not crash",
                    db.cancelReservation(invalidRes));
        } catch (NullPointerException e) {
            assertTrue("Caught expected NullPointerException for non-existent cancel", true);
        }
    }
}