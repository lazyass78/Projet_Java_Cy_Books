package Controller;

import Utils.DatabaseUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
                String title = "";
                int borrowCount = resultSet.getInt("borrow_count");
                String apiUrl = "https://gallica.bnf.fr/SRU?operation=searchRetrieve&version=1.2";
                String encodedQuery = URLEncoder.encode(isbn, "UTF-8");
                String searchQuery = "query=dc.identifier%20all%20" + encodedQuery;
                String url = apiUrl + "&" + searchQuery;

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(url))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                String responseBody = response.body();

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new InputSource(new StringReader(responseBody)));
                NodeList titleNodes = doc.getElementsByTagName("dc:title");
                if (titleNodes.getLength() > 0) {
                    Element titleElement = (Element) titleNodes.item(0);
                    title = titleElement.getTextContent();
                }

                String truncatedTitle = truncateTitle(title, 30);  // Truncate to 30 characters
                series.getData().add(new XYChart.Data<>(truncatedTitle, borrowCount));
                colorMap.put(truncatedTitle, generateRandomColor());
            }

            // Step 5: Close the connection
            connection.close();

            barChart.getData().add(series);
            applyBarColorsAndLabels(series, colorMap);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String truncateTitle(String title, int maxLength) {
        if (title.length() > maxLength) {
            return title.substring(0, maxLength) + "...";
        } else {
            return title;
        }
    }

    @FXML
    private void loadView(String fxmlFileName) {
        try {
            if (mainContainer == null) {
                System.err.println("Error: mainContainer has not been properly initialized.");
                return;
            }

            // Load the FXML file of the specified view
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFileName));
            Parent view = fxmlLoader.load();

            // Replace the current content of the main container with the content of the new view
            mainContainer.getChildren().clear();
            mainContainer.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void applyBarColorsAndLabels(XYChart.Series<String, Number> series, Map<String, String> colorMap) {
        for (XYChart.Data<String, Number> data : series.getData()) {
            Node node = data.getNode();
            String color = colorMap.get(data.getXValue());
            node.setStyle("-fx-bar-fill: " + color + ";");

            // Add a label at the bottom of each bar
            String labelText = data.getXValue();
            Label label = new Label(labelText);
            label.setStyle("-fx-text-fill: black; -fx-font-size: 10px;");

            Platform.runLater(() -> {
                ((Group) node.getParent()).getChildren().add(label);
                // Adjust the label's position
                label.setTranslateY(node.getBoundsInParent().getMaxY() + 10); // Adjust this value as needed
                label.setTranslateX(node.getBoundsInParent().getMinX() + node.getBoundsInParent().getWidth() / 2 - label.getWidth() / 2);
            });
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
