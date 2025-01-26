package stuptut.mirameble.controller;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ResultSetToGridPane {

    public static void populateGridPane(ResultSet rs, GridPane gridPane) {
        try {
            // Clear any existing content from the GridPane
            gridPane.getChildren().clear();
            gridPane.setHgap(10);
            gridPane.setVgap(10);
            gridPane.setPadding(new Insets(10, 10, 10, 10));

            // Get metadata to retrieve column count and names
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Add column headers to the GridPane
            for (int col = 1; col <= columnCount; col++) {
                Label header = new Label(metaData.getColumnName(col));
                header.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                gridPane.add(header, col - 1, 0); // Column headers in the first row (index 0)
            }

            // Add rows of data
            int row = 1; // Start from row 1 for data, as row 0 is for headers
            while (rs.next()) {
                for (int col = 1; col <= columnCount; col++) {
                    // Get the data as a string
                    String cellData = rs.getString(col);
                    Label cell = new Label(cellData != null ? cellData : "NULL");
                    gridPane.add(cell, col - 1, row); // Add data to the GridPane
                }
                row++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

