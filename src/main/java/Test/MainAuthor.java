package Test;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
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

public class MainAuthor extends Application {
    private TextField authorField;
    private VBox bookContainer;
    private Label pageInfo;
    private Button prevButton;
    private Button nextButton;

    private TextField dateField;
    private TextField languageField;
    private TextField titleField;

    private String currentQuery;
    private int currentPage;
    private int totalRecords;
    private final int recordsPerPage = 15;

    @Override
    public void start(Stage primaryStage) {
        // Create UI elements
        authorField = new TextField();
        authorField.setPromptText("Enter author's name...");

        dateField = new TextField();
        dateField.setPromptText("Publication date");

        languageField = new TextField();
        languageField.setPromptText("Language");

        titleField = new TextField();
        titleField.setPromptText("Enter book title...");

        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> {
            currentPage = 1;
            searchBooks();
        });

        prevButton = new Button("Previous");
        prevButton.setOnAction(e -> {
            if (currentPage > 1) {
                currentPage--;
                searchBooks();
            }
        });

        nextButton = new Button("Next");
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

        // Assemble UI elements in layout
        VBox topContainer = new VBox(10);
        topContainer.getChildren().addAll(
                new Label("Book Search"),
                authorField,
                dateField,
                languageField,
                titleField,
                searchButton,
                new Label("Search Results:")
        );

        BorderPane root = new BorderPane();
        root.setTop(topContainer);
        root.setCenter(bookContainer);
        root.setBottom(navigation);

        BorderPane.setAlignment(prevButton, Pos.BOTTOM_LEFT);
        BorderPane.setAlignment(nextButton, Pos.BOTTOM_RIGHT);

        // Create the scene and the window
        Scene scene = new Scene(root, 400, 400);
        primaryStage.setTitle("Book Search");
        primaryStage.setScene(scene);
        primaryStage.show();
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
                if (!searchQuery.equals("")){
                    searchQuery += "%20and%20dc.date%20all" + URLEncoder.encode("\"" + date + "\"", "UTF-8");
                }
                else{
                    searchQuery += "%20dc.date%20all" + URLEncoder.encode("\"" + date + "\"", "UTF-8");
                }
            }

            if (!language.isEmpty()) {
                if (!searchQuery.equals("")){
                    searchQuery += "%20and%20dc.language%20any" + URLEncoder.encode("\"" + language + "\"", "UTF-8");
                }
                else{
                    searchQuery += "%20dc.language%20any" + URLEncoder.encode("\"" + language + "\"", "UTF-8");
                }
            }

            if (!title.isEmpty()) {
                if (!searchQuery.equals("")){
                    searchQuery += "%20and%20dc.title%20all" + URLEncoder.encode("\"" + title + "\"", "UTF-8");
                }
                else{
                    searchQuery += "%20dc.title%20all" + URLEncoder.encode("\"" + title + "\"", "UTF-8");
                }
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
            for (int i = 0; i < titleNodes.getLength(); i++) {
                Element titleElement = (Element) titleNodes.item(i);
                String titles = titleElement.getTextContent();

                HBox bookItem = new HBox(10);
                bookItem.setAlignment(Pos.CENTER_LEFT);
                Label titleLabel = new Label("Title: " + titles);
                Button borrowButton = new Button("Borrow");
                borrowButton.setOnAction(e -> {
                    // Handle borrow action here
                    System.out.println("Borrowed: " + titles);
                });
                bookItem.getChildren().addAll(titleLabel, borrowButton);
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

    public static void main(String[] args) {
        launch(args);
    }
}




