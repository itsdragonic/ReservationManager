import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;
import java.util.ArrayList;

public class TestTable {
    private Table table;
    private ArrayList<Reservation> list;
    private Reservation r1;
    private Reservation r2;

    @Before
    public void setUp() {
        list = new ArrayList<>();
        table = new Table(5, 4, list); // tableNum = 5, seats = 4
        r1 = new Reservation("Alice", 5, "11/10/2025", 1900, 2);
        r2 = new Reservation("Bob", 5, "11/11/2025", 2000, 3);
    }

    @After
    public void tearDown() {
        list = null;
        table = null;
        r1 = null;
        r2 = null;
    }

    @Test
    public void testConstructorAndGetters() {
        assertEquals("Table number should match", 5, table.getTableNum());
        assertEquals("Seat count should match", 4, table.getSeats());
        assertNotNull("Reservation list should not be null", table.getReservations());
        assertTrue("Reservation list should start empty", table.getReservations().isEmpty());
    }

    @Test
    public void testAddReservation() {
        table.addReservation(r1);
        assertEquals(1, table.getReservations().size());
        assertEquals(r1, table.getReservations().get(0));

        table.addReservation(r2);
        assertEquals(2, table.getReservations().size());
        assertTrue(table.getReservations().contains(r1));
        assertTrue(table.getReservations().contains(r2));
    }

    @Test
    public void testRemoveReservation() {
        table.addReservation(r1);
        table.addReservation(r2);
        assertEquals(2, table.getReservations().size());

        table.removeReservation(r1);
        assertEquals(1, table.getReservations().size());
        assertFalse(table.getReservations().contains(r1));
        assertTrue(table.getReservations().contains(r2));
    }

    @Test
    public void testReservationListReference() {
        // Ensure the same list object is used
        assertSame("Constructor list should be the same reference", list, table.getReservations());
    }

    @Test
    public void testMultipleAddRemove() {
        for (int i = 0; i < 5; i++) {
            table.addReservation(new Reservation("User" + i, 5, "11/10/2025", 1800 + i, 2));
        }
        assertEquals(5, table.getReservations().size());

        // remove last one
        Reservation last = table.getReservations().get(4);
        table.removeReservation(last);
        assertEquals(4, table.getReservations().size());
    }
}