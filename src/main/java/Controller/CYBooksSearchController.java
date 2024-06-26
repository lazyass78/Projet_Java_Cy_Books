package Controller;



import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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

public class CYBooksSearchController {
    @FXML
    private Button homeButton;
    @FXML
    private Button moreButton;
    @FXML
    private TableColumn<Model.Document, Void> borrowColumn;
    @FXML
    private TableColumn<Model.Document, Integer> yearColumn;
    @FXML
    private TableColumn<Model.Document, String> authorColumn;
    @FXML
    private TableColumn<Model.Document, String> titleColumn;
    @FXML
    private TableColumn<Model.Document, Integer> idColumn;
    @FXML
    private TableView<Model.Document> documentTable;
    @FXML
    private BorderPane mainContainer;
    @FXML
    private TextField authorField;
    @FXML
    private TextField yearField;
    @FXML
    private TextField languageField;
    @FXML
    private TextField titleField;
    @FXML
    private VBox bookContainer;
    @FXML
    private Label pageInfo;

    private ObservableList<Model.Document> bookData = FXCollections.observableArrayList();

    private String currentQuery;
    private int currentPage;
    private int totalRecords;
    private final int recordsPerPage = 20;

    /**
     * Initializes the controller.
     * This method is called when the FXML file is loaded. It sets up the table columns,configures the Borrow button column in the TableView, and performs an initial book search.
     */
    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        // Configure the borrowColumn with buttons
        borrowColumn.setCellFactory(param -> new TableCell<Model.Document, Void>() {
            private final Button borrowButton = new Button("Borrow");

            {
                borrowButton.setOnAction(event -> {
                    Model.Document document = getTableView().getItems().get(getIndex());
                    String isbn = document.getId();
                    loadNewBorrowingView(isbn);
                });
            }
            {
                moreButton.setOnAction(event -> {
                    loadMoreBooks();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(borrowButton);
                }
            }
        });
        searchBooks();
    }

    /**
     * Loads the new borrowing view based on the selected document's ISBN.
     *
     * @param isbn the ISBN of the document to borrow.
     */
    @FXML private void loadNewBorrowingView(String isbn) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CYBooks_NewBorrowing2.fxml"));
            Parent view = loader.load();

            // Recover the target controller
            CYBooksNewBorrowing2Controller controller = loader.getController();

            // Passing data to the target controller
            controller.setDocumentIsbn(isbn);

            // Replace the current content of the main container with the content of the new view
            mainContainer.getChildren().clear();
            mainContainer.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Handles the search operation based on user input.
     */
    @FXML
    private void handleSearch() {
        bookData.clear();
        currentPage = 1;
        searchBooks();
    }

    /**
     * Loads more books for pagination.
     */
    @FXML
    private void loadMoreBooks() {
        if (currentPage * recordsPerPage < totalRecords) {
            currentPage++;
            searchBooks();
        }
    }

    /**
     * Loads the specified FXML view into the main container.
     *
     * @param fxmlFileName the file name of the FXML view to load.
     */
    @FXML private void loadView(String fxmlFileName) {
        try {
            if (mainContainer == null) {
                System.err.println("Erreur : mainContainer n'a pas été correctement initialisé.");
                return;
            }

            // Loads the FXML file for the specified view
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFileName));
            Parent view = fxmlLoader.load();

