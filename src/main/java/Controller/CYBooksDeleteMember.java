package Controller;

import javafx.event.ActionEvent;
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

public class CYBooksDeleteMember {

    @FXML private AnchorPane mainContainer;

    @FXML private Button Delete;
    @FXML private Button Cancel;
    @FXML private TextField memberId;
    @FXML private TextField name;

    public void DeleteMember(ActionEvent actionEvent) {
        String id = memberId.getText().trim();
        String firstName = name.getText().trim();

        // Vérifier que les deux champs sont renseignés
        if (id.isEmpty() || firstName.isEmpty()) {
            showError("Both fields must be filled out.");
            return;
        }

        try (Connection connection = DatabaseUtil.getConnection()) {
            // Vérifier l'existence de l'utilisateur et correspondance du prénom
            String selectQuery = "SELECT firstname FROM users WHERE id = ?";
            PreparedStatement selectStmt = connection.prepareStatement(selectQuery);
            selectStmt.setString(1, id);
            ResultSet resultSet = selectStmt.executeQuery();

            if (resultSet.next()) {
                String dbFirstName = resultSet.getString("firstname");
                if (!dbFirstName.equals(firstName)) {
                    showError("ID and first name do not match.");
                    return;
                }
            } else {
                showError("User ID not found in the database.");
                return;
            }

            // Supprimer l'utilisateur
            String deleteQuery = "DELETE FROM users WHERE id = ?";
            PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery);
            deleteStmt.setString(1, id);
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

    public void CancelDelete() {
        loadView("CYBooks_Member.fxml");
    }

    @FXML private void loadView(String fxmlFileName) {
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
}
