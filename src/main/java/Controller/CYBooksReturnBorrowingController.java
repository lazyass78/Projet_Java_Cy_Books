package Controller;

import Utils.DatabaseUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Controller class for returning a borrowing in the CYBooks application.
 * Deletes a borrowing record by specifying the ISBN of the borrowed book and the email of the borrower.
 */
public class CYBooksReturnBorrowingController {

    @FXML private AnchorPane mainContainer;
    @FXML private Button Delete;
    @FXML private Button Cancel;

    @FXML private TextField memberMail;
    @FXML private TextField isbnDocument;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9.]+@(.+)$");

    /**
     * Cancels the operation of deleting a borrowing record and returns to the borrowing view.
     * Invoked when the 'Cancel' button is clicked.
     */
    public void CancelDelete() {
        loadView("CYBooks_Borrowing.fxml");
    }

    /**
     * Loads the specified FXML view into the main container.
     *
     * @param fxmlFileName the filename of the FXML view to be loaded.
     */
    @FXML
    private void loadView(String fxmlFileName) {
        try {
            if (mainContainer == null) {
                System.err.println("Erreur : mainContainer n'a pas été correctement initialisé.");
                return;
            }

            // Loads the FXML file for the specified view
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFileName));
            Parent view = fxmlLoader.load();

            // Replaces the current contents of the main container with the contents of the new view
            mainContainer.getChildren().clear();
            mainContainer.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes the borrowing record associated with the specified ISBN and user email.
     * Invoked when the 'Return Borrow' button is clicked.
     */
    public void DeleteBorrowing() {
        String isbn = isbnDocument.getText().trim();
        String mail = memberMail.getText().trim();

        // Check that both fields are filled in
        if (mail.isEmpty() || isbn.isEmpty()) {
            showError("Both fields must be filled out.");
            return;
        }

        if (!isValidEmail(mail)) {
            showAlert(Alert.AlertType.ERROR, "Email Error", "Invalid email format");
            return;
        }


        try (Connection connection = DatabaseUtil.getConnection()) {
            // Check that the user exists and that the first name matches
            String selectQuery = "SELECT isbn FROM books JOIN users ON user_id = id WHERE email = ? AND isbn = ?";
            PreparedStatement selectStmt = connection.prepareStatement(selectQuery);
            selectStmt.setString(1, mail);
            selectStmt.setString(2, isbn);
            ResultSet resultSet = selectStmt.executeQuery();

            if (resultSet.next()) {
                String dbisbn= resultSet.getString("isbn");
                if (!dbisbn.equals(isbn)) {
                    showError("Mail and ISBN do not match.");
                    return;
                }
            } else {
                showError("User mail or ISBN not found in the database.");
                return;
            }

            // Delete user
            String deleteQuery = "DELETE FROM books WHERE isbn = ?";
            PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery);
            deleteStmt.setString(1, isbn);
            int rowsAffected = deleteStmt.executeUpdate();

            if (rowsAffected > 0) {
                // Update number_borrowing for the user
                String updateQuery = "UPDATE users SET number_borrowing = number_borrowing - 1 WHERE email = ?";
                PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                updateStatement.setString(1, mail);
                updateStatement.executeUpdate();
                showSuccess("Borrowing deleted successfully.");
                loadView("CYBooks_Borrowing.fxml");
            } else {
                showError("Failed to delete the borrowing.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showError("An error occurred while connecting to the database.");
        }
    }

    /**
     * Shows an alert with the specified type, title, and message.
     *
     * @param alertType the type of alert.
     * @param title the title of the alert.
     * @param message the message of the alert.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays an error message in an alert dialog with the specified message.
     *
     * @param message the error message to be displayed.
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays a success message in an alert dialog with the specified message.
     *
     * @param message the success message to be displayed.
     */
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Checks if the given email address matches the predefined email pattern.
     *
     * @param email the email address to be validated.
     * @return true if the email address matches the pattern, false otherwise.
     */
    private boolean isValidEmail(String email) {
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }
}
