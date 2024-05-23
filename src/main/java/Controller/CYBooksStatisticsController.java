package Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.*;

public class CYBooksStatisticsController {

    @FXML
    private AnchorPane mainContainer;
    @FXML
    private TextArea statisticsTextArea;
    @FXML
    private BarChart<String, Integer> borrowingBooksChart;

    private static final String URL = "jdbc:mysql://localhost:3306/Library";
    private static final String USER = "root";
    private static final String PASSWORD = "cytech0001";

    @FXML
    private void showStatistics(ActionEvent actionEvent) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            populateMostBorrowedBooksChart(connection);
            // Additional statistics if needed
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to retrieve statistics");
        }
    }

    private void populateMostBorrowedBooksChart(Connection connection) throws SQLException {
        ObservableList<XYChart.Series<String, Integer>> seriesList = FXCollections.observableArrayList();

        String query = "SELECT isbn, COUNT(*) AS borrow_count FROM books GROUP BY isbn ORDER BY borrow_count DESC LIMIT 10";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String isbn = rs.getString("isbn");
                int borrowCount = rs.getInt("borrow_count");

                // Fetch book title from API or database based on ISBN
                String bookTitle = fetchBookTitle(connection, isbn);

                // Create a series for each book
                XYChart.Series<String, Integer> series = new XYChart.Series<>();
                series.setName(bookTitle);
                series.getData().add(new XYChart.Data<>(bookTitle, borrowCount));
                seriesList.add(series);
            }
        }

        borrowingBooksChart.setData(seriesList);
    }

    private String fetchBookTitle(Connection connection, String isbn) {
        // Code to fetch book title from database based on ISBN
        // Replace this with your actual database query
        try {
            String query = "SELECT * FROM books WHERE isbn = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, isbn);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String title = "";
                        if (!isbn.isEmpty()) {
                            String apiUrl = "https://gallica.bnf.fr/SRU?operation=searchRetrieve&version=1.2";
                            String encodedQuery = URLEncoder.encode(isbn, "UTF-8");
                            String searchQuery = "query=dc.identifier%20all%20" + encodedQuery;
                            String url = apiUrl + "&" + searchQuery;
                            System.out.println(url);

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
                                return title;
                            }
                        }
                    }
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ParserConfigurationException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (SAXException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown Title"; // Default value if title not found
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}






