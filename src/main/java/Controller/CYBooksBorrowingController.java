package Controller;

import Utils.DatabaseUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.sql.ResultSet;
import java.sql.Statement;

public class CYBooksBorrowingController {
    @FXML private AnchorPane mainContainer;
    @FXML private Button NewBorrowing;
    @FXML private Button BackHomePage;

    @FXML private TableView<BorrowingRecord> borrowingTableView;
    @FXML private TableColumn<BorrowingRecord, String> isbnColumn;
    @FXML private TableColumn<BorrowingRecord, String> nameColumn;
    @FXML private TableColumn<BorrowingRecord, String> authorColumn;
    @FXML private TableColumn<BorrowingRecord, Integer> yearColumn;
    @FXML private TableColumn<BorrowingRecord, String> editorColumn;
    @FXML private TableColumn<BorrowingRecord, Integer> stockColumn;
    @FXML private TableColumn<BorrowingRecord, String> genreColumn;

    @FXML
    public void initialize() {
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        editorColumn.setCellValueFactory(new PropertyValueFactory<>("editor"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));

        loadBorrowingData();
    }

    private void loadBorrowingData() {
        ObservableList<BorrowingRecord> borrowingData = FXCollections.observableArrayList();

        try (Connection connection = DatabaseUtil.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM livres_empruntes")) {

            while (resultSet.next()) {
                String isbn = resultSet.getString("isbn");
                String name = resultSet.getString("titre_livre");
                // Vous pouvez ajouter d'autres champs si nécessaire
                String author = "Unknown"; // Remplacer par le champ approprié
                int year = 0; // Remplacer par le champ approprié
                String editor = "Unknown"; // Remplacer par le champ approprié
                int stock = resultSet.getInt("quantite_disponible");
                String genre = "Unknown"; // Remplacer par le champ approprié

                BorrowingRecord record = new BorrowingRecord(isbn, name, author, year, editor, stock, genre);
                borrowingData.add(record);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        borrowingTableView.setItems(borrowingData);
    }

    public void AddBorrowing() {
        loadView("CYBooks_NewBorrowing.fxml");
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

    public void returnMain() {
        loadView("CYBooks_Home.fxml");
    }

    // Classe pour représenter une ligne de l'historique des emprunts
    public static class BorrowingRecord {
        private String isbn;
        private String name;
        private String author;
        private int year;
        private String editor;
        private int stock;
        private String genre;

        public BorrowingRecord(String isbn, String name, String author, int year, String editor, int stock, String genre) {
            this.isbn = isbn;
            this.name = name;
            this.author = author;
            this.year = year;
            this.editor = editor;
            this.stock = stock;
            this.genre = genre;
        }

        public String getIsbn() {
            return isbn;
        }

        public String getName() {
            return name;
        }

        public String getAuthor() {
            return author;
        }

        public int getYear() {
            return year;
        }

        public String getEditor() {
            return editor;
        }

        public int getStock() {
            return stock;
        }

        public String getGenre() {
            return genre;
        }
    }
}
