# ReservationManager (Option 1)
Team members: Maximus Puckett, Jeremiah Tomasello, Fredrick Zawislak

| Team Member       | Role                                         | Contributions                                                                  |
|-------------------|----------------------------------------------|--------------------------------------------------------------------------------|
| Jeremiah Tomasello    | Project lead, Develop methods for application | Implemented `Server`, `Client`, `Database`,`Reservation`,`ReservationManager`, and `Table` classes. Helped with README |
| Maximus Puckett   | Develop methods for user                     | Implemented `User` and `ReservationValidator` clases. Helped with README                                                      |
| Fredrick Zawislak | Interface designer, Test cases               | Implemented test cases, created the interfaces, and wrote README               |

## Description
This project allows multiple users create their own accounts for restaurant. With those accounts, the user can book reservations for a table in advance, setting the date and time, party size, etc. The reservation will then be stored in a database to be accessed and cross-referenced if another user is planning to make their own reservation.


## Phase 1

### Classes
_(in alphabetical order)_
1. Database.java
   1. By Jeremiah Tomasello
   2. This holds all the data for the project
      Reservation and table data are stored in files (reservations.txt and seating.txt)
      Has methods for creating ArrayLists for reservations and tables for use within code
      Any changes made to reservations should be done through ReservationManager.java

2. Reservation.java
   1. By Jeremiah Tomasello
   2. This class holds data for individual reservations placed by the user. It holds all the relevent table, date/time, and party size
      information. This class can be attatched to Table objects so their relative information can be gotten through various means,
      though all reservation information can be formatted into a string to be saved to reservations.txt.

3. ReservationManager.java
   1. By Jeremiah Tomasello
   2. This class allows a user to edit data stored in the database. This class allows the user to edit the information in database
      without directly interacting with the class.

4. Table.java
   1. By Jeremiah Tomasello
   2. This class stores information relevent to each table at the restaurant such as the number of seats at the table, and if
      applicable, a Reservation object containing its relevent information. All of this data is used by the Database class to be
      accessed if need be and to be changed accordingly.

5. User.java
   1. By Maximus Puckett
   2. This class allows a user to create an account and for their information to be stored. This class lets the user set up their
      account by making a username and password, vetting them for illegal characters or duplicate accounts already stored. After
      successfully setting up an account, the account information is then stored in the usernames.txt file to be accessed for later use
      in either account creation/deletion.

### Interfaces
| Interface                     | Purpose                                                                 |
|-------------------------------| ----------------------------------------------------------------------- |
| `InterfaceDatabase`           | Declares all database operations and persistence methods.               |
| `InterfaceReservation`        | Declares getter/setter structure for reservations.                      |
| `InterfaceReservationManager` | Declares creation/deletion methods for reservations.                    |
| `InterfaceTable`              | Declares table operations such as seat management.                      |
| `InterfaceUser`               | Declares all getter/setter, creation/deletion, and persistence methods. |

### How to Run
In any IDE (typically IntelliJ), you can run the file from Main.java and interact with the database through the console as of now.
You can also go through and run the test cases inside the test folder.
1. User can choose to either log in to an existing account, sign up to create a new account, or exit the program
   1. If the user tries to log in, they are prompted with entering their username and password, and if the account exists, are able to
      proceed with the program.
   2. If the user tries to create an account, they are prompted to enter their username and password, and if the account doesn't already
      exist, are able to proceed with the program.
   3. If the user tries to exit, the program ends.
2. The user can now choose between adding a reservation, removing a reservation, viewing all reservations, logging out, or deleting
   their account.
   1. Adding a reservation prompts the user to enter in the specific information about when and how many guests to reserve a table for,
      of which the reservation is then saved to a database.
   2. Removing a reservation prompts to enter the reservation (ID) they would like to cancel, and if successful, will remove the
      reservation from the database.
   3. Viewing all reservations lets the user view all current reservations currently stored in the database.
   4. If the user logs out, they are brought back to the first step.
   5. Deleting an account confirms with the user their intentions, and if so, deletes the user along with their reservations from the
      database

### Test Cases
For test cases, we have test files for each of the (current 6) classes. For our test cases, we sampled creating an editing a Database class. Firstly, we tested whether Table objects could be created with the specifications of there being two tables with four seats each. Then, to create a reservation, we created a test case for an example user with test values for their reservation to be created successfully. Additionally, we did tests on whether (after the reservation had been created) we could access and cancel a reservation. Finally, we also have test cases in the event that a reservation or any of its components can handle null or unexpected input values. This is just one example of the many test cases we have for each of the methods.

### Submission Details
| Deliverable          | Submitted By                                   |
| -------------------- | ---------------------------------------------- |
| **Code & README**    | Jeremiah Tomasello on Vocareum & Brightspace   |
| **Git Repository**   | Private GitHub Repository cloned into Vocareum |
| **Peer Evaluations** | All members completed by deadline              |

### Extra Notes
- Our code should be thread safe in its current form.
- The current GUI via console is subject to change.
- InterfaceManagement.java will be handled in phase two.


