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

public class CYBooksNewBorrowingController {

    @FXML private AnchorPane mainContainer;

    @FXML private TextField memberId;
    @FXML private TextField isbnDocument;
    @FXML private TextField borrowingDate;

    @FXML private Button SaveBorrowing;
    @FXML private Button Cancel;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loadView(String fxmlFileName) {
        try {
            if (mainContainer == null) {
                System.err.println("Erreur : mainContainer n'a pas été correctement initialisé.");
                return;
            }

            // Load the specified FXML view
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFileName));
            Parent view = fxmlLoader.load();

            // Replace the current main container content with the new view content
            mainContainer.getChildren().clear();
            mainContainer.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML public void SaveNewBorrowing(ActionEvent actionEvent) {
        String memberIdText = memberId.getText();
        String isbnDocumentText = isbnDocument.getText();
        String borrowingDateText = borrowingDate.getText();

        if (memberIdText.isEmpty() || isbnDocumentText.isEmpty() || borrowingDateText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "Please fill in all fields");
            return;
        }

        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Library", "root", "cytech0001");
            String query = "INSERT INTO books (isbn, user_id, loan_date, return_date, quantity_available, total_quantity) VALUES (?, ?, ?, DATE_ADD(?, INTERVAL 2 WEEK), 0, 1)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, isbnDocumentText);
            preparedStatement.setInt(2, Integer.parseInt(memberIdText));
            preparedStatement.setDate(3, java.sql.Date.valueOf(borrowingDateText));
            preparedStatement.setDate(4, java.sql.Date.valueOf(borrowingDateText));

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                showAlert(AlertType.INFORMATION, "Success", "Borrowing record added successfully");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Database Error", "Failed to add borrowing record");
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

    public void CancelBorrowing(ActionEvent actionEvent) {
        loadView("CYBooks_Home.fxml");
    }
}



