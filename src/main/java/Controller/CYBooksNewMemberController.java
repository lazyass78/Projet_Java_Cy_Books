package Controller;

import Utils.DatabaseUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.control.Alert.AlertType;
import javafx.util.StringConverter;

/**
 * Controller for managing new member registration in the CYBooks application.
 */
public class CYBooksNewMemberController {

    @FXML private AnchorPane mainContainer;

    @FXML private TextField lastName;
    @FXML private TextField name;
    @FXML private DatePicker birthDate;
    @FXML private TextField mail;

    @FXML private Button SaveMember;
    @FXML private Button Cancel;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9.]+@(.+)$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z]+$");

    /**
     * Loads the specified FXML view into the main container.
     *
     * @param fxmlFileName the name of the FXML file to load.
     */
    @FXML
    private void loadView(String fxmlFileName) {
        try {
            if (mainContainer == null) {
                System.err.println("Error: mainContainer has not been properly initialized.");
                return;
            }

            // Load the FXML file for the specified view
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
     * Shows an alert with the specified type, title, and message.
     *
     * @param alertType the type of alert.
     * @param title the title of the alert.
     * @param message the message of the alert.
     */
    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Initializes the controller class. This method is automatically called after the FXML file has been loaded.
     * It sets up the date picker, disables future dates, and ensures correct date format.
     */
    @FXML
    private void initialize() {
        // Prevent future dates from being selected
        birthDate.setDayCellFactory(picker -> new javafx.scene.control.DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                // Disable future dates
                if (date.isAfter(LocalDate.now().minusYears(10))) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;"); // Optional: style for disabled dates
                }
            }
        });

        // Disable manual text entry in the DatePicker
        birthDate.getEditor().setDisable(true);

        // Ensure the format is correct
        birthDate.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return DATE_FORMATTER.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, DATE_FORMATTER);
                } else {
                    return null;
                }
            }
        });
        // Set placeholders for name and last name
        name.setPromptText("Firstname");
        lastName.setPromptText("Lastname");
        mail.setPromptText("member@cy-books.fr");


        // Set default date to 10 years ago
        birthDate.setValue(LocalDate.now().minusYears(10));

        // Add listeners to name and last name fields to format input
        addNameValidation(name);
        addNameValidation(lastName);
    }

    /**
     * Adds a listener to the specified text field for formatting input.
     *
     * @param textField the text field to add the listener to.
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

    /**
     * Handles the action of saving a new member registration.
     * @throws SQLException if a database access error occurs.
     */
    @FXML public void SaveNewMember() throws SQLException {
            String lastNameText = lastName.getText();
            String firstNameText = name.getText();
            LocalDate birthDateValue = birthDate.getValue();
            String emailText = mail.getText();

            if (lastNameText.isEmpty() || firstNameText.isEmpty() || birthDateValue == null || emailText.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Form Error!", "Please fill in all fields");
                return;
            }
            if (!isValidEmail(emailText)) {
                showAlert(Alert.AlertType.ERROR, "Email Error", "Invalid email format");
                return;
            }
            if (!isEmailUnique(emailText)) {
                showAlert(Alert.AlertType.ERROR, "Email Error", "Email already exists");
                return;
            }
            String birthDateText = birthDateValue.format(DATE_FORMATTER);

            Connection connection = DatabaseUtil.getConnection();
            try {
                String query = "INSERT INTO users (firstname, lastname, member_in_good_standing, email, birth_date) VALUES (?, ?, TRUE, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, firstNameText);
                preparedStatement.setString(2, lastNameText);
                preparedStatement.setString(3, emailText);
                preparedStatement.setDate(4, java.sql.Date.valueOf(birthDateText));

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    showAlert(AlertType.INFORMATION, "Success", "Member added successfully");
                    loadView("CYBooks_Member.fxml");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(AlertType.ERROR, "Database Error", "Failed to add member");
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

    /**
     * Checks if the specified email is unique in the database.
     *
     * @param email the email to check.
     * @return true if the email is unique, false otherwise.
     */
    private boolean isEmailUnique(String email) {
        Connection connection = null;
        try {
            connection = DatabaseUtil.getConnection();
            String query = "SELECT COUNT(*) FROM users WHERE email = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Checks if the specified email address is in the correct format.
     *
     * @param email the email address to be checked.
     * @return true if the email address is valid, false otherwise.
     */
    private boolean isValidEmail(String email) {
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

    /**
     * Cancels the operation of registering a new member and loads the member view.
     */
    public void CancelMember() {
        loadView("CYBooks_Member.fxml");
    }
}
