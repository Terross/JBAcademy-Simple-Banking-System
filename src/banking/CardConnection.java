package banking;

import org.sqlite.SQLiteDataSource;

import java.sql.*;

public class CardConnection {
    private static String url = "jdbc:sqlite:";
    private static Connection connection;
    private CardConnection() {

    }

    public static CardConnection getCardConnection(String dbName) {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url + dbName);
        try {
            connection = dataSource.getConnection();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new CardConnection();
    }

    public void createTable() {
        try (Statement statement = connection.createStatement()) {

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS card(" +
                    "id INTEGER PRIMARY KEY, " +
                    "number TEXT," +
                    "pin TEXT," +
                    "balance INTEGER  DEFAULT 0)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertData(String cardNumber, String pin, int balance) {
        String insertStatement = "INSERT INTO card (number, pin, balance) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertStatement)) {
            preparedStatement.setString(1, cardNumber);
            preparedStatement.setString(2, pin);
            preparedStatement.setInt(3, balance);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addIncome(String cardNumber, int income) {
        String incomeStatement  = "UPDATE card SET balance = ? WHERE number = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(incomeStatement)) {
            preparedStatement.setInt(1, income);
            preparedStatement.setString(2, cardNumber);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean findCard(String cardNumber) {
        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery("SELECT * FROM card WHERE number = " +
                                                                cardNumber)) {
                if (resultSet.next() != false) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public void closeAccount(String cardNumber) {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM card WHERE number = " + cardNumber);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void transferMoney(String recipientCard, String senderCard, int change) {
        String recipientUpdate = "UPDATE card SET balance = balance + ? WHERE number = ?";
        String senderUpdate = "UPDATE card SET balance = balance - ? WHERE number = ?";
        try {
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
            }
            try (PreparedStatement recipientStatement = connection.prepareStatement(recipientUpdate);
                 PreparedStatement senderStatement = connection.prepareStatement(senderUpdate)) {
                recipientStatement.setInt(1, change);
                recipientStatement.setString(2, recipientCard);
                recipientStatement.executeUpdate();
                senderStatement.setInt(1, change);
                senderStatement.setString(2, senderCard);
                senderStatement.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                if (connection != null) {
                    try {
                        System.err.println("Transaction is being rolled back");
                        connection.rollback();
                    } catch (SQLException exc) {
                        exc.printStackTrace();
                    }
                }
            }
            if (!connection.getAutoCommit()) {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void showData() {
        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery("SELECT * FROM card")) {
                while (resultSet.next()) {
                    System.out.println(resultSet.getInt("id") + " | " +
                            resultSet.getString("number") + " | " +
                            resultSet.getString("pin") + " | " +
                            resultSet.getInt("balance"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean checkCustomer(String cardNumber, String pin) {
        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(
                    "SELECT * FROM card WHERE number = " + cardNumber)) {
                if (resultSet.next() != false) {
                    String truePin = resultSet.getString("pin");
                    if (truePin.equals(pin)) {
                        return true;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Account getAccount(String cardNumber) {
        Account account = null;
        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(
                    "SELECT * FROM card WHERE number = " + cardNumber)) {
                if (resultSet.next() != false) {
                    account = new Account();
                    account.setBalance(resultSet.getInt("balance"));
                    account.setCardNumber(cardNumber);
                    account.setPIN(resultSet.getString("pin"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return account;
    }

    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
