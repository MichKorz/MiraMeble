package stuptut.mirameble.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import java.sql.SQLException;

public class OrdersArchiveController extends Controller
{
    @FXML
    private TextArea infoArea;

    @FXML
    private GridPane tableOrders;
    ResultSetToGridPane setGridPaneOrders;

    @FXML
    private GridPane tableArchives;
    ResultSetToGridPane setGridPaneOrdersArchive;


    @Override
    public void Initialize(String accessLevel)
    {
        refresh();
        setGridPaneOrders = new ResultSetToGridPane("ID Client_ID Product_ID Product Count Date_of_Placing", tableOrders,
                "SELECT o.ID, o.ID_client, p.ID, p.name, o.count, o.date_of_placing  FROM orders o JOIN products p ON o.ID_product=p.ID", stage, connection);
        setGridPaneOrdersArchive = new ResultSetToGridPane("ID Client_ID Product_ID Product Employee Count Date_of_Placing Date_of_Completion", tableArchives,
                "SELECT o.ID, o.ID_client, p.ID, p.name, Concat(e.name, ' ', e.surname), o.count, o.date_of_placing, o.date_of_completion  FROM orders_archive o JOIN products p ON o.ID_product=p.ID JOIN employees e ON o.ID_employee=e.ID", stage, connection);
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
                    setGridPaneOrdersArchive.populateGridPane();
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
