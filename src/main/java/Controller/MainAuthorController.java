package Controller;



import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
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

public class MainAuthorController {
    @FXML
    private BorderPane mainContainer;
    @FXML
    private TextField authorField;
    @FXML
    private TextField dateField;
    @FXML
    private TextField languageField;
    @FXML
    private TextField titleField;
    @FXML
    private VBox bookContainer;
    @FXML
    private Label pageInfo;
    @FXML
    private Button prevButton;
    @FXML
    private Button nextButton;

    private Button borrowButton;
    private String currentQuery;
    private int currentPage;
    private int totalRecords;
    private final int recordsPerPage = 15;

    @FXML
    private void handleSearch() {
        currentPage = 1;
        searchBooks();
    }

    @FXML
    private void handlePrev() {
        if (currentPage > 1) {
            currentPage--;
            searchBooks();
        }
    }

    @FXML
    private void handleNext() {
        if (currentPage * recordsPerPage < totalRecords) {
            currentPage++;
            searchBooks();
        }
    }

    @FXML
    private void loadView(String fxmlFileName) {
        try {
            if (mainContainer == null) {
                System.err.println("Erreur : mainContainer n'a pas été correctement initialisé.");
                return;
            }

            // Charge le fichier FXML de la vue spécifiée
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFileName));
            BorderPane view = fxmlLoader.load(); // Charger en tant qu'AnchorPane

            // Remplace le contenu actuel du conteneur principal par le contenu de la nouvelle vue
            mainContainer.setCenter(view); // Ajouter l'AnchorPane au centre du BorderPane existant
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void searchBooks() {
        String author = authorField.getText().trim();
        String date = dateField.getText().trim();
        String language = languageField.getText().trim();
        String title = titleField.getText().trim();

        if (author.isEmpty() && date.isEmpty() && language.isEmpty() && title.isEmpty()) {
            return;
        }

        if (!author.equals(currentQuery)) {
            currentQuery = author;
            currentPage = 1;
        }

        bookContainer.getChildren().clear();

        try {
            String apiUrl = "https://gallica.bnf.fr/SRU?operation=searchRetrieve&version=1.2&query=";
            String searchQuery = "";

            String encodedQuery = URLEncoder.encode("\"" + author + "%20Auteur%20du%20texte\"", "UTF-8");
            searchQuery = "dc.creator%20all%20" + encodedQuery;

            if (!date.isEmpty()) {
                searchQuery += "%20and%20dc.date%20all%20" + URLEncoder.encode("\"" + date + "\"", "UTF-8");
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

            NodeList titleNodes = doc.getElementsByTagName("dc:title");
            NodeList idNodes = doc.getElementsByTagName("uri");
            for (int i = 0; i < titleNodes.getLength(); i++) {
                Element titleElement = (Element) titleNodes.item(i);
                String titles = titleElement.getTextContent();

                Element idElement = (Element) idNodes.item(i);
                String id = idElement.getTextContent();

                HBox bookItem = new HBox(10);
                bookItem.setAlignment(Pos.CENTER_LEFT);
                Label idLabel = new Label("Id : " + id);
                Label titleLabel = new Label("Title : " + titles);
                borrowButton = new Button("Borrow");
                borrowButton.setOnAction(e -> {
                    System.out.println("Borrowed: " + titles);
                    loadView("CYBooks_NewBorrowing");
                });
                bookItem.getChildren().addAll(idLabel, titleLabel, borrowButton);
                bookContainer.getChildren().add(bookItem);
            }

            NodeList numberOfRecordsNodes = doc.getElementsByTagName("srw:numberOfRecords");
            if (numberOfRecordsNodes != null && numberOfRecordsNodes.getLength() > 0) {
                totalRecords = Integer.parseInt(numberOfRecordsNodes.item(0).getTextContent());
            } else {
                totalRecords = 0; // Initialize totalRecords if no number is found
            }

            pageInfo.setText("Page " + currentPage + " / " + ((totalRecords + recordsPerPage - 1) / recordsPerPage));
            prevButton.setDisable(currentPage == 1);
            nextButton.setDisable(currentPage * recordsPerPage >= totalRecords);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

