import java.util.List;

/*
 * InterfaceManagement.java
 * 
 * Admin only access
 *
 * @author Fredrick Zawislak
 * @version 11/24/2025
 */

public interface InterfaceManagement {
    void setHours(String openTime, String closeTime);
    void setSeatingArrangement(List<String> seatingLayout);

    boolean createEvent(String name);
    boolean lockSection(List<Integer> list);
    boolean unlockSection(List<Integer> list);
    boolean isLocked(int list);

    boolean setPrice(String item, double price);
    double getPrice(String item);
    boolean processPayment(String username, double amount);
}
