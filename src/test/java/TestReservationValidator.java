import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * TestReservationValidator.java
 *
 * Unit tests for the ReservationValidator class
 *
 * @author Fredrick Zawislak
 * @version November 24, 2025
 */
public class TestReservationValidator {

    private File testFile;
    private long originalLength;

    @Before
    public void setUp() throws Exception {
        testFile = new File("test_validator_data.txt");
        if (!testFile.exists()) {
            testFile.createNewFile();
        }
        originalLength = testFile.length();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(testFile, true))) {
            bw.newLine();
            bw.write("Test validator data");
        }
    }

    @After
    public void tearDown() throws Exception {
        if (testFile.exists()) {
            try (RandomAccessFile raf = new RandomAccessFile(testFile, "rw")) {
                raf.setLength(originalLength);
            }
        }
    }

    @Test
    public void testValidReservationMethodExists() throws NoSuchMethodException {
        assertNotNull(ReservationValidator.class.getDeclaredMethod("validReservation", String.class));
    }

    @Test
    public void testValidTimeMethodExists() throws NoSuchMethodException {
        assertNotNull(ReservationValidator.class.getDeclaredMethod("validTime", String.class, String.class));
    }

    @Test
    public void testImplementsInterface() {
        assertTrue("Should implement InterfaceReservationValidator",
                InterfaceReservationValidator.class.isAssignableFrom(ReservationValidator.class));
    }

    @Test
    public void testValidReservationTomorrow() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        String tomorrowDate = tomorrow.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        assertTrue("Tomorrow should be valid", ReservationValidator.validReservation(tomorrowDate));
    }

    @Test
    public void testValidReservationWithinSevenDays() {
        LocalDate fiveDays = LocalDate.now().plusDays(5);
        String fiveDaysDate = fiveDays.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        assertTrue("Five days ahead should be valid", ReservationValidator.validReservation(fiveDaysDate));
    }

    @Test
    public void testInvalidReservationPastDate() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String yesterdayDate = yesterday.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        assertFalse("Yesterday should be invalid", ReservationValidator.validReservation(yesterdayDate));
    }

    @Test
    public void testInvalidReservationTooFarFuture() {
        LocalDate tenDays = LocalDate.now().plusDays(10);
        String tenDaysDate = tenDays.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        assertFalse("Ten days ahead should be invalid", ReservationValidator.validReservation(tenDaysDate));
    }

    @Test
    public void testInvalidDateFormat() {
        assertFalse("Invalid format should return false", ReservationValidator.validReservation("2025-11-25"));
        assertFalse("Invalid format should return false", ReservationValidator.validReservation("11-25-2025"));
        assertFalse("Invalid format should return false", ReservationValidator.validReservation("invalid"));
    }

    @Test
    public void testValidTimeFutureToday() {
        LocalDate today = LocalDate.now();
        String todayDate = today.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        LocalTime futureTime = LocalTime.now().plusHours(2);
        String futureTimeStr = futureTime.format(DateTimeFormatter.ofPattern("HHmm"));

        assertTrue("Future time today should be valid",
                ReservationValidator.validTime(futureTimeStr, todayDate));
    }

    @Test
    public void testValidTimeFutureDate() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        String tomorrowDate = tomorrow.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));

        assertTrue("Any time on future date should be valid",
                ReservationValidator.validTime("1200", tomorrowDate));
    }

    @Test
    public void testInvalidTimeFormat() {
        LocalDate today = LocalDate.now();
        String todayDate = today.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));

        assertFalse("Invalid time format should return false",
                ReservationValidator.validTime("12:00", todayDate));
        assertFalse("Invalid time format should return false",
                ReservationValidator.validTime("invalid", todayDate));
    }

    @Test
    public void testNullInputValidReservation() {
        try {
            ReservationValidator.validReservation(null);
            fail("Should handle null input");
        } catch (Exception e) {
            assertTrue("Should throw exception for null", true);
        }
    }

    @Test
    public void testNullInputValidTime() {
        try {
            ReservationValidator.validTime(null, "11/25/2025");
            fail("Should handle null time");
        } catch (Exception e) {
            assertTrue("Should throw exception for null", true);
        }
    }

    @Test
    public void testValidatorInstantiation() {
        ReservationValidator validator = new ReservationValidator();
        assertNotNull("Should be able to instantiate", validator);
    }
}