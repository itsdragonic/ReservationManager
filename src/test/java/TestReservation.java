import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

/**
 * Unit tests for the Reservation class
 */
public class TestReservation {

    private Reservation res;
    private static final String NAME = "Alice";
    private static final int TABLE = 3;
    private static final String DAY = "03/14/2025";
    private static final int TIME = 1900;
    private static final int GUESTS = 2;

    @Before
    public void setUp() {
        res = new Reservation(NAME, TABLE, DAY, TIME, GUESTS);
    }

    @After
    public void tearDown() {
        res = null;
    }

    @Test
    public void testConstructorAndGetters() {
        assertEquals("Name should match", NAME, res.getName());
        assertEquals("Table number should match", TABLE, res.getTableNum());
        assertEquals("Day should match", DAY, res.getDay());
        assertEquals("Time should match", TIME, res.getTime());
    }

    @Test
    public void testReservationIdFormat() {
        Reservation r = new Reservation("Johnny", 22, "Sunday", 16, 4);
        String expected = "Johnny:22:Sunday:16:4";
        assertEquals("Reservation ID should be formatted correctly", expected, r.getReservationId());
    }

    @Test
    public void testCompareWithSameReservation() {
        Reservation a = new Reservation(NAME, TABLE, DAY, TIME, GUESTS);
        Reservation b = new Reservation(NAME, TABLE, DAY, TIME, GUESTS);
        assertTrue("Reservations with identical fields should compare as equal", a.compareWith(b));
    }

    @Test
    public void testCompareWithDifferentReservation() {
        Reservation a = new Reservation(NAME, TABLE, DAY, TIME, GUESTS);
        Reservation b = new Reservation("Bob", TABLE, DAY, TIME, GUESTS);
        assertFalse("Reservations with different names should not compare equal", a.compareWith(b));
    }

    @Test
    public void testCompareWithNullReservation() {
        try {
            res.compareWith(null);
            fail("compareWith(null) should throw NullPointerException (reservation is null)");
        } catch (NullPointerException e) {
            assertTrue("Caught expected NullPointerException", true);
        }
    }

    @Test
    public void testNullNameAndNegativeValues() {
        // Ensure class tolerates null/negative values and produces a deterministic ID string
        Reservation weird = new Reservation(null, -1, null, -500, 0);
        String expected = "null:-1:null:-500:0";
        assertEquals("Reservation ID should reflect null/negative fields", expected, weird.getReservationId());

        // Two identical "weird" reservations should compare equal
        Reservation weird2 = new Reservation(null, -1, null, -500, 0);
        assertTrue("Two reservations with identical null/negative fields should compare equal", weird.compareWith(weird2));
    }
}