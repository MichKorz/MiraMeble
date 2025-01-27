package stuptut.mirameble.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProductsController extends Controller
{
    @FXML
    private TextField IDField;
    @FXML
    private TextField newPriceField;

    @FXML
    private TextField deleteIDField;

    @FXML
    private TextField nameField;
    @FXML
    private TextField manufacturerField;
    @FXML
    private TextField priceField;

    @FXML
    private TextArea infoArea;

    @FXML
    private GridPane tableProducts;

    ResultSetToGridPane setGridPaneProducts;


    @Override
    public void Initialize(String accessLevel)
    {
        refresh();
        setGridPaneProducts = new ResultSetToGridPane("ID name manufacturer price", tableProducts, "SELECT ID, name, manufacturer, price FROM products JOIN prices ON ID=ID_product", stage, connection);
    }

    @FXML
    private void addProduct()
    {
        new Thread(() ->
        {
            if (mainApp.stageLocks.get(stage).tryLock())
            {
                setFlags(true); // Flag for letting the query do its job in peace

                String name = nameField.getText();
                String manufacturer = manufacturerField.getText();
                String priceString = priceField.getText();
                int price;

                try {
                    price = Integer.parseInt(priceString);
                }
                catch (NumberFormatException e)
                {
                    Platform.runLater(() -> infoArea.setText("Incorrect input for price" + priceString));
                    setFlags(false);
                    mainApp.stageLocks.get(stage).unlock();
                    return;
                }

                String query = "CALL add_product(?,?,?)";

                try (PreparedStatement stmt = connection.prepareStatement(query))
                {
                    stmt.setString(1, name);
                    stmt.setString(2, manufacturer);
                    stmt.setInt(3, price);
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
    void changePrice()
    {
        new Thread(() ->
        {
            if (mainApp.stageLocks.get(stage).tryLock())
            {
                setFlags(true); // Flag for letting the query do its job in peace

                String ID = IDField.getText();
                String priceString = newPriceField.getText();
                int id, price;

                try {
                    price = Integer.parseInt(priceString);
                    id = Integer.parseInt(ID);
                }
                catch (NumberFormatException e)
                {
                    Platform.runLater(() -> infoArea.setText("Incorrect input for Change Price query"));
                    setFlags(false);
                    mainApp.stageLocks.get(stage).unlock();
                    return;
                }

                String query = "UPDATE prices SET price = ? WHERE ID_product = ?";

                try (PreparedStatement stmt = connection.prepareStatement(query))
                {
                    stmt.setInt(1, price);
                    stmt.setInt(2, id);
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
    void deleteProduct()
    {
        new Thread(() ->
        {
            if (mainApp.stageLocks.get(stage).tryLock())
            {
                setFlags(true); // Flag for letting the query do its job in peace

                String ID = deleteIDField.getText();
                int id;

                try {
                    id = Integer.parseInt(ID);
                }
                catch (NumberFormatException e)
                {
                    Platform.runLater(() -> infoArea.setText("Incorrect input for id" + ID));
                    setFlags(false);
                    mainApp.stageLocks.get(stage).unlock();
                    return;
                }

                String query = "DELETE FROM products WHERE ID = ?";

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
