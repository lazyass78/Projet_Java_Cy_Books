package Controller;


import Utils.DatabaseUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CYBooksMemberController {
    @FXML private AnchorPane mainContainer;
    @FXML private Button BackHomePage2;
    @FXML private Button Search;
    @FXML private Button Add;
    @FXML private Button Delete;
    @FXML private TableView<CYBooksMemberRecord> borrowingTableView;
    @FXML private TableColumn<CYBooksMemberRecord, Integer> memberIdColumn;
    @FXML private TableColumn<CYBooksMemberRecord, String> lastNameColumn;
    @FXML private TableColumn<CYBooksMemberRecord, String> nameColumn;
    @FXML private TableColumn<CYBooksMemberRecord, LocalDate> birthDateColumn;
    @FXML private TableColumn<CYBooksMemberRecord, String> mailColumn;
    @FXML private TableColumn<CYBooksMemberRecord, Boolean> inOrderColumn;
    @FXML private TableColumn<CYBooksMemberRecord, String> borrowedBooksColumn;
    @FXML private TextField searchMember;

    private ObservableList<CYBooksMemberRecord> memberData = FXCollections.observableArrayList();
    private FilteredList<CYBooksMemberRecord> filteredData = new FilteredList<>(memberData, p -> true);

    public void initialize() {
        memberIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        birthDateColumn.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        mailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        inOrderColumn.setCellValueFactory(new PropertyValueFactory<>("inOrder"));
        borrowedBooksColumn.setCellValueFactory(new PropertyValueFactory<>("borrowedBooks"));
        borrowingTableView.setItems(filteredData);
        loadMemberData();
        setupFiltering();
    }

    private void loadMemberData() {
        try (Connection connection = DatabaseUtil.getConnection();
             Statement statement1 = connection.createStatement();
             Statement statement2 = connection.createStatement();
             ResultSet resultSet = statement1.executeQuery("SELECT * FROM users")) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String firstName = resultSet.getString("firstname");
                String lastName = resultSet.getString("lastname");
                boolean inOrder = resultSet.getBoolean("member_in_good_standing");
                String email = resultSet.getString("email");
                LocalDate birthDate = resultSet.getDate("birth_date").toLocalDate();

                // Récupérer les ISBN des livres empruntés pour ce membre
                List<String> borrowedBooks = new ArrayList<>();
                String getBooksQuery = "SELECT books.isbn FROM books WHERE user_id = " + id;
                try (ResultSet booksResultSet = statement2.executeQuery(getBooksQuery)) {
                    while (booksResultSet.next()) {
                        borrowedBooks.add(booksResultSet.getString("isbn"));
                    }
                }

                CYBooksMemberRecord record = new CYBooksMemberRecord(id, firstName, lastName, inOrder, email, birthDate, borrowedBooks);
                memberData.add(record);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setupFiltering() {
        searchMember.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(record -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();
                return record.getFirstName().toLowerCase().contains(lowerCaseFilter)
                        || record.getLastName().toLowerCase().contains(lowerCaseFilter)
                        || record.getEmail().toLowerCase().contains(lowerCaseFilter)
                        || String.valueOf(record.getId()).contains(lowerCaseFilter)
                        || record.getBorrowedBooks().toString().toLowerCase().contains(lowerCaseFilter);
            });
        });
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


    public void returnMain() {
        loadView("CYBooks_Home.fxml");
    }

    public void AddMember() {
        loadView("CYBooks_NewMember.fxml");
    }

    public void DeleteMember() {
        loadView("CYBooks_DeleteMember.fxml");
    }






}
