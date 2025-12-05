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



    // This menu is what is always first shown
    public static void signedOutMenu(Socket socket) {
        JFrame frame1 = new JFrame("Signed Out");
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals("Login")) {
                    login(socket);
                    if (loggedIn) {
                        frame1.dispose();
                        mainMenu(socket);
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

            String[] guestArray = new String[12];
            for (int i = 0; i < 12; i++) {
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
            String[] availableTables = line.split(", ");

            String userChoice;
            if (availableTables.length == 1 && !availableTables[0].contains("Table")) {
                JOptionPane.showMessageDialog(null, "No available tables.");
                return;
            }
            userChoice = (String) JOptionPane.showInputDialog(null, "Select your table", "Table", JOptionPane.PLAIN_MESSAGE, null, availableTables, null);

            if (userChoice == null) {
                writer.println("cancel");
                writer.flush();
                return;
            }
            String[] tableNumber = userChoice.split(" ");

            int table = Integer.parseInt(tableNumber[1]);

            // "Credit card payment"
            String card = JOptionPane.showInputDialog(null, "Enter credit card number (16 digits)", "Card", JOptionPane.PLAIN_MESSAGE);
            if (card == null) {
                return;
            }
            if (!card.matches("\\d{16}")) {
                JOptionPane.showMessageDialog(null, "Invalid credit card. Reservation cancelled.");
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

        // Send correct user to be deleted to Server
        writer.println("deleteAccount");
        writer.println(currentUser.getUsername());
        writer.flush();

        currentUser = null;

        JOptionPane.showMessageDialog(null, "Account deleted");
        loggedIn = false;

    }

    // Main method
    public static void main (String[] args) {
        try {
            // Network setup
            Socket socket = new Socket("localhost", 1234);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            // Starter code to repeat showing a menu based on whether the user is logged in
            //System.out.println("Welcome to Reservation Manager!");
            //while (running) {
              //  if (currentUser == null) {
                 //   signedOutMenu(socket);
                //} else {
                  //  mainMenu(socket);
                //}
          //  }

            signedOutMenu(socket);


            //Close Client tools
            System.out.println("Thank you for using the Reservation Manager!");
            //writer.close();
            //reader.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Couldn't connect to the server!","Connection Error" ,
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
