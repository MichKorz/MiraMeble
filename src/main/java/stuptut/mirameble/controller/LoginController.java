package stuptut.mirameble.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import stuptut.mirameble.util.HashUtil;

import java.sql.*;

public class LoginController extends Controller
{
    @FXML
    private Text errorText;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField usernameField;

    @FXML
    private void handleLogin() throws Exception
    {
        String username = usernameField.getText();
        String password = passwordField.getText();

        mainApp.set_id(Integer.parseInt(username));

        String hashedPassword = "";
        String salt = "";
        String accessLevel = "";

        if (username.isEmpty() || password.isEmpty())
        {
            errorText.setText("Username or Password is empty");
            return;
        }

        String query = "SELECT * FROM hash_passwords WHERE ID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query))
        {
            stmt.setInt(1, Integer.parseInt(username));
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
            {
                hashedPassword = rs.getString("hash");
                salt = rs.getString("salt");
                accessLevel = rs.getString("access_level");
            }
            else
            {
                errorText.setText("Username or Password is incorrect");
                return;
            }
        }
        catch (SQLException e)
        {
            throw new SQLException(e);
        }

        if (hashedPassword.equals(HashUtil.hashPassword(password, salt)))
        {
            mainApp.setupUser(accessLevel);
            mainApp.launchMainView();
        }
        else
        {
            errorText.setText("Username or Password is incorrect");
            return;
        }
    }
}
