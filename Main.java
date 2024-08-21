import java.sql.*;
import java.util.Scanner;

public class Main {

    private static final String url = "jdbc:mysql://localhost:3306/bankingSchema";
    private static final String userName = "root";
    private static final String password = "H1m@nsh1";
    private static final Scanner scanner = new Scanner(System.in);

    // REGISTER NEW USER
    public static void register(Connection connection) {
        System.out.print("ENTER YOUR FIRST NAME: "); // name
        String fullName = scanner.next();
        System.out.print("ENTER YOUR EMAIL-ID: "); // email
        String email = scanner.next();
        System.out.print("ENTER YOUR PASSWORD: "); // passwod
        String password = scanner.next();

        if (userExist(connection, email)) {
            System.out.println();
            System.out.print("!!! YOU ARE ALREADY REGISTERED !!!");
            System.out.println();
            return;
        }

        String query = "INSERT INTO register(fullName, email, password) VALUES (?,?,?)"; // write query
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query); // create prepared statment
            preparedStatement.setString(1, fullName);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);
            int affecteRows = preparedStatement.executeUpdate(); // execute prepared statement
            System.out.println();

            if (affecteRows > 0) {
                System.out.println("*** HURRAY, " + fullName + ", now YOU are REGISTERED in BANSAL BANK ***"); // successful
                                                                                                               // message
            } else {
                System.out.println("!!! SORRY, " + fullName + " your REGISTRATION gone FAILED !!!"); // failed
            }
            System.out.println();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    // LOGIN EXISTING USER
    public static String login(Connection connection) {
        System.out.print("ENTER YOUR EMAIL-ID: ");
        String email = scanner.next();
        System.out.print("Enter YOUR PASSWORD: ");
        String password = scanner.next();
        String query = "SELECT * FROM register WHERE email = ? AND password = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
                return email;
            else
                return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    // OPEN NEW ACCOUNT
    public static void newAccount(Connection connection) {
        System.out.println("--- PLEASE ENTER YOUR BASIC DETAILS ---");
        System.out.print("YOUR FIRST NAME: ");
        String fullName = scanner.next();
        System.out.print("YOUR AGE: ");
        int age = scanner.nextInt();
        System.out.print("YOUR PROFESSION: ");
        String profession = scanner.next();
        System.out.print("YOUR EMAIL-ID(registered): ");
        String email = scanner.next();
        System.out.print("INITIAL FUNDING(>=10000): ");
        double initialFund = scanner.nextDouble();
        System.out.print("SET PIN(4-digits): ");
        int pin = scanner.nextInt();

        long accNo = generateAccNo(connection); // GENERATE ACCOUNT NUMBER

        String query = "INSERT INTO USER (accNo, fullname, age, profession, email, pin, balance) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, accNo);
            preparedStatement.setString(2, fullName);
            preparedStatement.setInt(3, age);
            preparedStatement.setString(4, profession);
            preparedStatement.setString(5, email);
            preparedStatement.setInt(6, pin);
            preparedStatement.setDouble(7, initialFund);
            int affecteRows = preparedStatement.executeUpdate();
            System.out.println();
            if (affecteRows > 0) {
                System.out.println("*** CONGRATULATIONS, YOUR ACCOUNT HAS BEEN OPENED IN BANSAL BANK ***");
                System.out.println("YOUR ACCOUNT NUMBER IS: " + accNo);
            } else {
                System.out.println("!!! SORRY, SOMETHING WENT WRONG WHILE OPENING YOUR ACCOUNT !!!");
            }
            System.out.println();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // GENERATE ACCOUNT NUMBER
    public static long generateAccNo(Connection connection) {
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT accNo FROM user ORDER BY accNo DESC LIMIT 1";
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                long lastAccNo = resultSet.getLong("accNo");
                return lastAccNo + 1;
            } else {
                return 11001100;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return 11001100;
    }

    // GET ACCOUNT NUMBER
    public static long getAccNo(Connection connection, String email) {
        String query = "SELECT accNo FROM user WHERE email = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getLong("accNo");
            } else {
                System.out.println("!!! ERROR WHILE FETCHING ACCOUNT NUMBER !!!");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return 1;
    }

    // OPERATION ON EXISTING USER
    public static void operation(Connection connection, int choice, String email) {
        System.out.print("ENTER PIN: ");
        int pin = scanner.nextInt();

        // LOGOUT
        if (choice == 5) {
            System.out.println();
            System.out.println("*** HAPPY BANKING ***");
            System.out.println();
            return;
        }

        // CHECK BALANCE
        else if (choice == 1) {

            long accNo = getAccNo(connection, email);
            System.out.println("YOUR AVAILABLE BALANCE: " + checkBalance(connection, accNo, pin));
        }

        // DEBIT MONEY
        else if (choice == 2) {
            System.out.print("ENTER AMOUNT TO BE WITHDRAW: Rs.");
            double amount = scanner.nextDouble();

            long accNo = getAccNo(connection, email);

            debit(connection, accNo, amount, pin);
        }

        // CREDIT MONEY
        else if (choice == 3) {
            System.out.print("ENTER AMOUNT TO BE ADDED: Rs.");
            double amount = scanner.nextDouble();

            long accNo = getAccNo(connection, email);

            credit(connection, accNo, amount);
        }

        // TRANSFER MONEY
        else if (choice == 4) {
            long accNo = getAccNo(connection, email);
            transfer(connection, accNo);
        }

        System.out.println();
        System.out.print("RE-ENTER CHOICE: ");
        int newChoice = scanner.nextInt();
        System.out.println();
        operation(connection, newChoice, email);
    }

    // CHECK PIN
    public static boolean checkPin(Connection connection, int pin, long accNo) {
        String query = "SELECT * FROM user WHERE pin = ? AND accNo = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, pin);
            preparedStatement.setLong(2, accNo);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    // CHECK BALANCE
    public static double checkBalance(Connection connection, long accNo, int pin) {

        if (checkPin(connection, pin, accNo)) {
            String query = "SELECT balance FROM user WHERE accNo = ?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setLong(1, accNo);
                ResultSet resultSet = preparedStatement.executeQuery();
                System.out.println();
                if (resultSet.next()) {
                    double currBalance = resultSet.getDouble("balance");
                    return currBalance;
                } else {
                    System.out.println("!!! ERROR !!!");
                }
                System.out.println();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("!!! WRONG PIN !!!");
        }
        return 0;
    }
    // DEBIT MONEY
    public static void debit(Connection connection, long accNo, double amount, int pin) {
        try {
            connection.setAutoCommit(false);
            if (checkPin(connection, pin, accNo)) {
                String query = "SELECT * FROM user WHERE accNo = ? AND pin = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setLong(1, accNo);
                preparedStatement.setInt(2, pin);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    double currBalance = resultSet.getDouble("balance");
                    if (currBalance >= amount) {
                        String debitQuery = "UPDATE user SET balance = balance - ? WHERE accNo = ? AND pin = ?";
                        PreparedStatement preparedStatement1 = connection.prepareStatement(debitQuery);
                        preparedStatement1.setDouble(1, amount);
                        preparedStatement1.setLong(2, accNo);
                        preparedStatement1.setInt(3, pin);
                        int affecteRows = preparedStatement1.executeUpdate();
                        if (affecteRows > 0) {
                            System.out.println("*** Rs." + amount + " DEBITED SUCCESSFULLY ***");
                            connection.commit();
                            connection.setAutoCommit(true);
                        } else {
                            System.out.println("!!! TRANSACTION FAILED !!!");
                            connection.rollback();
                            connection.setAutoCommit(true);
                        }
                    } else {
                        System.out.println("!!! INSUFFICIENT BALANCE !!!");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // CREDIT MONEY
    public static void credit(Connection connection, long accNo, double amount) {
        try {
            String query = "UPDATE user SET balance = balance + ? WHERE accNo = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setDouble(1, amount);
            preparedStatement.setLong(2, accNo);
            int affecteRows = preparedStatement.executeUpdate();
            if (affecteRows > 0) {
                System.out.println("*** YOUR BALANCE UPDATED SUCCESSFULLY ***");
            } else {
                System.out.println("!!! ERROR !!!");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // TRANSFER MONEY
    public static void transfer(Connection connection, long accNo) {
        System.out.print("ENTER PAYEE ACCOUNT NUMBER: ");
        long payeeAccNo = scanner.nextLong();
        String query = "SELECT * FROM user WHERE accNo = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, payeeAccNo);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String email = resultSet.getString("email");
                if (userExist(connection, email)) {
                    connection.setAutoCommit(false);
                    System.out.print("ENTER AMOUNT TO BE TRANSFER: ");
                    double amount = scanner.nextDouble();
                    System.out.print("RE-ENTER PIN TO CONFIRM: ");
                    int pin = scanner.nextInt();
                    double available = checkBalance(connection, accNo, pin);
                    if (available > amount) {
                        // transfer
                        debit(connection, accNo, amount, pin);
                        credit(connection, payeeAccNo, amount);
                    } else {
                        System.out.println("!!! INSUFFICIENT BALANCE !!!");
                    }
                } else {
                    System.out.println("!!! ACCOUNT NUMBER DOESN'T EXIST !!!");
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // CHECK IS USER ALREADY EXISTS
    public static boolean userExist(Connection connection, String email) {
        String query = "SELECT * FROM register WHERE email = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    // MAIN FUNCTION
    public static void main(String[] args) {
        try {
            // establish connection
            Connection connection = DriverManager.getConnection(url, userName, password);

            // welcome
            System.out.println();
            System.out.println("!===== WELCOME TO THE BANSAL BANK =====!");
            System.out.println();
            System.out.println("PRESS '1' ==> LOGIN");
            System.out.println("PRESS '2' ==> REGISTER");
            System.out.println("PRESS '3' ==> EXIT");
            System.out.print("ENTER CHOICE: ");
            int choice = scanner.nextInt();
            System.out.println();

            // EXIT
            if (choice == 3) {
                System.out.println();
                System.out.println("*** COME BACK SOON ***");
                System.out.println();
            }

            // REGISTER
            else if (choice == 2) {
                register(connection);
            }

            // login
            else if (choice == 1) {
                String email = login(connection);
                if (email != null) { // login function
                    System.out.println();
                    System.out.println("*** YOU ARE SUCCESSFULLY LOGGED IN ***");
                    System.out.println();
                    System.out.println("PRESS '1' ==> OPEN NEW ACCOUNT");
                    System.out.println("PRESS '2' ==> ACCESS MY ACCOUNT");
                    System.out.println("PRESS '3' ==> EXIT");
                    System.out.print("ENTER CHOICE: ");
                    int loginChoice = scanner.nextInt();
                    System.out.println();

                    // EXIT
                    if (loginChoice == 3) {
                        System.out.println();
                        System.out.println("*** COME BACK SOON ***");
                        System.out.println();
                        return;
                    }

                    // already have an account
                    else if (loginChoice == 2) {
                        System.out.println();
                        System.out.println("*** WELCOME BACK ***");
                        System.out.println();
                        System.out.println("PRESS '1' ==> CHECK BALANCE");
                        System.out.println("PRESS '2' ==> DEBIT MONEY");
                        System.out.println("PRESS '3' ==> CREDIT MONEY");
                        System.out.println("PRESS '4' ==> TRANSFER MONEY");
                        System.out.println("PRESS '5' ==> LOGOUT");
                        System.out.print("ENTER CHOICE: ");
                        int operationChoice = scanner.nextInt();
                        System.out.println();
                        operation(connection, operationChoice, email);
                    }

                    // open new account
                    else if (loginChoice == 1) {
                        System.out.println();
                        System.out.println("*** THANKS for CHOOSING US");
                        System.out.println();
                        newAccount(connection);
                    }

                    else {
                        System.out.println("!!! INCORRECT ENTRY !!!");
                    }

                } else {
                    System.out.println("!!! LOGIN FAILED !!!");
                }

            } else {
                System.out.println("!!! INCORRECT ENTRY !!!");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}

//  to run ===> write this command ===> java -cp ./connector.jar Main.java