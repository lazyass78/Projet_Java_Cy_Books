package Controller;

import Model.Borrowing;
import Utils.DatabaseUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;



/***
 * Controller for managing CYBooks_Borrowing.fxml view in the CYBooks application.
 * Displays the data from the library database
 */
public class CYBooksBorrowingController {
    @FXML private AnchorPane mainContainer;

    @FXML private Button NewBorrowing;
    @FXML private Button BackHomePage;
    @FXML private Button Search;

    @FXML private TableView<Borrowing> borrowingTableView;
    @FXML private TableColumn<Borrowing, String> isbnColumn;
    @FXML private TableColumn<Borrowing, String> memberMailColumn;
    @FXML private TableColumn<Borrowing, String> titleColumn;
    @FXML private TableColumn<Borrowing, String> authorColumn;
    @FXML private TableColumn<Borrowing, Integer> yearColumn;
    @FXML private TableColumn<Borrowing, Integer> stockColumn;
    @FXML private TableColumn<Borrowing, String> borrowingDateColumn;
    @FXML private TableColumn<Borrowing, LocalDate> returnDateColumn;

    @FXML private TextField dataBook;
    private ObservableList<Borrowing> borrowingData = FXCollections.observableArrayList();
    private FilteredList<Borrowing> filteredData;

    /**
     * Initializes the borrowing table view.
     * Sets up cell value factories for each column to map the Borrowing object properties.
     * Sets up a custom cell factory for the return date column to display return dates with custom formatting and colors.
     * Loads borrowing data into the table view and sets up filtering for the table.
     */
    @FXML
    public void initialize() {
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        memberMailColumn.setCellValueFactory(new PropertyValueFactory<>("memberMail"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        borrowingDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrowingDate"));
        returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        returnDateColumn.setCellFactory(column -> new TableCell<Borrowing, LocalDate>() {
            /**
             * Updates the item in the cell with the given LocalDate.
             * If the item is null or the cell is empty, clears the text and style of the cell.
             * Otherwise, sets the text of the cell to the string representation of the LocalDate.
             * If the LocalDate is before the current date, sets the text color to red; otherwise, sets it to black.
             * @param item The LocalDate to be displayed in the cell.
             * @param empty A boolean indicating whether the cell is empty.
             */
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString());
                    if (item.isBefore(LocalDate.now())) {
                        setTextFill(javafx.scene.paint.Color.RED);
                    } else {
                        setTextFill(javafx.scene.paint.Color.BLACK);
                    }
                }
            }
        });
        loadBorrowingData();
        setupFiltering();
    }

    /**
     * Loads the borrowing data from the database and populates the table view.
     * Fetches additional information from an external API for each borrowing record.
     */
    private void loadBorrowingData() {
        try (Connection connection = DatabaseUtil.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM books JOIN users ON users.id = books.user_id")) {

            while (resultSet.next()) {
                String memberMail = resultSet.getString("email");

                // Vous pouvez ajouter d'autres champs si nécessaire
                String isbn = resultSet.getString("isbn");

                // Initialise les variables title et author à null
                String title = "";
                String author = "";
                String year = "";

                if (!isbn.isEmpty()) {
                    String apiUrl = "https://gallica.bnf.fr/SRU?operation=searchRetrieve&version=1.2";
                    String encodedQuery = URLEncoder.encode(isbn, "UTF-8");
                    String searchQuery = "query=dc.identifier%20all%20" + encodedQuery;
                    String url = apiUrl + "&" + searchQuery;

                    HttpClient client = HttpClient.newHttpClient();
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(new URI(url))
                            .build();

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    String responseBody = response.body();

                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document doc = builder.parse(new InputSource(new StringReader(responseBody)));


                    NodeList titleNodes = doc.getElementsByTagName("dc:title");
                    NodeList yearNodes = doc.getElementsByTagName("dc:date");
                    if (titleNodes.getLength() > 0) {
                        Element titleElement = (Element) titleNodes.item(0);
                        title = titleElement.getTextContent();
                    }
                    // Vous pouvez ajouter d'autres champs si nécessaire
                    NodeList authorNodes = doc.getElementsByTagName("dc:creator");
                    if (authorNodes.getLength() > 0) {
                        Element authorElement = (Element) authorNodes.item(0);
                        author = authorElement.getTextContent();
                    }

                    if (yearNodes.getLength() > 0) {
                        Element yearElement = (Element) yearNodes.item(0);
                        year = yearElement.getTextContent();
                    }
                }
                int stock = resultSet.getInt("quantity_available");
                LocalDate borrowingDate = resultSet.getDate("loan_date").toLocalDate();
                LocalDate returnDate = resultSet.getDate("return_date").toLocalDate();

                Borrowing record = new Borrowing(isbn, memberMail,title,author,year, stock,borrowingDate,returnDate);
                borrowingData.add(record);
                // Met à jour le statut si la date de retour est dépassée
                if (returnDate.isBefore(LocalDate.now())) {
                    try (PreparedStatement updateStatement = connection.prepareStatement("UPDATE users SET member_in_good_standing = FALSE WHERE email = ?")) {
                        updateStatement.setString(1, memberMail);
                        updateStatement.executeUpdate();

                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        borrowingTableView.setItems(borrowingData);
    }

    /**
     * Sets up filtering for the borrowing data based on the input text.
     * Filters the borrowing records displayed in the table view according to the search criteria.
     */
    private void setupFiltering() {
        filteredData = new FilteredList<>(borrowingData, p -> true);
        borrowingTableView.setItems(filteredData);

        dataBook.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(record -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();
                return record.getTitle().toLowerCase().contains(lowerCaseFilter)
                        || record.getAuthor().toLowerCase().contains(lowerCaseFilter)
                        || record.getIsbn().toLowerCase().contains(lowerCaseFilter)
                        || record.getMemberMail().toLowerCase().contains(lowerCaseFilter)
                        || String.valueOf(record.getYear()).contains(lowerCaseFilter)
                        || String.valueOf(record.getStock()).contains(lowerCaseFilter)
                        || record.getBorrowingDate().toString().contains(lowerCaseFilter)
                        || record.getReturnDate().toString().contains(lowerCaseFilter);
            });
        });
    }

    /**
     * Loads the specified FXML view into the main container.
     * @param fxmlFileName the name of the FXML file to load
     */
    private void loadView(String fxmlFileName) {
        try {
            if (mainContainer == null) {
                System.err.println("Erreur : mainContainer has not been initialised correctly.");
                return;
            }

            // Load the specified FXML file
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
     * Loads the New Borrowing view.
     * This method is called when the "New borrow" button is clicked.
     */
    public void AddBorrowing() {
        loadView("CYBooks_NewBorrowing1.fxml");
    }

    /**
     * Loads the main home page.
     * This method is called when the "Return home" button is clicked.
     */
    public void returnMain() {
        loadView("CYBooks_Home.fxml");
    }

    /**
     * Loads the Return Borrowing view.
     * This method is called when the "Return borrow" button is clicked.
     */
    public void returnBorrow() {
        loadView("CYBooks_DeleteBorrowing.fxml");
    }
}
