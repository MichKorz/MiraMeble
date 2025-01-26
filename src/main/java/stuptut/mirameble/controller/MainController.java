package stuptut.mirameble.controller;

import javafx.fxml.FXML;

import java.io.IOException;

public class MainController extends Controller
{
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
}
