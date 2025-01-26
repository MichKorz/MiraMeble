package stuptut.mirameble.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseService
{
    private static final String URL = "jdbc:mariadb://localhost:3306/warehouse";

    public static Connection getConnection(String user, String password)
    {
        try
        {
            return DriverManager.getConnection(URL, user, password);
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error connecting to the database", e);
        }
    }
}