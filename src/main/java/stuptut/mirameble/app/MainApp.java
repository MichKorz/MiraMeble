package stuptut.mirameble.app;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import stuptut.mirameble.controller.Controller;
import javafx.stage.WindowEvent;
import stuptut.mirameble.service.DatabaseService;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class MainApp extends Application
{
    private Stage primaryStage;
    private String accessLevel;

    public final HashMap<Stage, Controller> controllers = new HashMap<>();
    public final HashMap<Stage, Lock> stageLocks = new HashMap<>();

    @Override
    public void start(Stage primaryStage)
    {
        this.primaryStage = primaryStage;
        showLoginView();
    }

    public void showLoginView()
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/stuptut/mirameble/view/login.fxml"));
            Scene scene = new Scene(loader.load());

            Connection connection = DatabaseService.getConnection("auth", "auth");
            Controller controller = loader.getController();
            controller.setConnection(connection);
            controller.setMainApp(this);

            primaryStage.setOnCloseRequest((WindowEvent event) ->
            {
                if (!stageLocks.isEmpty())
                {
                    event.consume();

                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText("Query in Preparation");
                    alert.setContentText("Some of the Query Preparation windows are still open.");
                    alert.showAndWait();
                }
            });

            primaryStage.setScene(scene);
            primaryStage.setTitle("Login");
            primaryStage.show();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public void launchMainView()
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/stuptut/mirameble/view/main.fxml"));
            Scene scene = new Scene(loader.load());

            Controller controller = loader.getController();
            controller.setMainApp(this);

            primaryStage.setScene(scene);
            primaryStage.setTitle("Mira Meble");
            primaryStage.show();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    Connection userConnection;
    public Boolean queryRunning;


    public void setupUser(String accessLevel)
    {
        this.accessLevel = accessLevel;
        String user = "Crook";
        String password = "crook";

        if (accessLevel.equals("MafiaBoss"))
        {
            user = "MafiaBoss";
            password = "boss";
        }

        userConnection = DatabaseService.getConnection(user, password);
        queryRunning = false;
    }

    public void launchQueryWindow(String view) throws IOException
    {
        Stage stage = new Stage();
        stageLocks.put(stage, new ReentrantLock());

        stage.setOnCloseRequest((WindowEvent event) ->
        {
            if (!stageLocks.get(stage).tryLock())
            {
                event.consume();

                // Show an alert to inform the user
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText("Query in Progress");
                alert.setContentText("Please wait until query operation finishes.");
                alert.showAndWait();
            }
            else
            {
                controllers.remove(stage);
                stageLocks.remove(stage);
            }
        });

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/stuptut/mirameble/view/" + view + ".fxml"));
        Scene scene = new Scene(loader.load());

        Controller controller = loader.getController();
        controller.setMainApp(this);
        controller.setConnection(userConnection);
        controller.setStage(stage);
        controller.Initialize(accessLevel);

        controllers.put(stage, controller);

        stage.setScene(scene);
        stage.setTitle(view);
        stage.setResizable(true);
        stage.show();
    }

    public void refreshWindows()
    {
        for (Stage stage : controllers.keySet())
        {
            controllers.get(stage).refresh();
        }
    }
}
