import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

import java.io.*;
import java.util.ArrayList;

public class TestUser {

    private final File usernamesFile = new File("usernames.txt");

    private static final String EXISTING_USER = "existingUser";
    private static final String EXISTING_PASS = "securePass1";
    private static final String NEW_USER = "newUser";
    private static final String NEW_PASS = "newPassword1";

    @Before
    public void setUp() throws Exception {
        // Create a minimal usernames.txt with one user
        if (usernamesFile.exists()) usernamesFile.delete();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(usernamesFile))) {
            bw.write(EXISTING_USER + ": " + EXISTING_PASS);
            bw.newLine();
            // Add one malformed/empty line to ensure methods tolerate it
            bw.newLine();
        }
    }

    @After
    public void tearDown() {
        if (usernamesFile.exists()) usernamesFile.delete();
    }

    @Test
    public void testCreateAccountSuccess() {
        User u = new User(NEW_USER, NEW_PASS, new ArrayList<Reservation>(), 0);

        // createAccount reads the file, so it should succeed for a new username
        boolean created = u.createAccount(NEW_USER, NEW_PASS);
        assertTrue("createAccount should return true for valid new credentials", created);

        // The file should now contain the new line (login should succeed)
        User check = new User(NEW_USER, NEW_PASS, new ArrayList<Reservation>(), 0);
        assertTrue("login should succeed after creating account", check.login(NEW_USER, NEW_PASS));
    }

    @Test
    public void testCreateAccountDuplicateFails() {
        User u = new User("whatever", "whatever", new ArrayList<Reservation>(), 0);
        // Attempt to create the same username that already exists in the file (EXISTING_USER)
        boolean created = u.createAccount(EXISTING_USER, "anotherPass1");
        assertFalse("createAccount should return false when username already exists", created);
    }

    @Test
    public void testCreateAccountInvalidUsernameOrPassword() {
        User u = new User("x", "irrelevant", new ArrayList<Reservation>(), 0);

        // username too short
        assertFalse("username too short should be rejected", u.createAccount("ab", "validPass1"));

        // username with special characters
        assertFalse("username with special chars should be rejected", u.createAccount("bad*name", "validPass1"));

        // password too short
        assertFalse("password too short should be rejected", u.createAccount("validName", "short"));

        // password contains colon/backslash
        assertFalse("password with colon should be rejected", u.createAccount("validName2", "bad:pass123"));
        assertFalse("password with backslash should be rejected", u.createAccount("validName3", "bad\\pass123"));
    }

    @Test
    public void testLoginSuccessAndFailure() {
        User u = new User(EXISTING_USER, EXISTING_PASS, new ArrayList<Reservation>(), 0);

        // correct credentials
        assertTrue("login should succeed for correct credentials", u.login(EXISTING_USER, EXISTING_PASS));

        // wrong password
        assertFalse("login should fail for wrong password", u.login(EXISTING_USER, "wrongPass"));

        // non-existent user
        assertFalse("login should fail for non-existent user", u.login("noSuchUser", "anything"));
    }

    @Test
    public void testDeleteAccountExistingAndNonExisting() throws Exception {
        User admin = new User("admin", "p", new ArrayList<Reservation>(), 0);

        // Deleting an existing account should return true
        boolean deleted = admin.deleteAccount(EXISTING_USER);
        assertTrue("deleteAccount should return true when removing existing user", deleted);

        // After deletion, login with that user should fail
        assertFalse("login should fail for deleted user", admin.login(EXISTING_USER, EXISTING_PASS));

        // Deleting a non-existent user should return false
        boolean deleted2 = admin.deleteAccount("definitelyNotThere");
        assertFalse("deleteAccount should return false for non-existent user", deleted2);
    }

    @Test
    public void testReservationsGetSetAddRemove() {
        ArrayList<Reservation> list = new ArrayList<>();
        User u = new User("resUser", "pass", list, 0);

        // Initially empty
        assertNotNull("reservations list should not be null", u.getReservations());
        assertTrue("reservations list should start empty", u.getReservations().isEmpty());

        // addReservation
        Reservation r = new Reservation("resUser", 1, "12/01/2025", 1800, 2);
        u.addReservation(r);
        assertEquals("reservations should have one after add", 1, u.getReservations().size());
        assertTrue("list contains the added reservation", u.getReservations().contains(r));

        // removeReservation
        u.removeReservation(r);
        assertTrue("reservations should be empty after remove", u.getReservations().isEmpty());

        // setReservations
        ArrayList<Reservation> newList = new ArrayList<>();
        newList.add(r);
        u.setReservations(newList);
        assertEquals("setReservations should replace list", 1, u.getReservations().size());
        assertSame("getReservations should return the same list instance set", newList, u.getReservations());
    }

    @Test
    public void testReservationNumberGetSet() {
        User u = new User("rnUser", "pass", new ArrayList<Reservation>(), 5);
        assertEquals("initial reservation number should match constructor", 5, u.getReservationNumber());

        u.setReservationNumber(42);
        assertEquals("reservation number should be updated by setter", 42, u.getReservationNumber());
    }
}