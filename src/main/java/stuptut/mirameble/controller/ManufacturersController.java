package stuptut.mirameble.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ManufacturersController extends Controller
{
    @FXML
    private TextField countryField;
    @FXML
    private TextField nameField;

    @FXML
    private TextField deleteNameField;

    @FXML
    private TextField newNameField;
    @FXML
    private TextField oldNameField;

    @FXML
    private TextArea infoArea;

    @FXML
    private GridPane tableManufacturers;
    ResultSetToGridPane setGridPaneClients;

    @Override
    public void Initialize(String accessLevel)
    {
        refresh();
        setGridPaneClients = new ResultSetToGridPane("Name Country", tableManufacturers, "SELECT * FROM manufacturers", stage, connection);
    }

    @FXML
    private void addManufacturer()
    {
        new Thread(() ->
        {
            if (mainApp.stageLocks.get(stage).tryLock())
            {
                setFlags(true); // Flag for letting the query do its job in peace

                String country = countryField.getText();
                String name = nameField.getText();

                String query = "INSERT INTO manufacturers (name, country) VALUES (?, ?)";

                try (PreparedStatement stmt = connection.prepareStatement(query))
                {
                    stmt.setString(1, name);
                    stmt.setString(2, country);
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
    void updateManufacturer()
    {
        new Thread(() ->
        {
            if (mainApp.stageLocks.get(stage).tryLock())
            {
                setFlags(true); // Flag for letting the query do its job in peace

                String oldName = oldNameField.getText();
                String newName = newNameField.getText();

                String query = "UPDATE manufacturers SET name = ? WHERE name = ?";

                try (PreparedStatement stmt = connection.prepareStatement(query))
                {
                    stmt.setString(1, newName);
                    stmt.setString(2, oldName);
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
    void deleteManufacturer()
    {
        new Thread(() ->
        {
            if (mainApp.stageLocks.get(stage).tryLock())
            {
                setFlags(true); // Flag for letting the query do its job in peace

                String name = deleteNameField.getText();

                String query = "DELETE FROM manufacturers WHERE name = ?";

                try (PreparedStatement stmt = connection.prepareStatement(query))
                {
                    stmt.setString(1, name);
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
