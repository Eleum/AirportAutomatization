import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO extends AbstractDAO {

    public AccountDAO(Connection connection) {
        super(connection);
    }

    public boolean create(Account account) {
        Statement sql;

        try {
            sql = connection.createStatement();

            String appendToSQL;

            appendToSQL = "(" + "'" + account.getLogin()+ "'" + "," + "'" + account.getPasswordHash() + "'" + "," +
                    "'" + account.getPasswordSalt() + "'" + "," + "'" + account.getType() + "'" + ");";

            sql.execute("INSERT Accounts VALUES " + appendToSQL);
        } catch (Exception e) {
            System.out.println("Account creation failed!");
            return false;
        }
        try {
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean delete(String id) {
        Statement sql;
        try {
            sql = connection.createStatement();
            if(sql.executeUpdate("DELETE FROM Accounts WHERE ID = '" + id + "'") != 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void edit(String id, String type) {
        Statement sql;
        try {
            sql = connection.createStatement();
            sql.executeUpdate("UPDATE Accounts SET type = '" + type + "' WHERE ID = '" + id + "'");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Account> findAll() {
        Statement sql;
        ResultSet resultSet;
        List<Account> users = new ArrayList<>();

        try {
            sql = connection.createStatement();
            resultSet = sql.executeQuery("SELECT ID, type FROM Accounts");


            while(resultSet.next()) {
                users.add(new Account(resultSet.getInt("ID"), resultSet.getString("type")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    public boolean find(Account account) {
        Statement sql = null;
        ResultSet resultSet = null;

        try {
            sql = connection.createStatement();

            resultSet = sql.executeQuery("SELECT * FROM Accounts");

            while(resultSet.next()) {
                Account compare = new Account();
                compare.setLogin(resultSet.getString("login"));

                //if acc with the same login is found
                if(account.getLogin().equals(compare.getLogin())) return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            resultSet.close();
            sql.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Account> find(String info, String fieldsInfo) {

        Statement sql;

        List<Account> accounts = new ArrayList<>();

        String[] findArray = info.split("\\|");
        String[] fields = fieldsInfo.split("\\|");

        String appendToSQL = "";

        for(int i = 0; i < fields.length; i++) {
            appendToSQL += fields[i] + " = " + "'" + findArray[i] + "'" + " AND ";
        }
        appendToSQL = appendToSQL.substring(0, appendToSQL.length()-4);

        try {
            sql = connection.createStatement();

            ResultSet resultSet = sql.executeQuery("SELECT * FROM Accounts WHERE " + appendToSQL);

            while(resultSet.next()) {
                Account acc = new Account();
                acc.setId(resultSet.getInt("id"));
                acc.setLogin(resultSet.getString("login"));
                acc.setPasswordHash(resultSet.getString("passwordHash"));
                acc.setPasswordSalt(resultSet.getString("passwordSalt"));
                acc.setType(resultSet.getString("type"));

                accounts.add(acc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accounts;
    }
}
