package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.scene.control.Alert.AlertType;

public class CYBooksNewMemberController {

    @FXML private AnchorPane mainContainer;

    @FXML private Button SaveMember;
    @FXML private Button Cancel;



    private Stage stage;

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
    @FXML
    private void cancelBorrowing() {
        stage = (Stage) lastName.getScene().getWindow();
        stage.close();
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    

    @FXML public void SaveNewMember(ActionEvent actionEvent) {
            String lastNameText = lastName.getText();
            String firstNameText = firstName.getText();
            String birthDateText = birthDate.getText();
            String emailText = email.getText();

            if (lastNameText.isEmpty() || firstNameText.isEmpty() || birthDateText.isEmpty() || emailText.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Form Error!", "Please fill in all fields");
                return;
            }

            Connection connection = null;
            try {
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Library", "root", "cytech0001");
                String query = "INSERT INTO users (firstname, lastname, member_in_good_standing, email, birth_date) VALUES (?, ?, TRUE, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, firstNameText);
                preparedStatement.setString(2, lastNameText);
                preparedStatement.setString(3, emailText);
                preparedStatement.setDate(4, java.sql.Date.valueOf(birthDateText));

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    showAlert(AlertType.INFORMATION, "Success", "Member added successfully");
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
        // tout bdd pour enregister le membre dans la base...
        loadView("CYBooks_Member.fxml");
    }

    public void CancelMember(ActionEvent actionEvent) {
    }
}
