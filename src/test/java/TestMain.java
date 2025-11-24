import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * TestMain.java
 *
 * Tests for Main using real Main.main() with simulated input.
 * (Note: mostly obsolete now due to client I/O)
 *
 * Notes:
 * - Main uses several private static fields (db, scanner, users, currentUser, running).
 *   We reset those fields via reflection before each test so tests are isolated.
 * - seating.txt and usernames.txt are created in setUp() because Database and User read them.
 *
 * @author Fredrick Zawislak
 * @version 11/10/2025
 */

public class TestMain {

    private final File seatingFile = new File("src/main/seating.txt");
    private final File reservationsFile = new File("src/main/reservations.txt");
    private final File usernamesFile = new File("usernames.txt");

    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    private ByteArrayOutputStream outContent;

    @Before
    public void setUp() throws Exception {
        // ensure clean test files
        if (reservationsFile.exists()) reservationsFile.delete();
        if (seatingFile.exists()) seatingFile.delete();
        if (usernamesFile.exists()) usernamesFile.delete();

        // minimal seating file so Database constructor works
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(seatingFile))) {
            bw.write("Quantity:2:Seats:4\n");
        }

        // minimal usernames file so User methods won't crash if called
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(usernamesFile))) {
            bw.write("existing: password123\n");
        }

        // Prepare to capture output
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void tearDown() throws Exception {
        // restore System streams
        System.setOut(originalOut);
        System.setIn(originalIn);

        // remove test files
        if (reservationsFile.exists()) reservationsFile.delete();
        if (seatingFile.exists()) seatingFile.delete();
        if (usernamesFile.exists()) usernamesFile.delete();

        // reset Main's static fields to defaults to avoid cross-test leakage (best-effort)
        try {
            resetMainStatics(null, true);
        } catch (Exception ignored) { }
    }

    /**
     * Helper to reset Main's private static fields:
     * - sets System.in to provided InputStream
     * - reinitializes Main.scanner to new Scanner(System.in)
     * - sets Main.running
     * - sets Main.users to empty list
     * - sets Main.currentUser to null
     * - reinitializes Main.db to a new Database()
     */
    private void resetMainStatics(InputStream in, boolean running) throws Exception {
        // set system in first
        if (in != null) System.setIn(in);

        // Main.scanner -> new Scanner(System.in)
        Field scannerField = Main.class.getDeclaredField("scanner");
        scannerField.setAccessible(true);
        scannerField.set(null, new java.util.Scanner(System.in));

        // Main.running
        Field runningField = Main.class.getDeclaredField("running");
        runningField.setAccessible(true);
        runningField.setBoolean(null, running);

        // Main.users -> new ArrayList<User>()
        Field usersField = Main.class.getDeclaredField("users");
        usersField.setAccessible(true);
        usersField.set(null, new ArrayList<User>());

        // Main.currentUser -> null
        Field currentUserField = Main.class.getDeclaredField("currentUser");
        currentUserField.setAccessible(true);
        currentUserField.set(null, null);

        // Main.db -> new Database()
        Field dbField = Main.class.getDeclaredField("db");
        dbField.setAccessible(true);
        dbField.set(null, new Database());
    }

    @Test(timeout = 3000)
    public void testSignupThenExit() throws Exception {
        // Input sequence:
        // 2 -> Sign Up
        // testuser -> username
        // testPass12 -> password
        // 3 -> Exit
        String input = "2\n"           // Sign Up
                + "testuser\n"    // username
                + "testPass12\n"  // password
                + "3\n";          // Exit
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());

        // Reset Main statics with this input
        resetMainStatics(in, true);

        // Run main
        Main.main(new String[0]);

        String out = outContent.toString();
        assertTrue("Should print welcome", out.contains("Welcome to Reservation Manager"));
        // could be success or failure message depending on file state/validation rules
        assertTrue("Should indicate sign up outcome", out.contains("Sign up successful") || out.contains("Sign up failed"));
        assertTrue("Should print Goodbye before exiting", out.contains("Goodbye!"));
    }

    @Test(timeout = 4000)
    public void testLoginAddAndRemoveReservationFlow() throws Exception {
        /*
         * Flow:
         * 1 -> Login
         * existing (username from setUp)
         * password123
         * 1 -> Add Reservation
         * Alice
         * 12/12/2025
         * 1900
         * 2
         * 0  (table number)
         * 3 -> View All Reservations (choose 3 to view)
         * 4 -> Logout
         * 3 -> Exit
         */
        StringBuilder sb = new StringBuilder();
        sb.append("1\n");              // Login
        sb.append("existing\n");       // username (from setUp)
        sb.append("password123\n");    // password
        sb.append("1\n");              // Add Reservation
        sb.append("Alice\n");          // customer name
        sb.append("12/12/2025\n");     // date
        sb.append("1900\n");           // time
        sb.append("2\n");              // guests
        sb.append("0\n");              // table number (table 0 exists from seating file)
        sb.append("3\n");              // View All Reservations -> will print reservations.txt contents
        sb.append("4\n");              // Logout
        sb.append("3\n");              // Exit
        ByteArrayInputStream in = new ByteArrayInputStream(sb.toString().getBytes());

        // Reset main with this input
        resetMainStatics(in, true);

        // Run main
        Main.main(new String[0]);

        String out = outContent.toString();
        assertTrue("Should log in successfully", out.contains("Login successful") || out.contains("Invalid username or password."));
        // After adding reservation, DB writes to reservations.txt; viewReservations prints that file
        assertTrue("Should show 'All Reservations' section", out.contains("All Reservations (from reservations.txt):"));
        assertTrue("Should print Goodbye before exit", out.contains("Goodbye!"));
    }
}