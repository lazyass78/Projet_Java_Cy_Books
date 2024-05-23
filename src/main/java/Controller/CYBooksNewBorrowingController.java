package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.*;
import java.time.LocalDate;

public class CYBooksNewBorrowingController {

    @FXML private AnchorPane mainContainer;
    @FXML private TextField memberMail;
    @FXML private TextField isbnDocument;
    @FXML private TextField borrowingDate;
    @FXML private Button SaveBorrowing;
    @FXML private Button CancelBorrowing;

    @FXML
    private void initialize() {
        // Préremplir le champ de la date d'emprunt avec la date du jour
        borrowingDate.setText(LocalDate.now().toString());
        borrowingDate.setEditable(false);  // to block editing
        borrowingDate.setStyle("-fx-background-color: #F0F0F0;");
    }

    @FXML private void SaveNewBorrowing(ActionEvent actionEvent) {
        String memberMailText = memberMail.getText();
        String isbnText = isbnDocument.getText();
        String borrowingDateText = borrowingDate.getText();

        if (memberMailText.isEmpty() || isbnText.isEmpty() || borrowingDateText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "Please fill in all fields");
            return;
        }

        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Library", "root", "cytech0001");

            // Check if member exists
            if (!checkMemberExists(connection, memberMailText)) {
                showAlert(Alert.AlertType.ERROR, "Member Error", "Member not found");
                return;
            }

            // Check if book is not already borrowed
            if (!checkBookNotBorrowed(connection, isbnText)) {
                showAlert(Alert.AlertType.ERROR, "Book Error", "Book is already borrowed by someone else");
                return;
            }

            // Check if borrowing date is valid
            if (!isDateValid(borrowingDateText)) {
                showAlert(Alert.AlertType.ERROR, "Date Error", "Borrowing date is not valid");
                return;
            }

            // Check if id is valid
            if (!checkIdExists(isbnText)) {
                showAlert(Alert.AlertType.ERROR, "Id Error", "Id is not valid");
                return;
            }

            // Save borrowing record
            String query = "INSERT INTO books (isbn, user_id, loan_date, return_date, quantity_available, total_quantity) VALUES (?, (SELECT id FROM users WHERE email = ?), ?, DATE_ADD(?, INTERVAL 2 WEEK), ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, isbnText);
            preparedStatement.setString(2, memberMailText);
            preparedStatement.setDate(3, java.sql.Date.valueOf(borrowingDateText));
            preparedStatement.setDate(4, java.sql.Date.valueOf(borrowingDateText));
            preparedStatement.setInt(5, 0); // Assuming quantity available is 0 when borrowed
            preparedStatement.setInt(6, 1); // Assuming total quantity is 1 for now

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Borrowing registered successfully");
                loadView("CYBooks_Borrowing.fxml");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to register borrowing");
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void setDocumentIsbn(String isbn) {
        isbnDocument.setText(isbn);
    }


    private boolean checkMemberExists(Connection connection, String memberId) throws SQLException {
        String query = "SELECT email FROM users WHERE email  = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, memberMail.getText());
        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet.next();
    }
    private boolean checkIdExists(String id){
        try {
            String apiUrl = "https://gallica.bnf.fr/SRU?operation=searchRetrieve&version=1.2";
            String encodedQuery = URLEncoder.encode(id, "UTF-8");
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


            NodeList identifierNodes = doc.getElementsByTagName("dc:identifier");
            if (identifierNodes.getLength() == 0) {
                return false;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean checkBookNotBorrowed(Connection connection, String isbn) throws SQLException {
        String query = "SELECT isbn FROM books WHERE isbn = ? AND quantity_available = 0";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, isbn);
        ResultSet resultSet = preparedStatement.executeQuery();
        return !resultSet.next();
    }

    private boolean isDateValid(String dateStr) {
        try {
            java.sql.Date date = java.sql.Date.valueOf(dateStr);
            // Allow today's date or a future date
            return !date.before(new java.sql.Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML public void CancelBorrowing(ActionEvent actionEvent) {
        loadView("CYBooks_Borrowing.fxml");
    }

    @FXML private void loadView(String fxmlFileName) {
        try {
            if (mainContainer == null) {
                System.err.println("Error: mainContainer has not been properly initialized.");
                return;
            }

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFileName));
            Parent view = fxmlLoader.load();

            mainContainer.getChildren().clear();
            mainContainer.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}