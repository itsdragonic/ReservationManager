import java.util.List;

/*
 * Server stuff that will be implemented in phase 2
 *
 *
 */

public interface InterfaceManagement {
    void setHours(String openTime, String closeTime);
    void setSeatingArrangement(List<String> seatingLayout);
    boolean lockSection(String sectionName);
    boolean unlockSection(String sectionName);

    boolean setPrice(String item, double price);
    double getPrice(String item);
    boolean processPayment(String username, double amount);
}
