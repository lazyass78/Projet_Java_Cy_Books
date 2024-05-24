package Controller;

import Utils.DatabaseUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.Node;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class CYBooksStatisticsController {

    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private Button returnHome;

    @FXML private AnchorPane mainContainer;


    public void initialize() {
        try {
            // Step 1: Connect to the database
            Connection connection = DatabaseUtil.getConnection();

            // Step 2: Create a statement
            Statement statement = connection.createStatement();

            // Step 3: Execute the query to get books borrowed in the last 30 days
            String query = "SELECT isbn, COUNT(*) as borrow_count FROM historic WHERE loan_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) GROUP BY isbn ORDER BY borrow_count DESC LIMIT 10";
            ResultSet resultSet = statement.executeQuery(query);

            // Step 4: Process the results and populate the bar chart
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            Map<String, String> colorMap = new HashMap<>();
            while (resultSet.next()) {
                String isbn = resultSet.getString("isbn");
                int borrowCount = resultSet.getInt("borrow_count");
                series.getData().add(new XYChart.Data<>(isbn, borrowCount));
                // Generate and store a unique color for each ISBN
                colorMap.put(isbn, generateRandomColor());
            }

            // Add the series to the bar chart
            barChart.getData().add(series);

            // Step 5: Close the connection
            connection.close();

            // Apply different colors to each bar
            applyBarColors(series, colorMap);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loadView(String fxmlFileName) {
        try {
            if (mainContainer == null) {
                System.err.println("Erreur : mainContainer n'a pas été correctement initialisé.");
                return;
            }

            // Charge le fichier FXML de la vue spécifiée
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFileName));
            Parent view = fxmlLoader.load();

            // Remplace le contenu actuel du conteneur principal par le contenu de la nouvelle vue
            mainContainer.getChildren().clear();
            mainContainer.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void applyBarColors(XYChart.Series<String, Number> series, Map<String, String> colorMap) {
        for (XYChart.Data<String, Number> data : series.getData()) {
            Node node = data.getNode();
            String color = colorMap.get(data.getXValue());
            node.setStyle("-fx-bar-fill: " + color + ";");
        }
    }

    private String generateRandomColor() {
        Color color = Color.color(Math.random(), Math.random(), Math.random());
        return String.format("#%02X%02X%02X",
                (int)(color.getRed() * 255),
                (int)(color.getGreen() * 255),
                (int)(color.getBlue() * 255));
    }

    @FXML public void returnHome() {
        loadView("CYBooks_Home.fxml");
    }
}








