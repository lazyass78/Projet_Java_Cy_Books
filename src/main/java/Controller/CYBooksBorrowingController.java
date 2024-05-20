package Controller;

import Utils.DatabaseUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;

public class CYBooksBorrowingController {
    @FXML private AnchorPane mainContainer;

    @FXML private Button NewBorrowing;
    @FXML private Button BackHomePage;
    @FXML private Button Search;

    @FXML private TableView<CYBooksBorrowingRecord> borrowingTableView;
    @FXML private TableColumn<CYBooksBorrowingRecord, String> isbnColumn;
    @FXML private TableColumn<CYBooksBorrowingRecord, String> memberIdColumn;
    @FXML private TableColumn<CYBooksBorrowingRecord, String> titleColumn;
    @FXML private TableColumn<CYBooksBorrowingRecord, String> authorColumn;
    @FXML private TableColumn<CYBooksBorrowingRecord, Integer> yearColumn;
    @FXML private TableColumn<CYBooksBorrowingRecord, String> editorColumn;
    @FXML private TableColumn<CYBooksBorrowingRecord, Integer> stockColumn;
    @FXML private TableColumn<CYBooksBorrowingRecord, String> topicsColumn;
    @FXML private TableColumn<CYBooksBorrowingRecord, String> borrowingDateColumn;
    @FXML private TableColumn<CYBooksBorrowingRecord, String> returnDateColumn;

    @FXML
    public void initialize() {
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        memberIdColumn.setCellValueFactory(new PropertyValueFactory<>("memberId"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        editorColumn.setCellValueFactory(new PropertyValueFactory<>("editor"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        topicsColumn.setCellValueFactory(new PropertyValueFactory<>("topics"));
        borrowingDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrowingDate"));
        returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        loadBorrowingData();
    }

    private void loadBorrowingData() {
        ObservableList<CYBooksBorrowingRecord> borrowingData = FXCollections.observableArrayList();

        try (Connection connection = DatabaseUtil.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM books")) {

            while (resultSet.next()) {
                String isbn = resultSet.getString("isbn");
                String memberId = resultSet.getString("user_id");
                // Vous pouvez ajouter d'autres champs si nécessaire
                String title = "Unknown"; // Remplacer par le champ approprié
                String author = "Unknown"; // Remplacer par le champ approprié
                int year = 0; // Remplacer par le champ approprié
                String editor = "Unknown"; // Remplacer par le champ approprié
                int stock = resultSet.getInt("quantity_available");
                String topics = "Unknown"; // Remplacer par le champ approprié
                LocalDate borrowingDate = resultSet.getDate("loan_date").toLocalDate();
                LocalDate returnDate = resultSet.getDate("return_date").toLocalDate();

                CYBooksBorrowingRecord record = new CYBooksBorrowingRecord(isbn, memberId,title,author,year, editor, stock,topics,borrowingDate,returnDate);
                borrowingData.add(record);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        borrowingTableView.setItems(borrowingData);
    }

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

    public void AddBorrowing() {
        loadView("CYBooks_NewBorrowing.fxml");
    }

    public void returnMain() {
        loadView("CYBooks_Home.fxml");
    }

    public void SearchDocument(ActionEvent actionEvent) {
        // a completer API tout ça
    }

    // est ce vraiment utile de faire cette classe ??

    // Classe pour représenter une ligne de l'historique des emprunts
}
