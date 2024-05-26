package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import Utils.DatabaseUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Controller class for managing the "Delete Member" functionality in the CYBooks application.
 * This controller handles the deletion of a member from the database by verifying their email and last name.
 */
public class CYBooksDeleteMember {

    @FXML private AnchorPane mainContainer;

    @FXML private Button Delete;
    @FXML private Button Cancel;
    @FXML private TextField memberMail;
    @FXML private TextField memberLastName;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9.]+@(.+)$");

    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z]+$");

    /**
     * Initializes the controller class. This method is automatically called after the fxml file has been loaded.
     * Adds validation for the last name input field.
     */
    @FXML
    public void initialize() {
        addNameValidation(memberLastName);
    }

    /**
     * Handles the deletion of a member when the "Delete" button is pressed.
     * Verifies the email and last name of the member, checks their existence in the database,
     * and deletes the member if the verification is successful.
     */
    public void DeleteMember() {
        String mail = memberMail.getText().trim();
        String lastName = memberLastName.getText().trim();

        // Check that both fields are filled in
        if (mail.isEmpty() || lastName.isEmpty()) {
            showError("Both fields must be filled out.");
            return;
        }

        if (!isValidEmail(mail)) {
            showAlert(Alert.AlertType.ERROR, "Email Error", "Invalid email format");
            return;
        }

        try (Connection connection = DatabaseUtil.getConnection()) {
            // Check that the user exists and that the last name matches
            String selectQuery = "SELECT lastname FROM users WHERE email = ?";
            PreparedStatement selectStmt = connection.prepareStatement(selectQuery);
            selectStmt.setString(1, mail);
            ResultSet resultSet = selectStmt.executeQuery();

            if (resultSet.next()) {
                String dbLastName = resultSet.getString("lastname");
                if (!dbLastName.equals(lastName)) {
                    showError("Mail and last name do not match.");
                    return;
                }
            } else {
                showError("User mail not found in the database.");
                return;
            }

            // Delete user
            String deleteQuery = "DELETE FROM users WHERE email = ?";
            PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery);
            deleteStmt.setString(1, mail);
            int rowsAffected = deleteStmt.executeUpdate();

            if (rowsAffected > 0) {
                showSuccess("User deleted successfully.");
                loadView("CYBooks_Member.fxml");
            } else {
                showError("Failed to delete the user.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showError("An error occurred while connecting to the database.");
        }
    }

    /**
     * Cancels the deletion process and returns to the member view.
     * This method is called when the "Cancel" button is pressed.
     */
    public void CancelDelete() {
        loadView("CYBooks_Member.fxml");
    }

    /**
     * Loads the specified FXML view into the main container.
     *
     * @param fxmlFileName the name of the FXML file to load.
     */
    @FXML private void loadView(String fxmlFileName) {
        try {
            if (mainContainer == null) {
                System.err.println("Error : mainContainer has not been initialised correctly.");
                return;
            }

            // Loads the FXML file for the specified view
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFileName));
            Parent view = fxmlLoader.load();

            // Replace the current content of the main container with the new view
            mainContainer.getChildren().clear();
            mainContainer.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows an alert dialog with the specified title and message.
     *
     * @param alertType the type of alert to be shown.
     * @param title the title of the alert.
     * @param message the message to be displayed in the alert.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows an error alert dialog with the specified message.
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
     * Shows a success alert dialog with the specified message.
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
     * Validates the format of the given email address.
     *
     * @param email the email address to be validated.
     * @return true if the email address is valid, false otherwise.
     */
    private boolean isValidEmail(String email) {
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

    /**
     * Adds validation to the specified TextField to ensure that it contains a valid name.
     * The name must only contain alphabetic characters and will be automatically formatted.
     *
     * @param textField the TextField to add validation to.
     */
    private void addNameValidation(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && !NAME_PATTERN.matcher(newValue).matches()) {
                textField.setText(oldValue);
            } else if (!newValue.isEmpty()) {
                String formattedValue = newValue.substring(0, 1).toUpperCase() + newValue.substring(1).toLowerCase();
                if (!formattedValue.equals(newValue)) {
                    textField.setText(formattedValue);
                }
            }
        });
    }
}
