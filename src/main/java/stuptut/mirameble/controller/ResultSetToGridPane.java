package stuptut.mirameble.controller;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.sql.*;

public class ResultSetToGridPane {

    private final GridPane gridPane;
    private final String baseQuery;
    private String query;
    private final Stage stage;
    private final Connection connection;

    public ResultSetToGridPane( String headers, GridPane gridPane, String query, Stage stage, Connection connection )
    {
        this.gridPane = gridPane;
        this.baseQuery = query;
        this.query = query;
        this.stage = stage;
        this.connection = connection;
        String[] columnNames = headers.split(" ");

        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        for (int col = 0; col < columnNames.length; col++)
        {
            String headerString = columnNames[col];
            Label header = new Label(headerString);
            header.setOnMouseClicked(event -> headerPressed(headerString));
            header.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            gridPane.add(header, col, 0); // Column headers in the first row (index 0)
        }
    }

    public void populateGridPane() throws SQLException
    {
        try {
            // Clear any existing content from the GridPane
            clearPane();

            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            // Get metadata to retrieve column count and names
            ResultSetMetaData metaData = rs.getMetaData();

            int columnCount = metaData.getColumnCount();

            // Add rows of data
            int row = 1; // Start from row 1 for data, as row 0 is for headers
            while (rs.next())
            {
                for (int col = 1; col <= columnCount; col++) {
                    // Get the data as a string
                    String cellData = rs.getString(col);
                    Label cell = new Label(cellData != null ? cellData : "NULL");
                    gridPane.add(cell, col - 1, row); // Add data to the GridPane
                }
                row++;
            }
            gridPane.getParent().requestLayout();
            stage.sizeToScene();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    private void headerPressed(String columnName)
    {
        String newQuery;
        if(columnName.equals("Employee")) newQuery = baseQuery + " ORDER BY e.name, e.surname";
        else if(columnName.equals("Product")) newQuery = baseQuery + " ORDER BY p.name";
        else newQuery = baseQuery + " ORDER BY " + columnName;
        if(!query.equals(newQuery))
        {
            query = newQuery;
            try {
                populateGridPane();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void clearPane()
    {
        var iterator = gridPane.getChildren().iterator();
        while (iterator.hasNext()) {
            var child = iterator.next();
            Integer childRow = GridPane.getRowIndex(child);
            if (childRow != null && childRow != 0) {
                iterator.remove();
            }
        }
    }
}

