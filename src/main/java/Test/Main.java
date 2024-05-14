package Test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Main extends Application {
    private TextField searchField;
    private VBox bookContainer;

    @Override
    public void start(Stage primaryStage) {
        // Création des éléments de l'interface utilisateur
        searchField = new TextField();
        searchField.setPromptText("Entrez le nom de l'auteur...");

        Button searchButton = new Button("Rechercher");
        searchButton.setOnAction(e -> searchBooks());

        bookContainer = new VBox();

        // Assemblage des éléments dans la disposition
        VBox root = new VBox(10);
        root.getChildren().addAll(new Label("Recherche de Livres"), searchField, searchButton, new Label("Résultats de la recherche :"), bookContainer);

        // Création de la scène et de la fenêtre
        Scene scene = new Scene(root, 400, 400);
        primaryStage.setTitle("Recherche de Livres");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void searchBooks() {
        String query = searchField.getText().trim(); // Obtenir le texte de recherche
        if (query.isEmpty()) {
            return; // Ne rien faire si le champ de recherche est vide
        }

        bookContainer.getChildren().clear(); // Effacer les résultats précédents

        try {
            // URL de l'API de la BNF
            String apiUrl = "https://catalogue.bnf.fr/api/SRU?version=1.2&operation=searchRetrieve";

            // Encodage du texte de recherche pour gérer les espaces
            String encodedQuery = URLEncoder.encode(query, "UTF-8");

            // Requête de recherche avec le critère spécifié par l'utilisateur
            String searchQuery = "query=bib.author%20all%20%22" + encodedQuery + "%22%20and%20bib.unimarc:doctype%20all%20%22am%22";

            // Création de l'URL de requête en concaténant l'URL de base avec la requête de recherche
            URL url = new URL(apiUrl + "&" + searchQuery);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Récupérer la réponse de l'API
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Charger les données XML dans un Document
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(url.openStream());

            // Extraire les titres des livres et les afficher dans la fenêtre
            NodeList records = document.getElementsByTagName("srw:record");
            for (int i = 0; i < records.getLength(); i++) {
                Element record = (Element) records.item(i);
                NodeList datafieldList = record.getElementsByTagName("mxc:datafield");
                for (int j = 0; j < datafieldList.getLength(); j++) {
                    Element datafield = (Element) datafieldList.item(j);
                    if (datafield.getAttribute("tag").equals("200")) {
                        NodeList subfieldList = datafield.getElementsByTagName("mxc:subfield");
                        for (int k = 0; k < subfieldList.getLength(); k++) {
                            Element subfield = (Element) subfieldList.item(k);
                            if (subfield.getAttribute("code").equals("a")) {
                                String title = subfield.getTextContent();
                                bookContainer.getChildren().add(new Label("Titre : " + title));
                            }
                        }
                    }
                }
            }

            // Fermer la connexion
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
