package stuptut.mirameble.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import java.sql.SQLException;

public class EmployeesController extends Controller
{
    @FXML
    private TextArea infoArea;

    @FXML
    private GridPane tableEmployees;
    ResultSetToGridPane setGridPaneEmployees;


    @Override
    public void Initialize(String accessLevel)
    {
        refresh();
        setGridPaneEmployees = new ResultSetToGridPane("ID name surname level salary", tableEmployees,
                "SELECT ID, e.name, surname, level, salary FROM employees e JOIN levels l ON e.level = l.name", stage, connection);
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
                    setGridPaneEmployees.populateGridPane();
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
