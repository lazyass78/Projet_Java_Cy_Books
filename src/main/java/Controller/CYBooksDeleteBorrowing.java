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

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CYBooksDeleteBorrowing {

    @FXML private AnchorPane mainContainer;
    @FXML private Button Delete;
    @FXML private Button Cancel;

    @FXML private TextField memberMail;
    @FXML private TextField isbnDocument;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9.]+@(.+)$");

    public void CancelDelete(ActionEvent actionEvent) {
        loadView("CYBooks_Borrowing.fxml");
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

    public void DeleteBorrowing() {
        String isbn = isbnDocument.getText().trim();
        String mail = memberMail.getText().trim();

        // Vérifier que les deux champs sont renseignés
        if (mail.isEmpty() || isbn.isEmpty()) {
            showError("Both fields must be filled out.");
            return;
        }

        if (!isValidEmail(mail)) {
            showAlert(Alert.AlertType.ERROR, "Email Error", "Invalid email format");
            return;
        }


        try (Connection connection = DatabaseUtil.getConnection()) {
            // Vérifier l'existence de l'utilisateur et correspondance du prénom
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

            // Supprimer l'utilisateur
            String deleteQuery = "DELETE FROM books WHERE isbn = ?";
            PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery);
            deleteStmt.setString(1, isbn);
            int rowsAffected = deleteStmt.executeUpdate();

            if (rowsAffected > 0) {
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

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean isValidEmail(String email) {
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }
}
