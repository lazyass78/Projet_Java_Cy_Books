package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;

/**
 * Controller class for managing the main home view of the CYBooks application.
 * This controller handles navigation between different pages of the application.
 */
public class CYBooksHomeController {

    @FXML private AnchorPane mainContainer;

    @FXML private Button Borrowing;
    @FXML private Button Member;
    @FXML private Button Document;
    @FXML private Button Statistics;


    /**
     * Loads the borrowing management page when the "Borrowing" button is pressed.
     */
    @FXML public void Page_borrowing() {
        loadView("CYBooks_Borrowing.fxml");
    }

    /**
     * Loads the member management page when the "Member" button is pressed.
     */
    @FXML public void Page_member(){
        loadView("CYBooks_Member.fxml");
    }

    /**
     * Loads the document management page when the "Document" button is pressed.
     */
    @FXML public void Page_document(){
        loadView("CYBooks_Search.fxml");
    }

    /**
     * Loads the statistics page when the "Statistics" button is pressed.
     */
    @FXML public void Page_statistics(){
        loadView("CYBooks_Statistics.fxml");
    }


    /**
     * Loads the specified FXML view into the main container.
     *
     * @param fxmlFileName the name of the FXML file to load.
     */
    @FXML private void loadView(String fxmlFileName) {
        try {
            if (mainContainer == null) {
                System.err.println("Error: mainContainer has not been properly initialized.");
                return;
            }

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFileName));
            Parent view = fxmlLoader.load();

            mainContainer.getChildren().clear();
            mainContainer.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
