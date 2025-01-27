package stuptut.mirameble.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrdersController extends Controller
{
    @FXML
    private TextField clientField;
    @FXML
    private TextField productField;
    @FXML
    private TextField countField;

    @FXML
    private TextField deleteOrderField;

    @FXML
    private TextField orderField;

    @FXML
    private TextArea infoArea;

    @FXML
    private GridPane tableOrders;
    ResultSetToGridPane setGridPaneOrders;

    @FXML
    private GridPane tableClients;
    ResultSetToGridPane setGridPaneClients;

    @FXML
    private GridPane tableProducts;
    ResultSetToGridPane setGridPaneProducts;

    @Override
    public void Initialize(String accessLevel)
    {
        refresh();
        setGridPaneOrders = new ResultSetToGridPane("ID Client_Id Product_ID Count Date_of_Placing", tableOrders, "SELECT * FROM orders", stage, connection);
        setGridPaneProducts = new ResultSetToGridPane("ID Name Manufacturer Price", tableProducts, "SELECT ID, name, manufacturer, price FROM products JOIN prices ON ID=ID_product", stage, connection);
        setGridPaneClients = new ResultSetToGridPane("ID email", tableClients, "SELECT * FROM clients", stage, connection);
    }

    @FXML
    private void addOrder()
    {
        new Thread(() ->
        {
            if (mainApp.stageLocks.get(stage).tryLock())
            {
                setFlags(true); // Flag for letting the query do its job in peace

                String client = clientField.getText();
                String product = productField.getText();
                String countText = countField.getText();
                int id_client, id_product, count;

                try
                {
                    id_client = Integer.parseInt(client);
                    id_product = Integer.parseInt(product);
                    count = Integer.parseInt(countText);
                }
                catch (NumberFormatException e)
                {
                    Platform.runLater(() -> infoArea.setText("Incorrect input for adding order"));
                    setFlags(false);
                    mainApp.stageLocks.get(stage).unlock();
                    return;
                }

                String query = "CALL add_order(?,?,?)";

                try (PreparedStatement stmt = connection.prepareStatement(query))
                {
                    stmt.setInt(1, id_client);
                    stmt.setInt(2, id_product);
                    stmt.setInt(3, count);
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
    void completeOrder()
    {
        new Thread(() ->
        {
            if (mainApp.stageLocks.get(stage).tryLock())
            {
                setFlags(true); // Flag for letting the query do its job in peace

                String order = orderField.getText();
                int id;

                try
                {
                    id = Integer.parseInt(order);
                }
                catch (NumberFormatException e)
                {
                    Platform.runLater(() -> infoArea.setText("Incorrect input for completing order"));
                    setFlags(false);
                    mainApp.stageLocks.get(stage).unlock();
                    return;
                }

                String query = "CALL complete_order(?, ?)";

                try (PreparedStatement stmt = connection.prepareStatement(query))
                {
                    stmt.setInt(1, id);
                    stmt.setInt(2, mainApp.get_id());
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
    void deleteOrder()
    {
        new Thread(() ->
        {
            if (mainApp.stageLocks.get(stage).tryLock())
            {
                setFlags(true); // Flag for letting the query do its job in peace

                String order = deleteOrderField.getText();
                int id;

                try
                {
                    id = Integer.parseInt(order);
                }
                catch (NumberFormatException e)
                {
                    Platform.runLater(() -> infoArea.setText("Incorrect input for completing order"));
                    setFlags(false);
                    mainApp.stageLocks.get(stage).unlock();
                    return;
                }

                String query = "DELETE FROM orders WHERE ID = ?";

                try (PreparedStatement stmt = connection.prepareStatement(query))
                {
                    stmt.setInt(1, id);
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
                    setGridPaneOrders.populateGridPane();
                    setGridPaneProducts.populateGridPane();
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
