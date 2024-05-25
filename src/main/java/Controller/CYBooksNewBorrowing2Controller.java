package Controller;

import Utils.DatabaseUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.w3c.dom.Document;
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
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CYBooksNewBorrowing2Controller {

    @FXML private AnchorPane mainContainer;
    @FXML private TextField memberMail;
    @FXML private TextField isbnDocument;
    @FXML private TextField borrowingDate;
    @FXML private Button SaveBorrowing;
    @FXML private Button CancelBorrowing;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9.]+@(.+)$");

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
        if (!isValidEmail(memberMailText)) {
            showAlert(Alert.AlertType.ERROR, "Email Error", "Invalid email format");
            return;
        }
        Connection connection = null;
        try {
            //à changer
            connection = DatabaseUtil.getConnection();

            // Check if member exists
            if (!checkMemberExists(connection, memberMailText)) {
                showAlert(Alert.AlertType.ERROR, "Member Error", "Member not found");
                return;
            }

            // Check if book is not already borrowed
            if (!checkBookNotBorrowed(connection, isbnText)) {
                showAlert(Alert.AlertType.ERROR, "Book Error", "Book is already borrowed");
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
            if (!canUserBorrowMoreBooks(connection, memberMailText)) {
                showAlert(Alert.AlertType.ERROR, "Limit Reached", "This user has reached the borrowing limit");
                return;
            }

            connection.setAutoCommit(false); // Begin transaction

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
                // Update number_borrowing for the user
                String updateQuery = "UPDATE users SET number_borrowing = number_borrowing + 1 WHERE email = ?";
                PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                updateStatement.setString(1, memberMailText);
                updateStatement.executeUpdate();

                // Insert new record into historic
                String insertHistoricQuery = "INSERT INTO historic (isbn, loan_date) VALUES (?, ?)";
                PreparedStatement insertHistoricStatement = connection.prepareStatement(insertHistoricQuery);
                insertHistoricStatement.setString(1, isbnText);
                insertHistoricStatement.setDate(2, java.sql.Date.valueOf(borrowingDateText));
                insertHistoricStatement.executeUpdate();

                connection.commit(); // Commit transaction
                showAlert(Alert.AlertType.INFORMATION, "Success", "Borrowing registered successfully");
                loadView("CYBooks_Borrowing.fxml");
            } else {
                connection.rollback(); // Rollback transaction on failure
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to register borrowing");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (connection != null) {
                    connection.rollback(); // Rollback transaction on exception
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to register borrowing");
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true); // Reset autocommit
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

    private boolean isValidEmail(String email) {
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

    private boolean checkBookExistsInHistoric(Connection connection, String isbn) throws SQLException {
        String query = "SELECT isbn FROM historic WHERE isbn = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, isbn);
        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet.next();
    }
    private boolean canUserBorrowMoreBooks(Connection connection, String memberEmail) throws SQLException {
        String query = "SELECT number_borrowing FROM users WHERE email = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, memberEmail);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            int numberBorrowing = resultSet.getInt("number_borrowing");
            return numberBorrowing < 3;
        }
        return false;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML public void CancelBorrowing(ActionEvent actionEvent) {
        loadView("MainAuthor.fxml");
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