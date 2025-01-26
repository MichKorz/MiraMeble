package stuptut.mirameble.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddCustomerController extends Controller
{
    @FXML
    private TextField emailField;

    @FXML
    private TextArea infoArea;

    @FXML
    private void addUser()
    {
        new Thread(() ->
        {
            setFlag(true); // Flag for letting the query do its job in peace

            String email = emailField.getText();
            String query = "CALL add_client(?)";

            try (PreparedStatement stmt = connection.prepareStatement(query))
            {
                stmt.setString(1, email); // Set the email parameter
                int rowsAffected = stmt.executeUpdate();

                Platform.runLater(() -> infoArea.setText(rowsAffected + " rows affected"));
            }
            catch (SQLException e)
            {
                Platform.runLater(() -> infoArea.setText(e.getMessage()));  //Info, ex. You dont have permissions!
            }
            finally
            {
                setFlag(false);
            }
        }).start();

    }
}
