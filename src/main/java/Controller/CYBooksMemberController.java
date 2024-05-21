package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class CYBooksMemberController {
    @FXML private AnchorPane mainContainer;

    @FXML private Button BackHomePage2;
    @FXML private Button Search;
    @FXML private Button Add;
    @FXML private Button Delete;

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

    public void SearchMember(ActionEvent actionEvent) {
        // API tt ça
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
