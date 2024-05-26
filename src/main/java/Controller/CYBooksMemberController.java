package Controller;


import Model.Member;
import Utils.DatabaseUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller class for managing the member view page in the CYBooks application.
 * This controller handles loading and filtering member data and handles navigation between the deleting and adding pages of the application.
 */
public class CYBooksMemberController {
    @FXML private AnchorPane mainContainer;
    @FXML private Button BackHomePage2;
    @FXML private Button Add;
    @FXML private Button Delete;
    @FXML private TableView<Member> borrowingTableView;
    @FXML private TableColumn<Member, Integer> numberBorrowColumn;
    @FXML private TableColumn<Member, String> lastNameColumn;
    @FXML private TableColumn<Member, String> nameColumn;
    @FXML private TableColumn<Member, LocalDate> birthDateColumn;
    @FXML private TableColumn<Member, String> mailColumn;
    //@FXML private TableColumn<Member, Boolean> inOrderColumn;
    @FXML private TableColumn<Member, String> borrowedBooksColumn;
    @FXML private TextField searchMember;

    private ObservableList<Member> memberData = FXCollections.observableArrayList();
    private FilteredList<Member> filteredData = new FilteredList<>(memberData, p -> true);

    /**
     * Initializes the controller. Sets up the table columns and loads member data.
     */
    public void initialize() {
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        numberBorrowColumn.setCellValueFactory(new PropertyValueFactory<>("numberBorrow"));
        birthDateColumn.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        mailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        //inOrderColumn.setCellValueFactory(new PropertyValueFactory<>("inOrder"));
        borrowedBooksColumn.setCellValueFactory(new PropertyValueFactory<>("borrowedBooks"));
        borrowingTableView.setItems(filteredData);
        loadMemberData();
        setupFiltering();
    }

    /**
     * Loads member data from the database and fills the table view.
     */
    private void loadMemberData() {
        try (Connection connection = DatabaseUtil.getConnection();
             Statement statement1 = connection.createStatement();
             Statement statement2 = connection.createStatement();
             ResultSet resultSet = statement1.executeQuery("SELECT * FROM users")) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String firstName = resultSet.getString("firstname");
                String lastName = resultSet.getString("lastname");
                int numberBorrow = resultSet.getInt("number_borrowing");
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

                Member record = new Member(id, firstName, lastName,numberBorrow, inOrder, email, birthDate, borrowedBooks);
                memberData.add(record);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Sets up filtering for the borrowing data based on the input text.
     * Filters the borrowing records displayed in the table view according to the search criteria.
     */
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
                        || String.valueOf(record.getNumberBorrow()).contains(lowerCaseFilter)
                        || record.getBorrowedBooks().toString().toLowerCase().contains(lowerCaseFilter)
                        || record.getBirthDate().toString().contains(lowerCaseFilter);
            });
        });
    }

    /**
     * Loads the specified FXML view into the main container.
     *
     * @param fxmlFileName the name of the FXML file to load.
     */
    @FXML private void loadView(String fxmlFileName) {
        try {
            if (mainContainer == null) {
                System.err.println("Erreur : mainContainer n'a pas été correctement initialisé.");
                return;
            }

            // Load the specified FXML view file
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
     * Returns to the main home page.
     */
    public void returnMain() {
        loadView("CYBooks_Home.fxml");
    }

    /**
     * Loads the view for adding a new member.
     */
    public void AddMember() {
        loadView("CYBooks_NewMember.fxml");
    }

    /**
     * Loads the view for deleting a member.
     */
    public void DeleteMember() {
        loadView("CYBooks_DeleteMember.fxml");
    }






}
