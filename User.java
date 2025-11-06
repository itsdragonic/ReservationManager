import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class User implements InterfaceUser {
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
    Scanner scanner = new Scanner(System.in); // I implemented the scanner to make testing easier, feel free to remove

    public boolean createAccount(String username, String password) { // Creates account and adds it to the Usernames.txt file and checks if username and password are valid
        System.out.println("Create your username: ");
        username = scanner.nextLine();

        try {
            BufferedReader br = new BufferedReader(new FileReader("Usernames.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(": ");
                String existingUsername = parts[0];
                if (existingUsername.equals(username)) {
                    System.out.println("Username already exists!");
                    return false;
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        if (username.length() < 3 || username.length() > 16) {
            System.out.println("Invalid username! Username should be between 3 and 16 characters.");
            return false;
        } if (!username.matches("[a-zA-Z0-9]+")) { // Checks to make sure there are no special characters
            System.out.println("Invalid username! Username should only contain letters and numbers.");
            return false;
        }
        System.out.println("Create your password: ");
        password = scanner.nextLine();

        if (password.length() < 8 || password.length() > 20) {
            System.out.println("Invalid password! Password should be between 8 and 20 characters.");
            return false;
        } if (password.contains("\\") || (password.contains(":"))) {
            System.out.println("Invalid password! Illegal characters are not allowed.");
            return false;
        }
        String data = (username + ": " + password);

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("Usernames.txt", true));
            bw.write(data);
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;

    }

    @Override
    public boolean login(String username, String password) { // Asks for username and password and checks if they're valid
        System.out.println("Enter your username: ");
        username = scanner.nextLine();

        try {
            BufferedReader br = new BufferedReader(new FileReader("Usernames.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(": ");
                String existingUsername = parts[0];
                String existingPassword = parts[1];

                if (username.equals(existingUsername)) {
                    System.out.println("Enter your password: ");
                    password = scanner.nextLine();
                    if (password.equals(existingPassword)) {
                        System.out.println("Logged in successfully!");
                        return true;
                    } else {
                        System.out.println("Incorrect password!");
                    }
                }
            }
            System.out.println("Couldn't find username!");
            return false;



        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean deleteAccount(String username) { // removes the input username and password line from the Usernames.txt file

        ArrayList<String> lines = new ArrayList<>();
        boolean accountExists = false;
        System.out.println("Enter your username: "); // can be removed added for testing
        username = scanner.nextLine(); // can be removed added for testing

        try {
            BufferedReader br = new BufferedReader(new FileReader("Usernames.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(username)) {
                    accountExists = true;
                } else {
                    lines.add(line);
                }
            }
            br.close();

            BufferedWriter bw = new BufferedWriter(new FileWriter("Usernames.txt"));

            for (int i = 0; i < lines.size(); i++) {
                bw.write(lines.get(i));
                bw.newLine();
            }
            bw.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return false;
    }
    public static void main(String[] args) { // Added to test methods
        User user = new User("", "");
        boolean result = user.createAccount("", ""); // change methods to test
        System.out.println("Account created result: " + result);
    }
}
