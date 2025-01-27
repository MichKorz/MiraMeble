package stuptut.mirameble.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;

public class MainController extends Controller
{
    @FXML
    private Button productsButton;

    @FXML
    private Button manufacturersButton;

    @Override
    public void Initialize(String accessLevel)
    {
        if(accessLevel.equals("Crook"))
        {
            productsButton.setDisable(true);
            manufacturersButton.setDisable(true);
        }
    }

    @FXML
    private void addCustomer() throws IOException
    {
        mainApp.launchQueryWindow("addCustomer");
    }

    @FXML
    private void inventory() throws IOException
    {
        mainApp.launchQueryWindow("inventory");
    }

    @FXML
    private void orders() throws IOException
    {

    }

    @FXML
    private void products() throws IOException
    {
        mainApp.launchQueryWindow("products");
    }

    @FXML
    private void manufacturers() throws IOException
    {

    }
}