            // Replace the current content of the main container with the content of the new view
            mainContainer.getChildren().clear();
            mainContainer.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Performs a search for books based on user input criteria.
     */
    private void searchBooks() {
        String author = authorField.getText().trim();
        String year = yearField.getText().trim();
        String language = languageField.getText().trim();
        String title = titleField.getText().trim();

        if (author.isEmpty() && year.isEmpty() && language.isEmpty() && title.isEmpty()) {
            return;
        }

        if (!author.equals(currentQuery)) {
            currentQuery = author;
            currentPage = 1;
        }


        try {
            String apiUrl = "https://gallica.bnf.fr/SRU?operation=searchRetrieve&version=1.2&query=";
            String searchQuery = "";

            String encodedQuery = URLEncoder.encode("\"" + author + "%20Auteur%20du%20texte\"", "UTF-8");
            searchQuery = "dc.creator%20all%20" + encodedQuery;

            if (!year.isEmpty()) {
                searchQuery += "%20and%20dc.date%20all%20" + URLEncoder.encode("\"" + year + "\"", "UTF-8");
            }

            if (!language.isEmpty()) {
                searchQuery += "%20and%20dc.language%20any%20" + URLEncoder.encode("\"" + language + "\"", "UTF-8");
            }

            if (!title.isEmpty()) {
                searchQuery += "%20and%20dc.title%20all%20" + URLEncoder.encode("\"" + title + "\"", "UTF-8");
            }

            String url = apiUrl + searchQuery + "&startRecord=" + ((currentPage - 1) * recordsPerPage + 1) + "&maximumRecords=" + recordsPerPage;
            System.out.println(url);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(responseBody)));

            NodeList idNodes = doc.getElementsByTagName("uri");
            NodeList identifierNodes = doc.getElementsByTagName("dc:identifier");
            int i = 0;
            while (i < idNodes.getLength()) {
                Element identifierElement = (Element) identifierNodes.item(i);
                String identifiers = identifierElement.getTextContent();

                Element idElement = (Element) idNodes.item(i);
                String ids = idElement.getTextContent();
                int i1 = i + 1;

                while (!identifiers.contains(ids)) {
                    identifierElement = (Element) identifierNodes.item(i1);
                    identifiers = identifierElement.getTextContent();
                    i1++;
                }
                String titles = "";
                String authors = "";
                String years = "";


                String url1 = apiUrl + "dc.identifier%20all%20" + URLEncoder.encode(identifiers, "UTF-8");
                System.out.println(url1);
                HttpClient client1 = HttpClient.newHttpClient();
                HttpRequest request1 = HttpRequest.newBuilder()
                        .uri(new URI(url1))
                        .build();

                HttpResponse<String> response1 = client1.send(request1, HttpResponse.BodyHandlers.ofString());
                String responseBody1 = response1.body();

                DocumentBuilderFactory factory1 = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder1 = factory1.newDocumentBuilder();
                Document doc1 = builder1.parse(new InputSource(new StringReader(responseBody1)));

                NodeList titleNodes = doc1.getElementsByTagName("dc:title");
                if (titleNodes.getLength() > 0) {
                    Element titleElement = (Element) titleNodes.item(0);
                    titles = titleElement.getTextContent();
                }

                NodeList authorNodes = doc1.getElementsByTagName("dc:creator");
                if (authorNodes.getLength() > 0) {
                    Element authorElement = (Element) authorNodes.item(0);
                    authors = authorElement.getTextContent();
                }

                NodeList yearNodes = doc1.getElementsByTagName("dc:date");
                if (yearNodes.getLength() > 0) {
                    Element yearElement = (Element) yearNodes.item(0);
                    years = yearElement.getTextContent();
                }

                HBox bookItem = new HBox(10);
                bookItem.setAlignment(Pos.CENTER_LEFT);
                Model.Document document = new Model.Document(titles, authors, years, ids);
                bookData.add(document);
                documentTable.setItems(bookData);
                i++;
            }
            NodeList numberOfRecordsNodes = doc.getElementsByTagName("srw:numberOfRecords");
            if (numberOfRecordsNodes != null && numberOfRecordsNodes.getLength() > 0) {
                totalRecords = Integer.parseInt(numberOfRecordsNodes.item(0).getTextContent());
                System.out.println(totalRecords);
            } else {
                totalRecords = 0; // Initialize totalRecords if no number is found
            }
            moreButton.setDisable(currentPage * recordsPerPage >= totalRecords);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML private void handleHome(){
        loadView("CYBooks_Home.fxml");
    }
}

