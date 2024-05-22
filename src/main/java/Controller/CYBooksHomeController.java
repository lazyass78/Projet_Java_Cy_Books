package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class CYBooksHomeController {

    @FXML private AnchorPane mainContainer;

    @FXML private Button Borrowing;
    @FXML private Button Member;
    @FXML private Button Document;


    @FXML private Button SaveBorrowing;
    @FXML private Button CancelBorrowing;


    @FXML private Button Search;
    @FXML private Button BackHomePage2;


    @FXML public void Page_borrowing() {
        loadView("CYBooks_Borrowing.fxml");
    }

    @FXML public void Page_member(){
        loadView("CYBooks_Member.fxml");
    }

    @FXML public void Page_document(){
        loadView("MainAuthor.fxml");
    }


    @FXML public void SaveBorrowing(){
        loadView("CYBooks_Borrowing.fxml");
    }

    @FXML public void CancelBorrowing(){
        loadView("CYBooks_Borrowing.fxml");
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


    public void SearchMember() {
    }

    public void returnMain() {
        loadView("CYBooks_Home.fxml");
    }
}
