package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class CYBooksHomeController {

    /** Here we have made ID on SceneBuilder items like "button", and we make attributes that link with these items
     *  we have to put @FXML to indicate that it's a reference to objects that have received ID in FXML view */

    /** AnchorPane for holding the main content */
    @FXML private AnchorPane mainContainer;

    /** Buttons for different functionalities */
    @FXML private Button Borrowing;
    @FXML private Button Member;
    @FXML private Button Document;

    @FXML private Button NewBorrowing;

    @FXML private Button SaveBorrowing;
    @FXML private Button CancelBorrowing;

    @FXML private Button BackHomePage;

    @FXML private Button Search;
    @FXML private Button BackHomePage2;

    /**
     * Loads the "CYBooks_Borrowing.fxml" view when the "Borrowing" button is clicked.
     */
    @FXML public void Page_borrowing() {
        loadView("CYBooks_Borrowing.fxml");
    }

    /**
     * Loads the "CYBooks_Member.fxml" view when the "Member" button is clicked.
     */
    @FXML public void Page_member(){
        loadView("CYBooks_Member.fxml");
    }

    /**
     * Loads the "CYBooks_Document.fxml" view when the "Document" button is clicked.
     */
    @FXML public void Page_document(){
        loadView("CYBooks_Document.fxml");
    }

    /**
     * Loads the "CYBooks_NewBorrowing.fxml" view when the "New Borrowing" button is clicked.
     */
    @FXML public void AddBorrowing(){
        loadView("CYBooks_NewBorrowing.fxml");
    }

    /**
     * Loads the "CYBooks_Borrowing.fxml" view when the "Save Borrowing" button is clicked.
     */
    @FXML public void SaveBorrowing(){
        loadView("CYBooks_Borrowing.fxml");
    }

    /**
     * Loads the "CYBooks_Borrowing.fxml" view when the "Cancel Borrowing" button is clicked.
     */
    @FXML public void CancelBorrowing(){
        loadView("CYBooks_Borrowing.fxml");
    }

    /**
     * Loads the specified FXML view into the main container.
     * @param fxmlFileName The file name of the FXML view to load.
     */
    @FXML private void loadView(String fxmlFileName) {
        try {
            // check if mainContainer which is the AnchorPane (All the screen of the app) is setted and existed otherwise print error
            if (mainContainer == null) {
                System.err.println("Erreur : mainContainer n'a pas été correctement initialisé.");
                return;
            }

            // Load the FXML file of the specified view
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFileName));  // fxmlFileName is the file that will be printed
            Parent view = fxmlLoader.load();

            // Replace the current content of the main container with the content of the new view
            mainContainer.getChildren().clear();  // clear = erase the actual view
            mainContainer.getChildren().add(view); // replace
        } catch (IOException e) {  // catch error
            e.printStackTrace();
        }
    }

    /**
     * method for searching members.
     */
    public void SearchMember() {
    }

    /**
     * Loads the "CYBooks_Home.fxml" view when the "BackHomePage/2" buttons are clicked.
     */
    public void returnMain() {
        loadView("CYBooks_Home.fxml");
    }
}
