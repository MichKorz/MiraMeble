module stuptut.mirameble
{
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens stuptut.mirameble.app to javafx.fxml;
    exports stuptut.mirameble.app;

    opens stuptut.mirameble.controller to javafx.fxml;
    exports stuptut.mirameble.controller;
}