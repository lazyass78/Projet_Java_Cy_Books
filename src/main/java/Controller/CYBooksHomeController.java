package Controller;

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

    @FXML private Button NewBorrowing;

    @FXML private Button SaveBorrowing;
    @FXML private Button CancelBorrowing;



    public void Page_borrowing() {
        loadView("CYBooks_Borrowing.fxml");
    }

    public void Page_member(){
        loadView("CYBooks_Member.fxml");
    }

    public void Page_document(){
        loadView("CYBooks_Document.fxml");
    }

    public void AddBorrowing(){
        loadView("CYBooks_NewBorrowing.fxml");
    }

    public void SaveBorrowing(){
        loadView("CYBooks_Borrowing.fxml");
    }

    public void CancelBorrowing(){
        loadView("CYBooks_Borrowing.fxml");
    }

    private void loadView(String fxmlFileName) {
        try {
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
}
