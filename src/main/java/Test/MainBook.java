package Test;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MainBook extends Application {
    private TextField searchField;
    private VBox bookContainer;
    private Label pageInfo;
    private Button prevButton;
    private Button nextButton;

    private String currentQuery;
    private int currentPage;
    private int totalRecords;
    private final int recordsPerPage = 15;

    @Override
    public void start(Stage primaryStage) {
        // Création des éléments de l'interface utilisateur
        searchField = new TextField();
        searchField.setPromptText("Entrez le titre...");

        Button searchButton = new Button("Rechercher");
        searchButton.setOnAction(e -> {
            currentPage = 1;
            searchBooks();
        });

        prevButton = new Button("Précédent");
        prevButton.setOnAction(e -> {
            if (currentPage > 1) {
                currentPage--;
                searchBooks();
            }
        });

        nextButton = new Button("Suivant");
        nextButton.setOnAction(e -> {
            if (currentPage * recordsPerPage < totalRecords) {
                currentPage++;
                searchBooks();
            }
        });

        prevButton.setDisable(true);
        nextButton.setDisable(true);

        pageInfo = new Label();

        HBox navigation = new HBox(10, prevButton, pageInfo, nextButton);
        navigation.setAlignment(Pos.CENTER);
        navigation.setSpacing(10);

        bookContainer = new VBox();
        bookContainer.setSpacing(5);

        // Assemblage des éléments dans la disposition
        VBox topContainer = new VBox(10);
        topContainer.getChildren().addAll(new Label("Recherche de Livres"), searchField, searchButton, new Label("Résultats de la recherche :"));

        BorderPane root = new BorderPane();
        root.setTop(topContainer);
        root.setCenter(bookContainer);
        root.setBottom(navigation);

        BorderPane.setAlignment(prevButton, Pos.BOTTOM_LEFT);
        BorderPane.setAlignment(nextButton, Pos.BOTTOM_RIGHT);

        // Création de la scène et de la fenêtre
        Scene scene = new Scene(root, 400, 400);
        primaryStage.setTitle("Recherche de Livres");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void searchBooks() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            return;
        }

        if (!query.equals(currentQuery)) {
            currentQuery = query;
            currentPage = 1;
        }

        bookContainer.getChildren().clear();

        try {
            String apiUrl = "https://gallica.bnf.fr/SRU?operation=searchRetrieve&version=1.2";
            String encodedQuery = URLEncoder.encode("\"" + query + "%20Auteur%20du%20texte\"", "UTF-8");
            String searchQuery = "query=dc.title%20all%20" + encodedQuery;
            String url = apiUrl + "&" + searchQuery + "&startRecord=" + ((currentPage - 1) * recordsPerPage + 1) + "&maximumRecords=" + recordsPerPage;

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
            for (int i = 0; i < titleNodes.getLength(); i++) {
                Element titleElement = (Element) titleNodes.item(i);
                String title = titleElement.getTextContent();
                bookContainer.getChildren().add(new Label("Titre : " + title));
            }

            NodeList numberOfRecordsNodes = doc.getElementsByTagName("srw:numberOfRecords");
            if (numberOfRecordsNodes != null && numberOfRecordsNodes.getLength() > 0) {
                totalRecords = Integer.parseInt(numberOfRecordsNodes.item(0).getTextContent());
            } else {
                totalRecords = 0; // Initialiser totalRecords si aucun nombre n'est trouvé
            }

            pageInfo.setText("Page " + currentPage + " / " + ((totalRecords + recordsPerPage - 1) / recordsPerPage));
            prevButton.setDisable(currentPage == 1);
            nextButton.setDisable(currentPage * recordsPerPage >= totalRecords);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}