package stuptut.mirameble.controller;

import javafx.stage.Stage;
import stuptut.mirameble.app.MainApp;

import java.sql.Connection;

public class Controller
{
    Connection connection;
    MainApp mainApp;
    Stage stage;

    public void setConnection(Connection connection)
    {
        this.connection = connection;
    }

    public void setMainApp(MainApp mainApp)
    {
        this.mainApp = mainApp;
    }

    public void setStage(Stage stage)
    {
        this.stage = stage;
    }

    void setFlags(boolean value)
    {
        synchronized (mainApp.queryRunning) // TODO fix that perhaps???
        {
            mainApp.queryRunning = value;
        }
    }

    public void Initialize(String accessLevel){}

    public void refresh(){}
}
