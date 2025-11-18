import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {
    private static Database db = new Database();
    private static ArrayList<User> users = new ArrayList<>();
    private static boolean running = true;
    /*
    Must contain Thread-safe measures
     */


    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
        try {
            ServerSocket serverSocket = new ServerSocket(1234);

            System.out.println("Waiting for the client to connect...");
            Socket socket = serverSocket.accept();
            System.out.println("Client connected!");

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());

            while (true) {
                String command = reader.readLine();
                System.out.println(command);

                switch (command) {
                    case "login": {
                        String username = reader.readLine();
                        String password = reader.readLine();

                        User testUser = new User("", "", new ArrayList<Reservation>(), 0);
                        boolean ok = testUser.login(username, password);
                        if (ok) {
                            writer.write("success");
                        } else {
                            writer.write("failure");
                        }
                        writer.println();
                        writer.flush();
                        break;
                    }

                    case "signUp": {
                        String username = reader.readLine();
                        String password = reader.readLine();

                        User newUser = new User(username, password, new ArrayList<Reservation>(), 0);
                        boolean success = newUser.createAccount(username, password);

                        if (success) {
                            writer.write("success");
                        } else {
                            writer.write("failure");
                        }
                        writer.println();
                        writer.flush();
                        break;
                    }

                    case "showTables": {
                        String date = reader.readLine();
                        int time = Integer.parseInt(reader.readLine());

                        String allTableNums = "";
                        for (Table t : db.getUnreservedTables(date, time)) {
                            allTableNums += "Table: " + t.getTableNum() + "\n";
                        }

                        writer.write(db.getUnreservedTables(date, time).toString());
                        writer.println();
                        writer.flush();
                        break;
                    }

                    default:
                        System.out.println("Something went wrong");
                        System.out.println(command);
                }
            }
            /*
            String message = reader.readLine();
            System.out.printf("Received from client:\n%s\n", message);


            String response = message.replaceAll(" ", ",");


            writer.write(response);
            writer.println();
            writer.flush(); // Ensure data is sent to the client.
            System.out.printf("Sent to client:\n%s\n", response);
             */
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
