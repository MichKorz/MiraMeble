package stuptut.mirameble.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InventoryController extends Controller {

    @FXML
    private Button addButton;

    @FXML
    private TextField IDField;
    @FXML
    private TextField insertCountField;
    @FXML
    private Button insertButton;

    @FXML
    private TextArea infoArea;

    @FXML
    private TextField manufacturerField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField countField;

    @FXML
    private GridPane tableInventory;

    @FXML
    private GridPane tableProducts;

    ResultSetToGridPane setGridPaneInventory;
    ResultSetToGridPane setGridPaneProducts;

    @Override
    public void Initialize(String accessLevel)
    {
        refresh();
        setGridPaneInventory = new ResultSetToGridPane("ID name manufacturer count", tableInventory, "SELECT ID, name, manufacturer, count FROM inventory JOIN products ON ID_product = ID", stage, connection);
        setGridPaneProducts= new ResultSetToGridPane("ID name manufacturer", tableProducts, "SELECT * FROM products", stage, connection);
    }

    @FXML
    void insertInventory()
    {
        Platform.runLater(() -> stage.sizeToScene());
        stage.sizeToScene();
        new Thread(() ->
        {
            if (mainApp.stageLocks.get(stage).tryLock())
            {
                setFlags(true); // Flag for letting the query do its job in peace

                String ID = IDField.getText();
                String countText = insertCountField.getText();
                int count, id;

                try
                {
                    id = Integer.parseInt(ID);
                    count = Integer.parseInt(countText);
                }
                catch (NumberFormatException e)
                {
                    Platform.runLater(() -> infoArea.setText("Incorrect input"));
                    setFlags(false);
                    mainApp.stageLocks.get(stage).unlock();
                    return;
                }

                String query = "CALL insert_inventory(?, ?)";

                try (PreparedStatement stmt = connection.prepareStatement(query))
                {
                    stmt.setInt(1, id); // Set the name parameter
                    stmt.setInt(2, count); // Set the name parameter
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
    void addInventory()
    {
        Platform.runLater(() -> stage.sizeToScene());
        stage.sizeToScene();
        new Thread(() ->
        {
            if (mainApp.stageLocks.get(stage).tryLock())
            {
                setFlags(true); // Flag for letting the query do its job in peace

                String name = nameField.getText();
                String manufacturer = manufacturerField.getText();
                String countText = countField.getText();
                int count;

                try
                {
                    count = Integer.parseInt(countText);
                }
                catch (NumberFormatException e)
                {
                    Platform.runLater(() -> infoArea.setText("Incorrect input " + countText + " for field count"));
                    setFlags(false);
                    mainApp.stageLocks.get(stage).unlock();
                    return;
                }

                String query = "CALL add_inventory(?, ?, ?)";

                try (PreparedStatement stmt = connection.prepareStatement(query))
                {
                    stmt.setString(1, name); // Set the name parameter
                    stmt.setString(2, manufacturer); // Set the manufacturer parameter
                    stmt.setInt(3, count); // Set the name parameter
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

    @Override
    public void refresh()
    {
        if (mainApp.stageLocks.get(stage).tryLock())
        {
            refreshTables();
            mainApp.stageLocks.get(stage).unlock();
        }
    }

    private void refreshTables()
    {
        new Thread(() ->
        {
            setFlags(true); // Flag for letting the query do its job in peace

            Platform.runLater(() -> {
                try
                {
                    setGridPaneInventory.populateGridPane();
                    setGridPaneProducts.populateGridPane();
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
