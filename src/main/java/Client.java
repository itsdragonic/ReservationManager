import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.JFrame;

/**
 * Reservation Manager Project
 *
 * Client side interface for the project
 * Interacts with Server.java on the port 1234
 * Most implementation of functions can be seen through corresponding switch cases in Server.java
 *
 * @author Jeremiah Tomasello
 *
 * @version November 18, 2025
 *
 */

public class Client implements InterfaceClient {
    private static Scanner scanner = new Scanner(System.in);
    private static User currentUser;
    private static boolean running = true;
    public static BufferedReader reader;
    public static PrintWriter writer;
    public static boolean loggedIn = false;
    public static boolean loggedInAdmin = false;


    // This menu is what is always first shown
    public static void signedOutMenu(Socket socket) {
        JFrame frame1 = new JFrame("Signed Out");
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals("Login")) {
                    login(socket);
                    if (loggedInAdmin) {
                        frame1.dispose();
                        adminMenu(socket);  // Go to admin menu
                    } else if (loggedIn) {
                        frame1.dispose();
                        mainMenu(socket);   // Go to regular menu
                    }
                } else if (e.getActionCommand().equals("Sign Up")) {
                    signUp(socket);
                    if (loggedIn) {
                        frame1.dispose();
                        mainMenu(socket);
                    }
                } else if (e.getActionCommand().equals("Exit")) {
                    running = false;
                    frame1.dispose();
                }
            }
        };

        frame1.setSize(1280, 720);
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        frame1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame1.setResizable(true);

        ImageIcon logoIcon = new ImageIcon("logo.png");
        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setVerticalAlignment(SwingConstants.CENTER);
        frame1.add(logoLabel, BorderLayout.NORTH);

        JButton loginButton = new JButton("Login");
        loginButton.setActionCommand("Login");
        loginButton.addActionListener(actionListener);

        JButton signUpButton = new JButton("Sign Up");
        signUpButton.setActionCommand("Sign Up");
        signUpButton.addActionListener(actionListener);

        JButton exitButton = new JButton("Exit");
        exitButton.setActionCommand("Exit");
        exitButton.addActionListener(actionListener);

        loginButton.setPreferredSize(new Dimension(200, 100));
        signUpButton.setPreferredSize(new Dimension(200, 100));
        exitButton.setPreferredSize(new Dimension(200, 100));
        panel.add(loginButton);
        panel.add(signUpButton);
        panel.add(exitButton);

        frame1.add(panel);
        frame1.setVisible(true);


    }


    public static void login(Socket socket) {
        String username = JOptionPane.showInputDialog(null, "Enter your Username");
        String password = JOptionPane.showInputDialog(null, "Enter your Password");

        // Send login info to Server to confirm

        try {
            // Compare server output
            writer.println("login");
            writer.println(username);
            writer.println(password);
            writer.flush();

            String result = reader.readLine();

            if (result.equals("success")) {
                currentUser = new User(username, password, new ArrayList<Reservation>(), 0);
                JOptionPane.showMessageDialog(null, "Login Successful, welcome " + username + "!");
                loggedIn = true;
            } else if (result.equals("admin")) {
                currentUser = new User(username, password, new ArrayList<Reservation>(), 0);
                JOptionPane.showMessageDialog(null, "Login Successful, welcome " + username + "!");
                loggedInAdmin = true;
            } else {
                JOptionPane.showMessageDialog(null, "Invalid username or password. Please try again.");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Unexcepted network fail. Please try again.");
        }
    }

    public static void signUp(Socket socket) {
        String username = JOptionPane.showInputDialog(null, "Enter New Username");
        String password = JOptionPane.showInputDialog(null, "Enter Password");

        try {

            // Send sign up info to Server to confirm
            writer.println("signUp");
            writer.println(username);
            writer.println(password);
            writer.flush();

            // Compare Server output
            String result = reader.readLine();

            if (result.equals("success")) {
                currentUser = new User(username, password, new ArrayList<Reservation>(), 0);
                JOptionPane.showMessageDialog(null, "Sign Up Successful, welcome " + username + "!");
                loggedIn = true;
            } else {
                JOptionPane.showMessageDialog(null, "Invalid username or password. Please try again.");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Unexcepted network fail. Please try again.");
        }
    }

    // This menu is always shown when a user is currently logged in
    public static void mainMenu(Socket socket) {
        JFrame frame = new JFrame("Main Menu");
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals("Add Reservation")) {
                    addReservation(socket);
                } else if (e.getActionCommand().equals("Join Event")) {
                    joinEvent(socket);
                } else if (e.getActionCommand().equals("Remove Reservation")) {
                    removeReservation(socket);
                }
                // doesn't do anything currently, I think we should combine with remove to make a reservation manager instead
                else if (e.getActionCommand().equals("View All Reservations")) {
                    viewReservations(socket);
                } else if (e.getActionCommand().equals("Logout")) {
                    currentUser = null;
                    signedOutMenu(socket);
                    loggedIn = false;
                    loggedInAdmin = false;
                    frame.dispose();
                } else if (e.getActionCommand().equals("Delete Account")) {
                    deleteAccount(socket);
                    if (!loggedIn) {
                        frame.dispose();
                        signedOutMenu(socket);
                        currentUser = null;
                    }
                }

            }
        };

        ImageIcon logoIcon = new ImageIcon("logo.png");
        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setVerticalAlignment(SwingConstants.CENTER);
        frame.add(logoLabel, BorderLayout.NORTH);

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1280, 720);
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        frame.setResizable(true);

        JButton addReservationButton = new JButton("Add Reservation");
        addReservationButton.setActionCommand("Add Reservation");
        addReservationButton.addActionListener(actionListener);

        JButton joinEventButton = new JButton("Join Event");
        joinEventButton.setActionCommand("Join Event");
        joinEventButton.addActionListener(actionListener);

        JButton removeReservationButton = new JButton("Remove Reservation");
        removeReservationButton.setActionCommand("Remove Reservation");
        removeReservationButton.addActionListener(actionListener);

        JButton viewButton = new JButton("View All Reservations");
        viewButton.setActionCommand("View All Reservations");
        viewButton.addActionListener(actionListener);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setActionCommand("Logout");
        logoutButton.addActionListener(actionListener);

        JButton deleteAccountButton = new JButton("Delete Account");
        deleteAccountButton.setActionCommand("Delete Account");
        deleteAccountButton.addActionListener(actionListener);

        panel.add(addReservationButton);
        panel.add(joinEventButton);
        panel.add(removeReservationButton);
        panel.add(viewButton);
        panel.add(logoutButton);
        panel.add(deleteAccountButton);

        frame.add(panel);
        frame.setVisible(true);

    }

    public static void adminMenu(Socket socket) {
        JFrame frame = new JFrame("Admin Menu");
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals("Set Hours")) {
                    setOpenCloseHours(socket);
                } else if (e.getActionCommand().equals("Edit Seating")) {
                    editSeatingArrangement(socket);
                } else if (e.getActionCommand().equals("Create Event")) {
                    createEvent(socket);
                } else if (e.getActionCommand().equals("Change Prices")) {
                    changePrices(socket);
                } else if (e.getActionCommand().equals("Logout")) {
                    currentUser = null;
                    signedOutMenu(socket);
                    loggedIn = false;
                    loggedInAdmin = false;
                    frame.dispose();
                }
            }
        };

        ImageIcon logoIcon = new ImageIcon("logo.png");
        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setVerticalAlignment(SwingConstants.CENTER);
        frame.add(logoLabel, BorderLayout.NORTH);

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1280, 720);
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        frame.setResizable(true);

        JLabel adminLabel = new JLabel(" ADMIN ");
        adminLabel.setFont(new Font("Arial", Font.BOLD, 24));
        adminLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(adminLabel);

        JButton setHoursButton = new JButton("Set Open/Close Hours");
        setHoursButton.setActionCommand("Set Hours");
        setHoursButton.addActionListener(actionListener);
        setHoursButton.setPreferredSize(new Dimension(250, 80));

        JButton editSeatingButton = new JButton("Edit Seating Arrangement");
        editSeatingButton.setActionCommand("Edit Seating");
        editSeatingButton.addActionListener(actionListener);
        editSeatingButton.setPreferredSize(new Dimension(250, 80));

        JButton createEventButton = new JButton("Create Event");
        createEventButton.setActionCommand("Create Event");
        createEventButton.addActionListener(actionListener);
        createEventButton.setPreferredSize(new Dimension(250, 80));

        JButton changePricesButton = new JButton("Change Prices");
        changePricesButton.setActionCommand("Change Prices");
        changePricesButton.addActionListener(actionListener);
        changePricesButton.setPreferredSize(new Dimension(250, 80));

        JButton logoutButton = new JButton("Logout");
        logoutButton.setActionCommand("Logout");
        logoutButton.addActionListener(actionListener);
        logoutButton.setPreferredSize(new Dimension(250, 80));

        panel.add(setHoursButton);
        panel.add(editSeatingButton);
        panel.add(createEventButton);
        panel.add(changePricesButton);
        panel.add(logoutButton);

        frame.add(panel);
        frame.setVisible(true);
    }

    public static void setOpenCloseHours(Socket socket) {
        try {
            // Generate time options in 15 minute increments (military time)
            ArrayList<String> timeOptions = new ArrayList<>();
            for (int hour = 0; hour < 24; hour++) {
                for (int minute = 0; minute < 60; minute += 15) {
                    String time = String.format("%02d%02d", hour, minute);
                    timeOptions.add(time);
                }
            }

            String[] times = timeOptions.toArray(new String[0]);

            // Get current hours from server
            writer.println("getHours");
            writer.flush();

            String currentHours = reader.readLine(); // Format: "800:2200"
            String[] parts = currentHours.split(":");
            String currentOpen = parts[0];
            String currentClose = parts[1];

            // Format current times for display
            if (currentOpen.length() == 3) currentOpen = "0" + currentOpen;
            if (currentClose.length() == 3) currentClose = "0" + currentClose;

            // Select new open time
            String newOpenTime = (String) JOptionPane.showInputDialog(
                    null,
                    "Current open time: " + currentOpen + "\nSelect new open time:",
                    "Set Open Time",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    times,
                    currentOpen
            );

            if (newOpenTime == null) return; // User cancelled

            // Select new close time
            String newCloseTime = (String) JOptionPane.showInputDialog(
                    null,
                    "Current close time: " + currentClose + "\nSelect new close time:",
                    "Set Close Time",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    times,
                    currentClose
            );

            if (newCloseTime == null) return; // User cancelled

            // Validate that close time is after open time
            int openInt = Integer.parseInt(newOpenTime);
            int closeInt = Integer.parseInt(newCloseTime);

            if (closeInt <= openInt) {
                JOptionPane.showMessageDialog(null, "Close time must be after open time!");
                return;
            }

            // Send to server
            writer.println("setHours");
            writer.println(newOpenTime);
            writer.println(newCloseTime);
            writer.flush();

            String result = reader.readLine();
            JOptionPane.showMessageDialog(null, result);

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Network error occurred.");
        }
    }

    public static void editSeatingArrangement(Socket socket) {
        try {
            // Request current seating data from server
            writer.println("getSeating");
            writer.flush();

            String seatingData = reader.readLine();

            if (seatingData.equals("No seating data")) {
                JOptionPane.showMessageDialog(null, "No seating data found.");
                return;
            }

            // Parse seating data: "1:15:25|2:10:40|4:25:75|8:10:140|12:5:200"
            // Format: seats:quantity:price|seats:quantity:price...
            String[] seatGroups = seatingData.split("\\|");

            // Create display options
            ArrayList<String> options = new ArrayList<>();
            options.add("Add New Seat Size");

            for (String group : seatGroups) {
                String[] parts = group.split(":");
                int seats = Integer.parseInt(parts[0]);
                int quantity = Integer.parseInt(parts[1]);
                int price = Integer.parseInt(parts[2]);
                options.add(seats + " Seats - Quantity: " + quantity + " - $" + price);
            }

            String[] optionsArray = options.toArray(new String[0]);

            ImageIcon tableIcon = new ImageIcon("tables.png");
            String choice = (String) JOptionPane.showInputDialog(
                    null,
                    "Select seating arrangement to edit:",
                    "Edit Seating",
                    JOptionPane.PLAIN_MESSAGE,
                    tableIcon,
                    optionsArray,
                    null
            );

            if (choice == null) return;

            if (choice.equals("Add New Seat Size")) {
                // Add new seat size
                String seatsInput = (String) JOptionPane.showInputDialog(
                        null,
                        "Enter number of seats:",
                        "Seat Entry",
                        JOptionPane.PLAIN_MESSAGE,
                        tableIcon,
                        null,
                        null
                );
                if (seatsInput == null) return;

                String quantityInput = (String) JOptionPane.showInputDialog(
                        null,
                        "Enter quantity of tables:",
                        "Quantity",
                        JOptionPane.PLAIN_MESSAGE,
                        tableIcon,
                        null,
                        null
                );
                if (quantityInput == null) return;

                String priceInput = (String) JOptionPane.showInputDialog(
                        null,
                        "Enter price:",
                        "Price",
                        JOptionPane.PLAIN_MESSAGE,
                        tableIcon,
                        null,
                        null
                );
                if (priceInput == null) return;

                try {
                    int seats = Integer.parseInt(seatsInput);
                    int quantity = Integer.parseInt(quantityInput);
                    int price = Integer.parseInt(priceInput);

                    if (seats <= 0 || quantity <= 0 || price <= 0) {
                        JOptionPane.showMessageDialog(null, "All values must be positive!");
                        return;
                    }

                    writer.println("addSeating");
                    writer.println(seats);
                    writer.println(quantity);
                    writer.println(price);
                    writer.flush();

                    String result = reader.readLine();
                    JOptionPane.showMessageDialog(null, result);

                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Invalid input! Please enter numbers only.");
                }

            } else {
                // Edit existing seat size - extract the seat number
                String[] parts = choice.split(" ");
                int seats = Integer.parseInt(parts[0]);

                String newQuantity = JOptionPane.showInputDialog(null,
                        "Current seating: " + choice + "\nEnter new quantity:");

                if (newQuantity == null) return;

                try {
                    int quantity = Integer.parseInt(newQuantity);

                    if (quantity < 0) {
                        JOptionPane.showMessageDialog(null, "Quantity cannot be negative!");
                        return;
                    }

                    writer.println("updateSeating");
                    writer.println(seats);
                    writer.println(quantity);
                    writer.flush();

                    String result = reader.readLine();
                    JOptionPane.showMessageDialog(null, result);

                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Invalid input! Please enter a number.");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Network error occurred.");
        }
    }

    public static void createEvent(Socket socket) {
        String[] options = {"Create Event", "Delete Event"};

        String choice = (String) JOptionPane.showInputDialog(
                null,
                "Select an option:",
                "Event Management",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                null
        );

        if (choice == null) return;

        if (choice.equals("Create Event")) {
            createNewEvent(socket);
        } else {
            deleteExistingEvent(socket);
        }
    }

    public static void createNewEvent(Socket socket) {
        try {
            // Get event name
            String eventName = JOptionPane.showInputDialog(null, "Enter event name:");
            if (eventName == null || eventName.trim().isEmpty()) return;

            // Get date - allow any future date
            String dateInput = JOptionPane.showInputDialog(null, "Enter date (MM/DD/YYYY):");
            if (dateInput == null) return;

            // Validate date format
            if (!dateInput.matches("\\d{2}/\\d{2}/\\d{4}")) {
                JOptionPane.showMessageDialog(null, "Invalid date format. Please use MM/DD/YYYY");
                return;
            }

            // Generate time options in 15 minute increments (military time)
            ArrayList<String> timeOptions = new ArrayList<>();
            for (int hour = 0; hour < 24; hour++) {
                for (int minute = 0; minute < 60; minute += 15) {
                    String time = String.format("%02d:%02d", hour, minute);
                    timeOptions.add(time);
                }
            }

            String[] times = timeOptions.toArray(new String[0]);

            String timeChoice = (String) JOptionPane.showInputDialog(
                    null,
                    "Select event time:",
                    "Event Time",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    times,
                    null
            );

            if (timeChoice == null) return;

            // Convert time to military format (remove colon)
            String militaryTime = timeChoice.replace(":", "");
            int eventTime = Integer.parseInt(militaryTime);

            // Now select tables for the event
            ArrayList<Integer> selectedTables = new ArrayList<>();
            boolean selectingTables = true;

            while (selectingTables) {
                // Get available tables from server
                writer.println("getAvailableTables");
                writer.println(dateInput);
                writer.println(eventTime);
                writer.flush();

                String tablesData = reader.readLine();

                if (tablesData.equals("No tables available")) {
                    JOptionPane.showMessageDialog(null, "No more tables available for this time.");
                    break;
                }

                // Parse table data: "0:1:25|1:1:25|2:2:40..."
                // Format: tableNum:seats:price|...
                String[] tableArray = tablesData.split("\\|");
                ArrayList<String> displayOptions = new ArrayList<>();
                displayOptions.add("Done selecting tables");

                for (String table : tableArray) {
                    String[] parts = table.split(":");
                    int tableNum = Integer.parseInt(parts[0]);

                    // Skip if already selected
                    if (selectedTables.contains(tableNum)) continue;

                    int seats = Integer.parseInt(parts[1]);
                    int price = Integer.parseInt(parts[2]);
                    displayOptions.add("Table " + tableNum + " (" + seats + " seats, $" + price + ")");
                }

                if (displayOptions.size() == 1) {
                    JOptionPane.showMessageDialog(null, "All available tables have been selected.");
                    break;
                }

                String[] optionsArray = displayOptions.toArray(new String[0]);

                ImageIcon tableIcon = new ImageIcon("tables.png");
                String tableChoice = (String) JOptionPane.showInputDialog(
                        null,
                        "Currently selected " + selectedTables.size() + " table(s)\nSelect a table to add:",
                        "Select Tables",
                        JOptionPane.PLAIN_MESSAGE,
                        tableIcon,
                        optionsArray,
                        null
                );

                if (tableChoice == null || tableChoice.equals("Done selecting tables")) {
                    selectingTables = false;
                } else {
                    // Extract table number from choice
                    String[] parts = tableChoice.split(" ");
                    int tableNum = Integer.parseInt(parts[1]);
                    selectedTables.add(tableNum);
                }
            }

            if (selectedTables.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No tables selected. Event creation cancelled.");
                return;
            }

            // Send event data to server
            writer.println("createEvent");
            writer.println(eventName);
            writer.println(dateInput);
            writer.println(eventTime);
            writer.println(selectedTables.size());
            for (int tableNum : selectedTables) {
                writer.println(tableNum);
            }
            writer.flush();

            String result = reader.readLine();
            JOptionPane.showMessageDialog(null, result);

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Network error occurred.");
        }
    }

    public static void deleteExistingEvent(Socket socket) {
        try {
            // Request list of events from server
            writer.println("getEvents");
            writer.flush();

            String eventsData = reader.readLine();

            if (eventsData.equals("No events found")) {
                JOptionPane.showMessageDialog(null, "No events to delete.");
                return;
            }

            // Parse events: "eventName:date:time|eventName:date:time..."
            String[] eventArray = eventsData.split("\\|");
            ArrayList<String> displayOptions = new ArrayList<>();

            for (String event : eventArray) {
                String[] parts = event.split(":");
                String name = parts[0];
                String date = parts[1];
                String time = parts[2];

                // Format time for display
                if (time.length() == 3) time = "0" + time;
                String formattedTime = time.substring(0, 2) + ":" + time.substring(2);

                displayOptions.add(name + " - " + date + " at " + formattedTime);
            }

            String[] optionsArray = displayOptions.toArray(new String[0]);

            String choice = (String) JOptionPane.showInputDialog(
                    null,
                    "Select event to delete:",
                    "Delete Event",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    optionsArray,
                    null
            );

            if (choice == null) return;

            // Extract event name from choice
            String eventName = choice.split(" - ")[0];

            // Confirm deletion
            int confirm = JOptionPane.showConfirmDialog(
                    null,
                    "Are you sure you want to delete event: " + eventName + "?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) return;

            // Send delete request to server
            writer.println("deleteEvent");
            writer.println(eventName);
            writer.flush();

            String result = reader.readLine();
            JOptionPane.showMessageDialog(null, result);

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Network error occurred.");
        }
    }

    public static void changePrices(Socket socket) {
        try {
            // Request current seating data from server
            writer.println("getSeating");
            writer.flush();

            String seatingData = reader.readLine();

            if (seatingData.equals("No seating data")) {
                JOptionPane.showMessageDialog(null, "No seating data found.");
                return;
            }

            // Parse seating data: "1:15:25|2:10:40|4:25:75|8:10:140|12:5:200"
            String[] seatGroups = seatingData.split("\\|");

            // Create display options
            ArrayList<String> options = new ArrayList<>();

            for (String group : seatGroups) {
                String[] parts = group.split(":");
                int seats = Integer.parseInt(parts[0]);
                int quantity = Integer.parseInt(parts[1]);
                int price = Integer.parseInt(parts[2]);
                options.add(seats + " Seats - Current Price: $" + price);
            }

            String[] optionsArray = options.toArray(new String[0]);

            String choice = (String) JOptionPane.showInputDialog(
                    null,
                    "Select table size to change price:",
                    "Change Prices",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    optionsArray,
                    null
            );

            if (choice == null) return;

            // Extract seat number from choice
            String[] parts = choice.split(" ");
            int seats = Integer.parseInt(parts[0]);

            String newPriceInput = JOptionPane.showInputDialog(null,
                    choice + "\nEnter new price:");

            if (newPriceInput == null) return;

            try {
                int newPrice = Integer.parseInt(newPriceInput);

                if (newPrice <= 0) {
                    JOptionPane.showMessageDialog(null, "Price must be positive!");
                    return;
                }

                writer.println("updatePrice");
                writer.println(seats);
                writer.println(newPrice);
                writer.flush();

                String result = reader.readLine();
                JOptionPane.showMessageDialog(null, result);

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input! Please enter a number.");
            }

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Network error occurred.");
        }
    }

    public static void joinEvent(Socket socket) {
        try {
            String eventName = JOptionPane.showInputDialog(null, "Enter event name");
            if (eventName == null) return;

            writer.println("joinEvent");
            writer.println(currentUser.getUsername());
            writer.println(eventName);
            writer.flush();

            String line = reader.readLine();

            if (line.startsWith("Sorry") || line.equals("Event not found.")) {
                JOptionPane.showMessageDialog(null, line);
                return;
            }

            // Parse available tables from "Available tables: 1 2 3"
            String[] parts = line.split(": ");
            if (parts.length < 2) {
                JOptionPane.showMessageDialog(null, "Error retrieving available tables.");
                return;
            }

            String[] availableTables = parts[1].trim().split(" ");

            String tableChoice = (String) JOptionPane.showInputDialog(
                    null,
                    line + "\nSelect a table:",
                    "Join Event",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    availableTables,
                    null
            );

            if (tableChoice == null) return;

            int table = Integer.parseInt(tableChoice);

            writer.println(table);
            writer.flush();

            String result = reader.readLine();
            JOptionPane.showMessageDialog(null, result);

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Network error occurred.");
        }
    }

    public static void addReservation(Socket socket) {
        String[] nextSevenDays = ReservationValidator.getNextSevenDays();
        String date = (String) JOptionPane.showInputDialog(null, "Select a reservation date",
                "Reservations", JOptionPane.PLAIN_MESSAGE, null, nextSevenDays, null);

        if (date == null) {
            return;
        }
        try {
            // Gathers reservation info to create it in Server
            boolean valid = ReservationValidator.validReservation(date);
            if (!valid) {
                JOptionPane.showMessageDialog(null, "Invalid date. Please try again.");
                return;
            }
            writer.println("getTimes");
            writer.flush();

            // Ask server for open/close times
            String times = reader.readLine();     // Example: "800:2200"
            String openTime = times.split(":")[0];
            if (openTime.length() == 3) {
                openTime = "0" + openTime;
            }
            String closeTime = times.split(":")[1];

            ArrayList<String> temp = ReservationValidator.getAvailableTime(date, openTime, closeTime);
            String[] availableTimes = new String[temp.size()];
            for (int i = 0; i < temp.size(); i++) {
                availableTimes[i] = temp.get(i);
            }
            if (availableTimes.length == 0) {
                JOptionPane.showMessageDialog(null, "No available times. Please try again.");
                return;
            }

            String time = (String) JOptionPane.showInputDialog(null, "Select reservation time", "Time", JOptionPane.PLAIN_MESSAGE, null, availableTimes, null);
            if (time == null) {
                return;
            }
            time = time.split(":")[0] + time.split(":")[1];

            boolean validTime = ReservationValidator.validTime(time, date);
            if (!validTime) {
                JOptionPane.showMessageDialog(null, "Invalid time. Please try again.");
                return;
            }
            int resTime = Integer.parseInt(time);

            String[] guestArray = new String[6];
            for (int i = 0; i < 6; i++) {
                guestArray[i] = "" + (i + 1);
            }

            // Changed into a dropdown box to make more User-friendly
            String guest = (String) JOptionPane.showInputDialog(null, "Select number of guests", "Guests",
                    JOptionPane.PLAIN_MESSAGE, null, guestArray, null);
            if (guest == null) {
                return;
            }

            // Get available tables, then display them here
            writer.println("addReservation");
            // Send reservation info to server to create it
            // Must be sent now as the Server will cross-check the current reservation info with current reservations
            // This will allow for a curated display of available tables
            writer.println(currentUser.getUsername());
            writer.println(date);
            writer.println(resTime);
            writer.println(guest);
            writer.flush();


            String line = reader.readLine();

            // Check if line is empty
            if (line == null || line.trim().isEmpty() || !line.contains("Table")) {
                JOptionPane.showMessageDialog(null, "No available tables for this time and party size.");
                writer.println("cancel");
                writer.flush();
                String response = reader.readLine();
                return;
            }

            String[] availableTables = line.split(", ");

            String userChoice;
            if (availableTables.length == 1 && !availableTables[0].contains("Table")) {
                JOptionPane.showMessageDialog(null, "No available tables.");
                return;
            }

            ImageIcon tableIcon = new ImageIcon("tables.png");
            userChoice = (String) JOptionPane.showInputDialog(null, "Select your table", "Table", JOptionPane.PLAIN_MESSAGE, tableIcon, availableTables, null);

            if (userChoice == null) {
                writer.println("cancel");
                writer.flush();

                try {
                    String response = reader.readLine();
                    JOptionPane.showMessageDialog(null, response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
            String[] tableNumber = userChoice.split(" ");

            int table = Integer.parseInt(tableNumber[1]);

            // "Credit card payment"
            String card = JOptionPane.showInputDialog(null, "Enter credit card number (16 digits)", "Card", JOptionPane.PLAIN_MESSAGE);
            if (card == null) {
                writer.println("cancel");
                writer.flush();

                try {
                    String response = reader.readLine();
                    JOptionPane.showMessageDialog(null, response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
            if (!card.matches("\\d{16}")) {
                JOptionPane.showMessageDialog(null, "Invalid credit card. Reservation cancelled.");
                writer.println("cancel");
                writer.flush();

                try {
                    String response = reader.readLine();
                    JOptionPane.showMessageDialog(null, response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
            JOptionPane.showMessageDialog(null, "Payment complete");

            // Send remaining reservation info
            writer.println(table);
            writer.flush();

            // Display Server output on reservation creation
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void removeReservation(Socket socket) {
        try {

            // Repeat of view reservations code
            writer.println("viewReservations");
            writer.println(currentUser.getUsername());
            writer.flush();

            String allReservations = reader.readLine();

            if (allReservations.equals("No reservations found")) {
                JOptionPane.showMessageDialog(null, "No reservations found.");
                return;
            }

            // Format current reservations so it's just their ID's (so they can be sent back to Server)
            String[] reservationStrings = allReservations.substring(25).split("\\)");

            // New array which the user sees
            String[] displayReservations = new String[reservationStrings.length];
            for (int i = 0; i < reservationStrings.length; i++) {
                reservationStrings[i] = reservationStrings[i].substring(reservationStrings[i].indexOf(" ") + 1,
                        reservationStrings[i].indexOf(','));
                displayReservations[i] = ((i + 1) + ".) " + reservationStrings[i]);

            }

            String userChoice = (String) JOptionPane.showInputDialog(null, "Select a reservation to cancel.",
                    "Reservations", JOptionPane.PLAIN_MESSAGE, null, displayReservations, null);
            if (userChoice == null) {
                return;
            }
            String[] temp = userChoice.split("\\.\\) ");
            int num = Integer.parseInt(temp[0]);



            //Check if reservationNum is a valid number

            // Send reservationID to server to be removed
            writer.println("removeReservation");
            writer.println(reservationStrings[num - 1]);
            writer.flush();

            reader.readLine();

            JOptionPane.showMessageDialog(null, "Reservation cancelled.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void viewReservations(Socket socket) {
        try {
            // Send username to Server so it can display appropriate reservations
            writer.println("viewReservations");
            writer.println(currentUser.getUsername());
            writer.flush();

            String allReservations = reader.readLine();

            if (allReservations.equals("No reservations found")) {
                JOptionPane.showMessageDialog(null, "No reservations found.");
                return;
            }

            // Format current reservations so it's just their ID's (so they can be sent back to Server)
            String[] reservationStrings = allReservations.substring(25).split("\\)");

            // New array which the user sees
            String[] displayReservations = new String[reservationStrings.length];
            for (int i = 0; i < reservationStrings.length; i++) {
                reservationStrings[i] = reservationStrings[i].substring(reservationStrings[i].indexOf(" ") + 1,
                        reservationStrings[i].indexOf(','));
                displayReservations[i] = ((i + 1) + ".) " + reservationStrings[i]);
            }

            String userChoice = (String) JOptionPane.showInputDialog(null, "Current Reservations",
                    "Reservations", JOptionPane.PLAIN_MESSAGE, null, displayReservations, null);
            if (userChoice == null) {
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteAccount(Socket socket) {
        // Confirm w/ user
        int option = JOptionPane.showConfirmDialog(null, "Confirm account deletion?", "Account deletion", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.NO_OPTION) {
            return;
        }

        try {
            // Send correct user to be deleted to Server
            writer.println("deleteAccount");
            writer.println(currentUser.getUsername());
            writer.flush();

            String result = reader.readLine();
            currentUser = null;

            JOptionPane.showMessageDialog(null, result);
            loggedIn = false;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Network error occurred.");
        }
    }

    // Main method
    public static void main (String[] args) {
        try {
            // Network setup
            Socket socket = new Socket("localhost", 1234);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            // Starter code to repeat showing a menu based on whether the user is logged in
            signedOutMenu(socket);


        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Couldn't connect to the server!","Connection Error" ,
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
