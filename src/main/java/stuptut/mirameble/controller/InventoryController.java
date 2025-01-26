package stuptut.mirameble.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InventoryController extends Controller {

    @FXML
    private Button addButton;

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

    @Override
    public void Initialize(String accessLevel)
    {
        refreshTable("SELECT ID, name, manufacturer, count FROM inventory JOIN products ON ID_product = ID", tableInventory);
        refreshTable("SELECT * FROM products", tableProducts);
    }

    @FXML
    void addInventory()
    {
        new Thread(() ->
        {
            setFlag(true); // Flag for letting the query do its job in peace

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
                setFlag(false);
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
                refreshTable("SELECT ID, name, manufacturer, count FROM inventory JOIN products ON ID_product = ID", tableInventory);
                setFlag(false);
            }
        }).start();
    }

    private void refreshTable(String query, GridPane gridPane)
    {

        new Thread(() ->
        {
            setFlag(true); // Flag for letting the query do its job in peace

            try (PreparedStatement stmt = connection.prepareStatement(query))
            {
                ResultSet rs = stmt.executeQuery();
                Platform.runLater(() -> ResultSetToGridPane.populateGridPane(rs, gridPane));
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
