package stuptut.mirameble.controller;

import stuptut.mirameble.app.MainApp;

import java.sql.Connection;

public class Controller
{
    Connection connection;
    MainApp mainApp;

    public void setConnection(Connection connection)
    {
        this.connection = connection;
    }

    public void setMainApp(MainApp mainApp)
    {
        this.mainApp = mainApp;
    }

    void setFlag(boolean value)
    {
        synchronized (mainApp.queryRunning) // TODO fix that perhaps???
        {
            mainApp.queryRunning = value;
        }
    }

    public void Initialize(String accessLevel){};
}
