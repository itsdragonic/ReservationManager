import java.io.*;
import java.util.ArrayList;

/**
 * Reservation Manager Project
 *
 * This deals with the User
 * Usernames and passwords are stored in the "usernames.txt" file
 * Has methods for creating accounts, logging in, and deleting accounts

 * @author Maximus Puckett
 *
 * @version November 8, 2025
 *
 */

public class User implements InterfaceUser {
    private String username;
    private String password;
    private ArrayList<Reservation> reservations;
    private int reservationNumber;

    private final String usernameFilename = "usernames.txt";

    public User(String username, String password,  ArrayList<Reservation> reservations, int reservationNumber) {
        this.username = username;
        this.password = password;
        this.reservations = reservations;
        this.reservationNumber = reservationNumber;
    }

    public boolean createAccount(String username, String password) { // Creates account and adds it to the usernames.txt file and checks if username and password are valid

        try {
            BufferedReader br = new BufferedReader(new FileReader(usernameFilename));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || !line.contains(":")) {
                    continue;
                }

                String[] parts = line.split(": ");
                String existingUsername = parts[0];
                if (existingUsername.equals(username)) {
                    return false;
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        if (username.length() < 3 || username.length() > 16) {
            return false;
        } if (!username.matches("[a-zA-Z0-9]+")) { // Checks to make sure there are no special characters
            return false;
        }

        if (password.length() < 8 || password.length() > 20) {
            return false;
        } if (password.contains("\\") || (password.contains(":"))) {
            return false;
        }
        String data = (username + ": " + password);

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(usernameFilename, true));
            bw.newLine();
            bw.write(data);
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;

    }

    @Override
    public boolean login(String username, String password) { // Asks for username and password and checks if they're valid

        try {
            BufferedReader br = new BufferedReader(new FileReader(usernameFilename));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(": ");
                String existingUsername = parts[0];
                String existingPassword = parts[1];

                if (username.equals(existingUsername)) {
                    if (password.equals(existingPassword)) {
                        return true;
                    }
                }
            }
            return false;



        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean deleteAccount(String username) {
        ArrayList<String> lines = new ArrayList<>();
        boolean accountFound = false;

        try (BufferedReader br = new BufferedReader(new FileReader(usernameFilename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty()) {
                    lines.add("");
                    continue;
                }

                String[] parts = line.split(":", 2);
                String currentUsername = parts[0].trim();

                if (currentUsername.equals(username)) {
                    accountFound = true;
                    lines.add("");
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(usernameFilename))) {
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return accountFound;
    }

    public ArrayList<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(ArrayList<Reservation> reservations) {
        this.reservations = reservations;
    }

    public int getReservationNumber() {
        return reservationNumber;
    }
    public void setReservationNumber(int reservationNumber) {
        this.reservationNumber = reservationNumber;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
    }
    public void removeReservation(Reservation reservation) {
        reservations.remove(reservation);
    }

}