## Phase 2

### Classes
_(in alphabetical order)_
1. Client.java
   1. By Jeremiah Tomasello
   2. This class is the client side interface that the user interacts with to login or create their account where they can view, add, or
      remove their reservations.
      

2. Database.java
   1. By Jeremiah Tomasello
   2. This holds all the data for the project
      Reservation and table data are stored in files (reservations.txt and seating.txt)
      Has methods for creating ArrayLists for reservations and tables for use within code
      Any changes made to reservations should be done through ReservationManager.java

3. Management.java

4. Reservation.java
   1. By Jeremiah Tomasello
   2. This class holds data for individual reservations placed by the user. It holds all the relevent table, date/time, and party size
      information. This class can be attatched to Table objects so their relative information can be gotten through various means,
      though all reservation information can be formatted into a string to be saved to reservations.txt.

5. ReservationManager.java
   1. By Jeremiah Tomasello
   2. This class allows a user to edit data stored in the database. This class allows the user to edit the information in database
      without directly interacting with the class.

6. ReservationValidator.java
   1. By Maximus Puckett
   2. This class validates the reservation date and time by comparing user input to the current local time and checking if it's
      within the allotted time period.

7. Server.java
   1. By Jeremiah Tomasello
   2. This class implements the server side and waits for a connection on port 1234 and handles each client in its own thread.
      The server processes user commands, such as logging in, adding or removing reservations, etc in a thread-safe way

8. Table.java
   1. By Jeremiah Tomasello
   2. This class stores information relevent to each table at the restaurant such as the number of seats at the table, and if
      applicable, a Reservation object containing its relevent information. All of this data is used by the Database class to be
      accessed if need be and to be changed accordingly.

9. User.java
   1. By Maximus Puckett
   2. This class allows a user to create an account and for their information to be stored. This class lets the user set up their
      account by making a username and password, vetting them for illegal characters or duplicate accounts already stored. After
      successfully setting up an account, the account information is then stored in the usernames.txt file to be accessed for later use
      in either account creation/deletion.

### Interfaces
| Interface                       | Purpose                                                                 |
|---------------------------------|-------------------------------------------------------------------------|
| `InterfaceClient`               | Placeholder for client logic                                            |
| `InterfaceDatabase`             | Declares all database operations and persistence methods.               |
| `InterfaceManagement`           |                                                                         |
| `InterfaceReservation`          | Declares getter/setter structure for reservations.                      |
| `InterfaceReservationManager`   | Declares creation/deletion methods for reservations.                    |
| `InterfaceReservationValidator` | Placeholder for validation logic                                        |
| `InterfaceServer`               | Declares server operations such as start, end, and run                  |
| `InterfaceTable`                | Declares table operations such as seat management.                      |
| `InterfaceUser`                 | Declares all getter/setter, creation/deletion, and persistence methods. |

### How to Run
In any IDE (typically IntelliJ), you can run Server.java and in a separate terminal, run Client.java (can run multiple). You can interact with the reservation manager through the client with the console being the current I/O method.
You can also go through and run the test cases inside the test folder.
1. User can choose to either log in to an existing account, sign up to create a new account, or exit the program
   1. If the user tries to log in, they are prompted with entering their username and password, and if the account exists, are able to
      proceed with the program.
   2. If the user tries to create an account, they are prompted to enter their username and password, and if the account doesn't already
      exist, are able to proceed with the program.
   3. If the user tries to exit, the program ends.
2. The user can now choose between adding a reservation, removing a reservation, viewing all reservations, logging out, or deleting
   their account.
   1. Adding a reservation prompts the user to enter in the specific information about when and how many guests to reserve a table for,
      of which the reservation is then saved to a database.
   2. Removing a reservation prompts to enter the reservation (ID) they would like to cancel, and if successful, will remove the
      reservation from the database.
   3. Viewing all reservations lets the user view all current reservations currently stored in the database.
   4. If the user logs out, they are brought back to the first step.
   5. Deleting an account confirms with the user their intentions, and if so, deletes the user along with their reservations from the
      database

### Test Cases
For test cases, we have test files for each of the (current 6) classes. For our test cases, we sampled creating an editing a Database class. Firstly, we tested whether Table objects could be created with the specifications of there being two tables with four seats each. Then, to create a reservation, we created a test case for an example user with test values for their reservation to be created successfully. Additionally, we did tests on whether (after the reservation had been created) we could access and cancel a reservation. Finally, we also have test cases in the event that a reservation or any of its components can handle null or unexpected input values. This is just one example of the many test cases we have for each of the methods.

### Submission Details
| Deliverable          | Submitted By                                   |
| -------------------- | ---------------------------------------------- |
| **Code & README**    | Jeremiah Tomasello on Vocareum & Brightspace   |
| **Git Repository**   | Private GitHub Repository cloned into Vocareum |
| **Peer Evaluations** | All members completed by deadline              |

### Extra Notes
- Our code should be thread safe in its current form.
- The current GUI via console is subject to change.
