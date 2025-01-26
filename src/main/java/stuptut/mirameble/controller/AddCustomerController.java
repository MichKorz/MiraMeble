package stuptut.mirameble.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddCustomerController extends Controller
{
    @FXML
    private TextField emailField;

    @FXML
    private TextArea infoArea;

    @FXML
    private GridPane tableClients;

    ResultSetToGridPane setGridPaneClients;

    @Override
    public void Initialize(String accessLevel)
    {
        refresh();
        setGridPaneClients = new ResultSetToGridPane("ID email", tableClients, "SELECT * FROM clients", stage, connection);
    }

    @FXML
    private void addUser()
    {
        new Thread(() ->
        {
            if (mainApp.stageLocks.get(stage).tryLock())
            {
                setFlags(true); // Flag for letting the query do its job in peace

                String email = emailField.getText();

                if(!email.contains("@") || !email.contains("."))
                {
                    Platform.runLater(() -> infoArea.setText("Incorrect input " + email + " for field email"));
                    setFlags(false);
                    mainApp.stageLocks.get(stage).unlock();
                    return;
                }

                String query = "INSERT INTO clients (email) VALUES (?)";

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
                    mainApp.refreshWindows();
                    setFlags(false);
                    mainApp.stageLocks.get(stage).unlock();
                }
            }
        }).start();

    }

    @FXML
    public void refresh()
    {
        if (mainApp.stageLocks.get(stage).tryLock())
        {
            refreshTable();
            mainApp.stageLocks.get(stage).unlock();
        }
    }

    private void refreshTable()
    {
        new Thread(() ->
        {
            setFlags(true); // Flag for letting the query do its job in peace

            Platform.runLater(() -> {
                try
                {
                    setGridPaneClients.populateGridPane();
                }
                catch (SQLException e)
                {
                    Platform.runLater(() -> infoArea.setText(e.getMessage()));  //Info, ex. You dont have permissions!
                }
                finally
                {
                    setFlags(false);
                }
            });
        }).start();
    }
}
