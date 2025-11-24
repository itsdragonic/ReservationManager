import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

import java.io.*;
import java.util.ArrayList;

/**
 * TestEvent.java
 *
 * Unit tests for the Event class
 *
 * @author Fredrick Zawislak
 * @version November 24, 2025
 */
public class TestEvent {

    private Event event;
    private ArrayList<Integer> tables;
    private File testFile;
    private long originalLength;

    private static final String NAME = "Birthday Party";
    private static final String DATE = "12/25/2025";
    private static final int TIME = 1800;

    @Before
    public void setUp() throws Exception {
        tables = new ArrayList<>();
        tables.add(1);
        tables.add(2);
        tables.add(3);

        event = new Event(NAME, DATE, TIME, tables);

        testFile = new File("test_event_data.txt");
        if (!testFile.exists()) {
            testFile.createNewFile();
        }
        originalLength = testFile.length();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(testFile, true))) {
            bw.newLine();
            bw.write("Test event data");
        }
    }

    @After
    public void tearDown() throws Exception {
        if (testFile.exists()) {
            try (RandomAccessFile raf = new RandomAccessFile(testFile, "rw")) {
                raf.setLength(originalLength);
            }
        }

        event = null;
        tables = null;
    }

    @Test
    public void testEventConstructor() {
        assertNotNull("Event should be created", event);
        assertEquals(NAME, event.name);
        assertEquals(DATE, event.date);
        assertEquals(TIME, event.time);
        assertEquals(tables, event.tables);
    }

    @Test
    public void testEventFields() {
        assertEquals("Name should match", NAME, event.name);
        assertEquals("Date should match", DATE, event.date);
        assertEquals("Time should match", TIME, event.time);
        assertNotNull("Tables should not be null", event.tables);
        assertEquals("Tables size should be 3", 3, event.tables.size());
    }

    @Test
    public void testEventImplementsInterface() {
        assertTrue("Event should implement InterfaceEvent",
                event instanceof InterfaceEvent);
    }

    @Test
    public void testEmptyTables() {
        ArrayList<Integer> emptyTables = new ArrayList<>();
        Event emptyEvent = new Event("Empty Event", "01/01/2026", 1200, emptyTables);

        assertNotNull(emptyEvent);
        assertEquals(0, emptyEvent.tables.size());
    }

    @Test
    public void testNullName() {
        Event nullNameEvent = new Event(null, DATE, TIME, tables);
        assertNull(nullNameEvent.name);
    }

    @Test
    public void testNullDate() {
        Event nullDateEvent = new Event(NAME, null, TIME, tables);
        assertNull(nullDateEvent.date);
    }

    @Test
    public void testNullTables() {
        Event nullTablesEvent = new Event(NAME, DATE, TIME, null);
        assertNull(nullTablesEvent.tables);
    }

    @Test
    public void testTablesList() {
        assertEquals("Should have 3 tables", 3, event.tables.size());
        assertTrue("Should contain table 1", event.tables.contains(1));
        assertTrue("Should contain table 2", event.tables.contains(2));
        assertTrue("Should contain table 3", event.tables.contains(3));
    }

    @Test
    public void testMultipleEvents() {
        ArrayList<Integer> tables2 = new ArrayList<>();
        tables2.add(4);
        tables2.add(5);

        Event event2 = new Event("Wedding", "06/15/2026", 1900, tables2);

        assertNotNull(event);
        assertNotNull(event2);
        assertNotEquals(event.name, event2.name);
        assertNotEquals(event.tables, event2.tables);
    }
}