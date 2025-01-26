package stuptut.mirameble.app;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import stuptut.mirameble.controller.Controller;
import javafx.stage.WindowEvent;
import stuptut.mirameble.service.DatabaseService;

import java.io.IOException;
import java.sql.Connection;


public class MainApp extends Application
{
    private Stage primaryStage;

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

            //Connection connection = DatabaseService.getConnection("auth", "password");
            Controller controller = loader.getController();
            //controller.setConnection(connection);
            controller.setMainApp(this);

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
    Stage queryStage;
    public Boolean queryRunning;

    public void setupUser(String accessLevel)
    {
        String user = "Crook"; //TODO set default for basic employee access
        String password = "password";

        if (accessLevel.equals("MafiaBoss"))
        {
            user = "MafiaBoss";
            password = "bossPassword";
        }

        //userConnection = DatabaseService.getConnection(user, password);

        queryStage = new Stage();
        queryRunning = false;

        queryStage.setOnCloseRequest((WindowEvent event) -> {
            synchronized (queryRunning)
            {
                if (queryRunning)
                {
                    event.consume(); // Cancel the close event

                    // Show an alert to inform the user
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText("Operation in Progress");
                    alert.setContentText("Please wait until the background operation finishes.");
                    alert.showAndWait();
                }
            }
        });
    }

    public void launchQueryWindow(String view) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/stuptut/mirameble/view/" + view + ".fxml"));
        Scene scene = new Scene(loader.load());

        Controller controller = loader.getController();
        controller.setMainApp(this);
        controller.setConnection(userConnection);

        queryStage.setScene(scene);
        queryStage.setTitle(view);
        queryStage.show();
    }
}
