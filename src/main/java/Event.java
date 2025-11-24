import java.util.ArrayList;

/**
 * Event.java
 *
 * @author Fredrick Zawislak
 *
 * @version November 24, 2025
 *
 */

public class Event implements InterfaceEvent {
    String name;
    String date;
    int time;
    ArrayList<Integer> tables;

    Event(String name, String date, int time, ArrayList<Integer> tables) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.tables = tables;
    }
}