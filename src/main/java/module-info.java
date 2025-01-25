module stuptut.mirameble {
    requires javafx.controls;
    requires javafx.fxml;


    opens stuptut.mirameble.app to javafx.fxml;
    exports stuptut.mirameble.app;
}