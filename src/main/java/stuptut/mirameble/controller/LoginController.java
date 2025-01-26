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
        String hashedPassword = null;
        hashedPassword = "1234"; //TODO REMOOOVEEEEEEEEEEEEEEEEE
        String salt = null;
        salt = "POINTLESSSSSSSSSS";

        String accessLevel = "Crook";

        if (username.isEmpty() || password.isEmpty())
        {
            errorText.setText("Username or Password is empty");
            return;
        }

        String query = "SELECT * FROM login WHERE username = ?";

        /*try (PreparedStatement stmt = connection.prepareStatement(query))
        {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
            {
                hashedPassword = rs.getString("password");
                salt = rs.getString("salt");
            }
            else
            {
                errorText.setText("Username or Password is incorrect");
                return;
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }*/

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
