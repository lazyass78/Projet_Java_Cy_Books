package Controller;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
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
    public void initialize() {
        if (yAxis != null) {
            yAxis.setTickUnit(1.0);  // Example value, adjust based on expected data range
        } else {
            System.err.println("yAxis is not properly injected.");
        }

        // Fetch borrowing data for the last 30 days and populate the bar chart
        Map<String, Integer> borrowingData = getBorrowingDataForLast30Days();
        populateBarChart(borrowingData);
    }

    private Map<String, Integer> getBorrowingDataForLast30Days() {
        Map<String, Integer> data = new HashMap<>();
        String url = "jdbc:mysql://localhost:3306/Library";
        String user = "root";  // Replace with your actual database username
        String password = "cytech0001";  // Replace with your actual database password

        String query = "SELECT isbn, COUNT(*) as borrow_count " +
                "FROM books " +
                "WHERE loan_date >= CURDATE() - INTERVAL 30 DAY " +
                "GROUP BY isbn";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String isbn = rs.getString("isbn");
                int count = rs.getInt("borrow_count");
                data.put(isbn, count);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    private void populateBarChart(Map<String, Integer> data) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        barChart.getData().add(series);
    }
}










