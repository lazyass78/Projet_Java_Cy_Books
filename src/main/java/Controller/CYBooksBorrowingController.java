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

    @FXML private TextField dataBook;
    private ObservableList<CYBooksBorrowingRecord> borrowingData = FXCollections.observableArrayList();
    private FilteredList<CYBooksBorrowingRecord> filteredData;


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
        setupFiltering();
    }

    private void loadBorrowingData() {
        try (Connection connection = DatabaseUtil.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM books")) {

            while (resultSet.next()) {
                String memberId = resultSet.getString("user_id");

                // Vous pouvez ajouter d'autres champs si nécessaire
                String isbn = resultSet.getString("isbn");


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
                Element titleElement = (Element) titleNodes.item(0);
                String title = titleElement.getTextContent();
                // Vous pouvez ajouter d'autres champs si nécessaire
                NodeList authorNodes = doc.getElementsByTagName("dc:creator");
                Element authorElement = (Element) authorNodes.item(0);
                String author = authorElement.getTextContent();
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
                        || record.getMemberId().toLowerCase().contains(lowerCaseFilter);
            });
        });
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
        loadView("MainAuthor.fxml");
    }

    public void returnMain() {
        loadView("CYBooks_Home.fxml");
    }

}
