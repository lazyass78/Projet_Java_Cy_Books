package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.sql.*;

public class CYBooksNewBorrowingController {

    @FXML private AnchorPane mainContainer;
    @FXML private TextField memberId;
    @FXML private TextField isbnDocument;
    @FXML private TextField borrowingDate;
    @FXML private Button SaveBorrowing;
    @FXML private Button CancelBorrowing;

    @FXML private void SaveNewBorrowing(ActionEvent actionEvent) {
        String memberIdText = memberId.getText();
        String isbnText = isbnDocument.getText();
        String borrowingDateText = borrowingDate.getText();

        if (memberIdText.isEmpty() || isbnText.isEmpty() || borrowingDateText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "Please fill in all fields");
            return;
        }

        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Library", "root", "cytech0001");

            // Check if member exists
            if (!checkMemberExists(connection, memberIdText)) {
                showAlert(Alert.AlertType.ERROR, "Member Error", "Member ID not found");
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

            // Save borrowing record
            String query = "INSERT INTO books (isbn, user_id, loan_date, return_date, quantity_available, total_quantity) VALUES (?, ?, ?, DATE_ADD(?, INTERVAL 2 WEEK), ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, isbnText);
            preparedStatement.setInt(2, Integer.parseInt(memberIdText));
            preparedStatement.setDate(3, java.sql.Date.valueOf(borrowingDateText));
            preparedStatement.setDate(4, java.sql.Date.valueOf(borrowingDateText));
            preparedStatement.setInt(5, 0); // Assuming quantity available is 0 when borrowed
            preparedStatement.setInt(6, 1); // Assuming total quantity is 1 for now

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Borrowing registered successfully");
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

    private boolean checkMemberExists(Connection connection, String memberId) throws SQLException {
        String query = "SELECT id FROM users WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, Integer.parseInt(memberId));
        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet.next();
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
            return !date.before(new java.sql.Date(System.currentTimeMillis()));
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
