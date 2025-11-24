import java.util.ArrayList;

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